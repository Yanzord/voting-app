package com.yanzord.votingsessionservice.service;

import com.yanzord.votingsessionservice.exception.OpenedSessionException;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.model.Session;
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
import java.util.Collections;
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
    public void shouldOpenSession() throws OpenedSessionException {
        String fakeId = "1";
        long fakeTimeout = 5;
        LocalDateTime fakeStartDate = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30);

        Session fakeSession = new Session(fakeId, fakeTimeout, fakeStartDate);
        fakeSession.setId(fakeId);

        Session expected = new Session(fakeId, fakeTimeout, fakeStartDate);
        expected.setId(fakeId);
        expected.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));

        Mockito.when(sessionRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(sessionRepository.save(fakeSession)).thenReturn(fakeSession);

        Session actual = sessionService.openSession(fakeSession);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getTimeout(), actual.getTimeout());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }

    @Test
    public void shouldNotOpenSessionWhenSessionIsAlreadyOpened() {
        String fakeId = "1";
        long fakeTimeout = 5;
        LocalDateTime fakeStartDate = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30);

        Session fakeSession = new Session(fakeId, fakeTimeout, fakeStartDate);
        fakeSession.setId(fakeId);
        fakeSession.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));

        List<Session> sessions = new ArrayList<>();
        sessions.add(fakeSession);

        Mockito.when(sessionRepository.findAll()).thenReturn(sessions);

        Exception exception = assertThrows(OpenedSessionException.class, () -> sessionService.openSession(fakeSession));

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

        Session expected = new Session(fakeId, fakeTimeout, fakeStartDate);
        expected.setId(fakeId);
        expected.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));
        expected.setVotes(votes);

        Mockito.when(sessionRepository.findById(fakeId)).thenReturn(Optional.of(sessionFound));

        Session actual = sessionService.registerVote(vote, fakeId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getTimeout(), actual.getTimeout());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getVotes().size(), actual.getVotes().size());
        assertEquals(expected.getVotes().get(0).getAssociateCPF(), actual.getVotes().get(0).getAssociateCPF());
    }

    @Test
    public void shouldNotRegisterVoteWhenSessionIsNotFound() {
        String fakeId = "1";
        Vote vote = new Vote(fakeId, "123", VoteChoice.SIM);

        Mockito.when(sessionRepository.findById(fakeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(SessionNotFoundException.class,
                () -> sessionService.registerVote(vote, fakeId));

        assertNotNull(exception);
    }

    @Test
    public void shouldNotRegisterVoteWhenSessionIsTimedOut() throws SessionNotFoundException {
        String fakeId = "1";
        long fakeTimeout = 2;
        LocalDateTime fakeStartDate = LocalDateTime.now().minusMinutes(5);
        Vote vote = new Vote(fakeId, "123", VoteChoice.SIM);

        Session expected = new Session(fakeId, fakeTimeout, fakeStartDate);
        expected.setId(fakeId);
        expected.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));

        Mockito.when(sessionRepository.findById(fakeId)).thenReturn(Optional.of(expected));

        Session actual = sessionService.registerVote(vote, fakeId);

        assertNull(actual.getVotes());
    }
}
