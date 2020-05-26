package com.yanzord.votingappservice.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yanzord.votingappservice.model.*;
import com.yanzord.votingappservice.exception.*;
import com.yanzord.votingappservice.feign.CPFValidator;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import com.yanzord.votingappservice.feign.VotingSessionClient;
import feign.FeignException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppService {
    private static final Logger logger = Logger.getLogger(AppService.class);
    private final Session DEFAULT_SESSION = new Session("1", "1", 1, SessionStatus.CLOSED);

    @Autowired
    private VotingAgendaClient votingAgendaClient;

    @Autowired
    private VotingSessionClient votingSessionClient;

    @Autowired
    private CPFValidator cpfValidator;


    @HystrixCommand(fallbackMethod = "defaultRegisterAgenda")
    public Agenda registerAgenda(Agenda agenda) {
        agenda.setStatus(AgendaStatus.NEW);
        return votingAgendaClient.saveAgenda(agenda);
    }

    @HystrixCommand(
            fallbackMethod = "defaultCreateSession",
            ignoreExceptions = { CreatedSessionException.class, FeignException.NotFound.class })
    public Session createSession(Session session) throws CreatedSessionException {
        votingAgendaClient.getAgendaById(session.getAgendaId());

        Session sessionFound;
        try {
            sessionFound = votingSessionClient.getSessionByAgendaId(session.getAgendaId());
        } catch (FeignException.NotFound e) {
            LocalDateTime startDate = LocalDateTime.now();
            session.setStartDate(startDate);

            LocalDateTime endDate = session.getStartDate().plusMinutes(session.getDuration());
            session.setEndDate(endDate);
            session.setStatus(SessionStatus.OPENED);

            if (session.getDuration() == 0)
                session.setDuration(1);

            return votingSessionClient.saveSession(session);
        }

        logger.debug("Tried to create session that already exists. " +
                "Session ID: " + sessionFound.getId() + "; Agenda ID: " + sessionFound.getAgendaId());
        throw new CreatedSessionException();
    }

    @HystrixCommand(
            fallbackMethod = "defaultRegisterVote",
            ignoreExceptions = { VoteException.class, ClosedSessionException.class,
                    InvalidCpfException.class, FeignException.NotFound.class })
    public Session registerVote(Vote vote, String agendaId) throws VoteException, ClosedSessionException, InvalidCpfException {
        Session session = votingSessionClient.getSessionByAgendaId(agendaId);

        if (LocalDateTime.now().isAfter(session.getEndDate())) {
            closeSession(session);
            Agenda agenda = votingAgendaClient.getAgendaById(agendaId);
            finishAgenda(agenda, generateAgendaResult(session));

            logger.debug("Tried to vote in closed session. " +
                    "Session ID: " + session.getId() + "; Agenda ID: " + session.getAgendaId());

            throw new ClosedSessionException();
        }

        List<Vote> votes = Optional.ofNullable(session.getVotes())
                .orElse(new ArrayList<>());

        if (votes.stream().anyMatch(v -> v.getAssociateId().equals(vote.getAssociateId()) || v.getAssociateCPF().equals(vote.getAssociateCPF()))) {
            logger.debug("Associate ID or CPF already voted. " +
                    "Session ID: " + session.getId() + "; Agenda ID: " + session.getAgendaId() +
                    "; associate ID: " + vote.getAssociateId() + "; associate CPF: " + vote.getAssociateCPF());

            throw new VoteException();
        }

        try {
            ObjectNode json = cpfValidator.validateCPF(vote.getAssociateCPF());

            VoteStatus status = VoteStatus.valueOf(json.get("status").asText());

            if (status.equals(VoteStatus.UNABLE_TO_VOTE)) {
                logger.debug("CPF unable to vote. " + "Session ID: " + session.getId() +
                        "; Agenda ID: " + session.getAgendaId() + "; associate CPF: " + vote.getAssociateCPF());

                throw new InvalidCpfException("Associate is unable to vote.");
            }

            votes.add(vote);
            session.setVotes(votes);

            return votingSessionClient.saveSession(session);
        } catch (FeignException.NotFound e) {
            logger.debug("Invalid CPF. " + "Session ID: " + session.getId() +
                    "; Agenda ID: " + session.getAgendaId() + "; associate CPF: " + vote.getAssociateCPF());

            throw new InvalidCpfException("Invalid CPF.");
        }
    }

    @HystrixCommand(
            fallbackMethod = "defaultAgendaResult",
            ignoreExceptions = { NewAgendaException.class, FeignException.NotFound.class })
    public AgendaResult getAgendaResult(String agendaId) throws NewAgendaException {
        Agenda agenda = votingAgendaClient.getAgendaById(agendaId);

        if (agenda.getAgendaResult() == null) {
            if (agenda.getStatus().equals(AgendaStatus.NEW)) {
                Session session = votingSessionClient.getSessionByAgendaId(agendaId);

                if (session.getStatus().equals(SessionStatus.CLOSED)) {
                    return finishAgenda(agenda, generateAgendaResult(session));
                }

                if (LocalDateTime.now().isAfter(session.getEndDate())) {
                    closeSession(session);
                    return finishAgenda(agenda, generateAgendaResult(session));
                }

                logger.debug("Session for agenda is still open, requested results will not be show. Agenda ID: " + agendaId);
                throw new NewAgendaException("Agenda is new and session is still opened.");
            }
        }

        return agenda.getAgendaResult();
    }

    public AgendaResult generateAgendaResult(Session session) {
        List<Vote> votes = Optional.ofNullable(session.getVotes())
                .orElse(new ArrayList<>());

        long totalUpVotes = votes.stream()
                .filter(vote -> vote.getVoteChoice().equals(VoteChoice.SIM))
                .count();

        long totalDownVotes = votes.stream()
                .filter(vote -> vote.getVoteChoice().equals(VoteChoice.NAO))
                .count();

        if (totalUpVotes > totalDownVotes) {
            String result = VoteChoice.SIM.toString();

            return new AgendaResult(totalUpVotes, totalDownVotes, result);
        }

        if (totalDownVotes > totalUpVotes) {
            String result = VoteChoice.NAO.toString();

            return new AgendaResult(totalUpVotes, totalDownVotes, result);
        }

        String result = "EMPATE";

        return new AgendaResult(totalUpVotes, totalDownVotes, result);
    }

    public AgendaResult finishAgenda(Agenda agenda, AgendaResult agendaResult) {
        agenda.setAgendaResult(agendaResult);
        agenda.setStatus(AgendaStatus.FINISHED);
        votingAgendaClient.saveAgenda(agenda);

        return agendaResult;
    }

    public void closeSession(Session session) {
        session.setStatus(SessionStatus.CLOSED);
        votingSessionClient.saveSession(session);
    }

    public Agenda defaultRegisterAgenda(Agenda agenda) {
        return new Agenda("1", "This is a default agenda.", AgendaStatus.FINISHED);
    }

    public Session defaultCreateSession(Session session) {
        return DEFAULT_SESSION;
    }

    public Session defaultRegisterVote(Vote vote, String agendaId) {
        return DEFAULT_SESSION;
    }

    public AgendaResult defaultAgendaResult(String agendaId) {
        return new AgendaResult(0, 0, "DEFAULT");
    }
}
