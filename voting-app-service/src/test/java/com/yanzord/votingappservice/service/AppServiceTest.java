package com.yanzord.votingappservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yanzord.votingappservice.exception.*;
import com.yanzord.votingappservice.feign.CPFValidator;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import com.yanzord.votingappservice.feign.VotingSessionClient;
import com.yanzord.votingappservice.model.*;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AppServiceTest {
    @Autowired
    private ObjectMapper mapper;

    @Mock
    private VotingSessionClient votingSessionClient;

    @Mock
    private VotingAgendaClient votingAgendaClient;

    @Mock
    private CPFValidator cpfValidator;

    @InjectMocks
    private AppService appService;

    @Test
    public void shouldRegisterAgenda() {
        Agenda expected = new Agenda();
        expected.setId("1");
        expected.setDescription("New agenda.");

        Mockito.when(votingAgendaClient.saveAgenda(expected)).thenReturn(expected);

        Agenda actual = appService.registerAgenda(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(AgendaStatus.NEW, actual.getStatus());
    }

    @Test
    public void shouldCreateSession() throws CreatedSessionException {
        String sessionId = "2";
        String agendaId = "1";

        Agenda agenda = new Agenda(agendaId, "New agenda", AgendaStatus.NEW);

        Session session = new Session(agendaId, 2);
        session.setId("2");

        Session expected = new Session(sessionId, agendaId, 2, SessionStatus.OPENED);

        Mockito.when(votingAgendaClient.getAgendaById(session.getAgendaId())).thenReturn(agenda);
        Mockito.when(votingSessionClient.getSessionByAgendaId(session.getAgendaId()))
                .thenThrow(FeignException.NotFound.class);

        Mockito.when(votingSessionClient.saveSession(session)).thenReturn(session);

        Session actual = appService.createSession(session);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldCreateSessionWithDefaultDuration() throws CreatedSessionException {
        String sessionId = "2";
        String agendaId = "1";

        Agenda agenda = new Agenda(agendaId, "New agenda", AgendaStatus.NEW);

        Session session = new Session();
        session.setId("2");
        session.setAgendaId(agendaId);

        Session expected = new Session(sessionId, agendaId, 1, SessionStatus.OPENED);

        Mockito.when(votingAgendaClient.getAgendaById(session.getAgendaId())).thenReturn(agenda);
        Mockito.when(votingSessionClient.getSessionByAgendaId(session.getAgendaId()))
                .thenThrow(FeignException.NotFound.class);

        Mockito.when(votingSessionClient.saveSession(session)).thenReturn(session);

        Session actual = appService.createSession(session);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldThrowExceptionWhenCreatingSessionWithInvalidAgendaId() {
        Session session = new Session("1", 2);

        Mockito.when(votingAgendaClient.getAgendaById(session.getAgendaId()))
                .thenThrow(FeignException.NotFound.class);

        assertThrows(FeignException.NotFound.class, () -> appService.createSession(session));
    }

    @Test
    public void shouldThrowExceptionWhenCreatingSessionThatAlreadyExists() {
        String agendaId = "1";

        Session session = new Session(agendaId, 2);

        Session sessionFound = new Session("2", agendaId, 2, SessionStatus.OPENED);

        Mockito.when(votingSessionClient.getSessionByAgendaId(session.getAgendaId())).thenReturn(sessionFound);

        assertThrows(CreatedSessionException.class, () -> appService.createSession(session));
    }

    @Test
    public void shouldRegisterVote() throws JsonProcessingException, ClosedSessionException, InvalidCpfException, VoteException {
        String agendaId = "2";
        long duration = 10;
        String associateCPF = "123";
        ObjectNode cpfValidatorResponse = mapper.readValue("{\"status\":\"ABLE_TO_VOTE\"}", ObjectNode.class);

        Vote vote = new Vote("1", associateCPF, VoteChoice.SIM);
        List<Vote> votes = new ArrayList<>();
        votes.add(vote);
        Session expected = new Session("1", agendaId, duration, SessionStatus.OPENED);
        expected.setStartDate(LocalDateTime.now().minusMinutes(2));
        expected.setEndDate(LocalDateTime.now().plusMinutes(duration));
        expected.setVotes(votes);

        Session session = new Session("1", agendaId, duration, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(2));
        session.setEndDate(LocalDateTime.now().plusMinutes(duration));

        Mockito.when(votingSessionClient.getSessionByAgendaId(agendaId)).thenReturn(session);
        Mockito.when(cpfValidator.validateCPF(associateCPF)).thenReturn(cpfValidatorResponse);
        Mockito.when(votingSessionClient.saveSession(session)).thenReturn(session);

        Session actual = appService.registerVote(vote, agendaId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getVotes().size(), actual.getVotes().size());

        assertEquals(expected.getVotes().get(0).getAssociateId(), actual.getVotes().get(0).getAssociateId());
        assertEquals(expected.getVotes().get(0).getAssociateCPF(), actual.getVotes().get(0).getAssociateCPF());
        assertEquals(expected.getVotes().get(0).getVoteChoice(), actual.getVotes().get(0).getVoteChoice());
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRegisterVoteAndSessionDurationIsOver() {
        String agendaId = "2";
        long duration = 3;
        String associateCPF = "123";

        Vote vote = new Vote("1", associateCPF, VoteChoice.SIM);

        Agenda agenda = new Agenda(agendaId, "New agenda", AgendaStatus.NEW);

        Session session = new Session("1", agendaId, duration, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(5));
        session.setEndDate(LocalDateTime.now().minusMinutes(2));

        Mockito.when(votingAgendaClient.getAgendaById(agendaId)).thenReturn(agenda);
        Mockito.when(votingSessionClient.getSessionByAgendaId(agendaId)).thenReturn(session);

        assertThrows(ClosedSessionException.class, () -> appService.registerVote(vote, agendaId));
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRegisterVoteAndAssociateIdAlreadyVoted() {
        String agendaId = "2";
        long duration = 10;
        String associateId = "1";
        String associateCPF = "123";

        Vote receivedVote = new Vote(associateId, "321", VoteChoice.SIM);

        Vote vote = new Vote(associateId, associateCPF, VoteChoice.SIM);
        List<Vote> votes = new ArrayList<>();
        votes.add(vote);
        Session session = new Session("1", agendaId, duration, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(2));
        session.setEndDate(LocalDateTime.now().plusMinutes(duration));
        session.setVotes(votes);

        Mockito.when(votingSessionClient.getSessionByAgendaId(agendaId)).thenReturn(session);

        assertThrows(VoteException.class, () -> appService.registerVote(receivedVote, agendaId));
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRegisterVoteAndAssociateCPFAlreadyVoted() {
        String agendaId = "2";
        long duration = 10;
        String associateId = "1";
        String associateCPF = "123";

        Vote receivedVote = new Vote("2", associateCPF, VoteChoice.SIM);

        Vote vote = new Vote(associateId, associateCPF, VoteChoice.SIM);
        List<Vote> votes = new ArrayList<>();
        votes.add(vote);
        Session session = new Session("1", agendaId, duration, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(2));
        session.setEndDate(LocalDateTime.now().plusMinutes(duration));
        session.setVotes(votes);

        Mockito.when(votingSessionClient.getSessionByAgendaId(agendaId)).thenReturn(session);

        assertThrows(VoteException.class, () -> appService.registerVote(receivedVote, agendaId));
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRegisterVoteAndAssociateCPFIsUnableToVote() throws JsonProcessingException {
        String agendaId = "2";
        long duration = 10;
        String associateCPF = "123";
        ObjectNode cpfValidatorResponse = mapper.readValue("{\"status\":\"UNABLE_TO_VOTE\"}", ObjectNode.class);

        Vote receivedVote = new Vote("2", associateCPF, VoteChoice.SIM);

        Session session = new Session("1", agendaId, duration, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(2));
        session.setEndDate(LocalDateTime.now().plusMinutes(duration));

        Mockito.when(votingSessionClient.getSessionByAgendaId(agendaId)).thenReturn(session);
        Mockito.when(cpfValidator.validateCPF(associateCPF)).thenReturn(cpfValidatorResponse);

        assertThrows(InvalidCpfException.class, () -> appService.registerVote(receivedVote, agendaId));
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRegisterVoteAndAssociateCPFIsInvalid() {
        String agendaId = "2";
        long duration = 10;
        String associateCPF = "123";

        Vote receivedVote = new Vote("2", associateCPF, VoteChoice.SIM);

        Session session = new Session("1", agendaId, duration, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(2));
        session.setEndDate(LocalDateTime.now().plusMinutes(duration));

        Mockito.when(votingSessionClient.getSessionByAgendaId(agendaId)).thenReturn(session);
        Mockito.when(cpfValidator.validateCPF(associateCPF)).thenThrow(FeignException.NotFound.class);

        assertThrows(InvalidCpfException.class, () -> appService.registerVote(receivedVote, agendaId));
    }

    @Test
    public void shouldGetAgendaResult() throws NewAgendaException {
        String agendaId = "2";
        String description = "Finished agenda.";

        AgendaResult expected = new AgendaResult(1, 1, "EMPATE");

        Agenda agenda = new Agenda(agendaId, description, AgendaStatus.FINISHED);
        agenda.setAgendaResult(expected);

        Mockito.when(votingAgendaClient.getAgendaById(agendaId)).thenReturn(agenda);

        AgendaResult actual = appService.getAgendaResult(agendaId);

        assertEquals(expected.getTotalDownVotes(), actual.getTotalDownVotes());
        assertEquals(expected.getTotalUpVotes(), actual.getTotalUpVotes());
        assertEquals(expected.getResult(), actual.getResult());
    }

    @Test
    public void shouldGenerateAgendaResultWhenSessionIsClosedAndAgendaHasNoResult() throws NewAgendaException {
        String agendaId = "2";
        String description = "New agenda.";

        List<Vote> votes = new ArrayList<>();
        votes.add(new Vote("1", "123", VoteChoice.SIM));
        votes.add(new Vote("2", "321", VoteChoice.NAO));
        Session session = new Session("1", agendaId, 2, SessionStatus.CLOSED);
        session.setStartDate(LocalDateTime.now().minusMinutes(10));
        session.setEndDate(LocalDateTime.now().minusMinutes(8));
        session.setVotes(votes);

        AgendaResult expected = new AgendaResult(1, 1, "EMPATE");

        Agenda agenda = new Agenda(agendaId, description, AgendaStatus.NEW);

        Mockito.when(votingAgendaClient.getAgendaById(agendaId)).thenReturn(agenda);
        Mockito.when(votingSessionClient.getSessionByAgendaId(agendaId)).thenReturn(session);
        Mockito.when(votingAgendaClient.saveAgenda(agenda)).thenReturn(agenda);

        AgendaResult actual = appService.getAgendaResult(agendaId);

        assertEquals(AgendaStatus.FINISHED, agenda.getStatus());

        assertEquals(expected.getTotalDownVotes(), actual.getTotalDownVotes());
        assertEquals(expected.getTotalUpVotes(), actual.getTotalUpVotes());
        assertEquals(expected.getResult(), actual.getResult());
    }

    @Test
    public void shouldGenerateAgendaResultWhenSessionDurationIsOverAndAgendaHasNoResult() throws NewAgendaException {
        String agendaId = "2";
        String description = "New agenda.";

        List<Vote> votes = new ArrayList<>();
        votes.add(new Vote("1", "123", VoteChoice.SIM));
        votes.add(new Vote("2", "321", VoteChoice.NAO));
        Session session = new Session("1", agendaId, 2, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(10));
        session.setEndDate(LocalDateTime.now().minusMinutes(8));
        session.setVotes(votes);

        AgendaResult expected = new AgendaResult(1, 1, "EMPATE");

        Agenda agenda = new Agenda(agendaId, description, AgendaStatus.NEW);

        Mockito.when(votingAgendaClient.getAgendaById(agendaId)).thenReturn(agenda);
        Mockito.when(votingSessionClient.getSessionByAgendaId(agendaId)).thenReturn(session);
        Mockito.when(votingSessionClient.saveSession(session)).thenReturn(session);
        Mockito.when(votingAgendaClient.saveAgenda(agenda)).thenReturn(agenda);

        AgendaResult actual = appService.getAgendaResult(agendaId);

        assertEquals(SessionStatus.CLOSED, session.getStatus());
        assertEquals(AgendaStatus.FINISHED, agenda.getStatus());

        assertEquals(expected.getTotalDownVotes(), actual.getTotalDownVotes());
        assertEquals(expected.getTotalUpVotes(), actual.getTotalUpVotes());
        assertEquals(expected.getResult(), actual.getResult());
    }

    @Test
    public void shouldThrowExceptionWhenAgendaIsNewAndSessionDurationIsNotOverYet() {
        String agendaId = "2";
        String description = "New agenda.";

        Session session = new Session("1", agendaId, 10, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(5));
        session.setEndDate(LocalDateTime.now().plusMinutes(5));

        Agenda agenda = new Agenda(agendaId, description, AgendaStatus.NEW);

        Mockito.when(votingAgendaClient.getAgendaById(agendaId)).thenReturn(agenda);
        Mockito.when(votingSessionClient.getSessionByAgendaId(agendaId)).thenReturn(session);

        assertThrows(NewAgendaException.class, () -> appService.getAgendaResult(agendaId));
    }

    @Test
    public void shouldGenerateAgendaResultSIM() {
        List<Vote> votes = new ArrayList<>();
        votes.add(new Vote("1", "123", VoteChoice.SIM));
        Session session = new Session("1", "2", 2, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(10));
        session.setEndDate(LocalDateTime.now().minusMinutes(8));
        session.setVotes(votes);

        AgendaResult result = appService.generateAgendaResult(session);

        assertEquals(1, result.getTotalUpVotes());
        assertEquals(0, result.getTotalDownVotes());
        assertEquals("SIM", result.getResult());
    }

    @Test
    public void shouldGenerateAgendaResultNAO() {
        List<Vote> votes = new ArrayList<>();
        votes.add(new Vote("1", "123", VoteChoice.NAO));
        Session session = new Session("1", "2", 2, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(10));
        session.setEndDate(LocalDateTime.now().minusMinutes(8));
        session.setVotes(votes);

        AgendaResult result = appService.generateAgendaResult(session);

        assertEquals(0, result.getTotalUpVotes());
        assertEquals(1, result.getTotalDownVotes());
        assertEquals("NAO", result.getResult());
    }

    @Test
    public void shouldGenerateAgendaResultEMPATE() {
        List<Vote> votes = new ArrayList<>();
        votes.add(new Vote("1", "123", VoteChoice.NAO));
        votes.add(new Vote("2", "321", VoteChoice.SIM));
        Session session = new Session("1", "2", 2, SessionStatus.OPENED);
        session.setStartDate(LocalDateTime.now().minusMinutes(10));
        session.setEndDate(LocalDateTime.now().minusMinutes(8));
        session.setVotes(votes);

        AgendaResult result = appService.generateAgendaResult(session);

        assertEquals(1, result.getTotalUpVotes());
        assertEquals(1, result.getTotalDownVotes());
        assertEquals("EMPATE", result.getResult());
    }

    @Test
    public void shouldRegisterResultAndFinishAgenda() {
        Agenda agenda = new Agenda("1", "Agenda.", AgendaStatus.NEW);

        AgendaResult result = new AgendaResult(1, 0, "SIM");

        Mockito.when(votingAgendaClient.saveAgenda(agenda)).thenReturn(agenda);

        appService.finishAgenda(agenda, result);

        assertEquals(result.getResult(), agenda.getAgendaResult().getResult());
        assertEquals(AgendaStatus.FINISHED, agenda.getStatus());
    }

    @Test
    public void shouldCloseSession() {
        Session session = new Session("1", "2", 2, SessionStatus.OPENED);

        Mockito.when(votingSessionClient.saveSession(session)).thenReturn(session);

        appService.closeSession(session);

        assertEquals(SessionStatus.CLOSED, session.getStatus());
    }
}
