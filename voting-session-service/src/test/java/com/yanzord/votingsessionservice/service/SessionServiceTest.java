package com.yanzord.votingsessionservice.service;

import com.yanzord.votingsessionservice.exception.ClosedSessionException;
import com.yanzord.votingsessionservice.exception.OpenedSessionException;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.model.SessionStatus;
import com.yanzord.votingsessionservice.model.Vote;
import com.yanzord.votingsessionservice.model.VoteChoice;
import com.yanzord.votingsessionservice.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    public void shouldOpenSession() throws OpenedSessionException, ClosedSessionException {
        String fakeId = "1";
        long fakeTimeout = 5;
        LocalDateTime fakeStartDate = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30);

        Session fakeSession = new Session(fakeId, fakeTimeout, fakeStartDate);
        fakeSession.setId(fakeId);

        Session expected = new Session(fakeId, fakeTimeout, fakeStartDate);
        expected.setId(fakeId);
        expected.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));
        expected.setStatus(SessionStatus.OPENED);

        Mockito.when(sessionRepository.getSessionByAgendaId(fakeSession.getAgendaId())).thenReturn(Optional.empty());
        Mockito.when(sessionRepository.save(fakeSession)).thenReturn(fakeSession);

        Session actual = sessionService.openSession(fakeSession);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldNotOpenSessionWhenSessionIsAlreadyOpened() {
        String fakeId = "1";
        long fakeTimeout = 5;
        LocalDateTime fakeStartDate = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30);

        Session fakeSession = new Session(fakeId, fakeTimeout, fakeStartDate);
        fakeSession.setId(fakeId);
        fakeSession.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));
        fakeSession.setStatus(SessionStatus.OPENED);

        Mockito.when(sessionRepository.getSessionByAgendaId(fakeSession.getAgendaId())).thenReturn(Optional.of(fakeSession));

        Exception exception = assertThrows(OpenedSessionException.class, () -> sessionService.openSession(fakeSession));

        assertNotNull(exception);
    }

    @Test
    public void shouldNotOpenSessionWhenSessionIsClosed() {
        String fakeId = "1";
        long fakeTimeout = 5;
        LocalDateTime fakeStartDate = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30);

        Session fakeSession = new Session(fakeId, fakeTimeout, fakeStartDate);
        fakeSession.setId(fakeId);
        fakeSession.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));
        fakeSession.setStatus(SessionStatus.CLOSED);

        Mockito.when(sessionRepository.getSessionByAgendaId(fakeSession.getAgendaId())).thenReturn(Optional.of(fakeSession));

        Exception exception = assertThrows(ClosedSessionException.class, () -> sessionService.openSession(fakeSession));

        assertNotNull(exception);
    }

    @Test
    public void shouldRegisterVote() throws SessionNotFoundException {
        String fakeId = "1";
        long fakeTimeout = 5;
        LocalDateTime fakeStartDate = LocalDateTime.now();
        Vote vote = new Vote(fakeId, "123", VoteChoice.SIM);
        List<Vote> votes = new ArrayList<>();
        votes.add(vote);

        Session sessionFound = new Session(fakeId, fakeTimeout, fakeStartDate);
        sessionFound.setId(fakeId);
        sessionFound.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));
        sessionFound.setStatus(SessionStatus.OPENED);

        Session expected = new Session(fakeId, fakeTimeout, fakeStartDate);
        expected.setId(fakeId);
        expected.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));
        expected.setStatus(SessionStatus.OPENED);
        expected.setVotes(votes);

        Mockito.when(sessionRepository.getSessionByAgendaId(fakeId)).thenReturn(Optional.of(sessionFound));
        Mockito.when(sessionRepository.save(sessionFound)).thenReturn(sessionFound);

        Session actual = sessionService.registerVote(vote, fakeId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getVotes().size(), actual.getVotes().size());
        assertEquals(expected.getVotes().get(0).getAssociateCPF(), actual.getVotes().get(0).getAssociateCPF());
    }

    @Test
    public void shouldNotRegisterVoteWhenSessionIsNotFound() {
        String fakeId = "1";
        Vote vote = new Vote(fakeId, "123", VoteChoice.SIM);

        Mockito.when(sessionRepository.getSessionByAgendaId(fakeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(SessionNotFoundException.class,
                () -> sessionService.registerVote(vote, fakeId));

        assertNotNull(exception);
    }

    @Test
    public void shouldCloseSessionAndNotRegisterVoteWhenSessionDurationIsOver() throws SessionNotFoundException {
        String fakeId = "1";
        long fakeTimeout = 2;
        LocalDateTime fakeStartDate = LocalDateTime.now().minusMinutes(5);
        Vote vote = new Vote(fakeId, "123", VoteChoice.SIM);

        Session fakeSession = new Session(fakeId, fakeTimeout, fakeStartDate);
        fakeSession.setId(fakeId);
        fakeSession.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));
        fakeSession.setStatus(SessionStatus.OPENED);

        Session expected = new Session(fakeId, fakeTimeout, fakeStartDate);
        expected.setId(fakeId);
        expected.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));
        expected.setStatus(SessionStatus.CLOSED);

        Mockito.when(sessionRepository.getSessionByAgendaId(fakeId)).thenReturn(Optional.of(fakeSession));
        Mockito.when(sessionRepository.save(fakeSession)).thenReturn(fakeSession);

        Session actual = sessionService.registerVote(vote, fakeId);

        assertEquals(expected.getStatus(), actual.getStatus());
        assertNull(actual.getVotes());
    }
}
