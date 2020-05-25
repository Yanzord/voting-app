package com.yanzord.votingsessionservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yanzord.votingsessionservice.exception.ClosedSessionException;
import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.exception.OpenedSessionException;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.model.SessionStatus;
import com.yanzord.votingsessionservice.model.Vote;
import com.yanzord.votingsessionservice.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    @HystrixCommand(
            fallbackMethod = "defaultOpenSession",
            ignoreExceptions = { OpenedSessionException.class, ClosedSessionException.class })
    public Session openSession(Session session) throws OpenedSessionException, ClosedSessionException {
        Optional<Session> optionalSession = sessionRepository.getSessionByAgendaId(session.getAgendaId());

        if(optionalSession.isPresent()) {
            switch(optionalSession.get().getStatus()) {
                case OPENED: {
                    throw new OpenedSessionException("Session is already opened. Session ID: " + session.getId());
                }
                case CLOSED: {
                    throw new ClosedSessionException("Session is closed. Session ID: " + session.getId());
                }
            }
        }

        LocalDateTime endDate = session.getStartDate().plusMinutes(session.getDuration());
        session.setEndDate(endDate);
        session.setStatus(SessionStatus.OPENED);

        return sessionRepository.save(session);
    }

    @HystrixCommand(
            fallbackMethod = "defaultRegisterVote",
            ignoreExceptions = { SessionNotFoundException.class })
    public Session registerVote(Vote vote, String agendaId) throws SessionNotFoundException {
        Session session = sessionRepository.getSessionByAgendaId(agendaId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found for Agenda ID: " + agendaId));

        if(LocalDateTime.now().isAfter(session.getEndDate())) {
            session.setStatus(SessionStatus.CLOSED);
            return sessionRepository.save(session);
        }

        List<Vote> votes = Optional.ofNullable(session.getVotes())
                .orElse(new ArrayList<>());

        votes.add(vote);
        session.setVotes(votes);
        return sessionRepository.save(session);
    }

    public Session defaultOpenSession(Session session) {
        return new Session();
    }

    public Session defaultRegisterVote(Vote vote, String sessionId) {
        return new Session();
    }
}
