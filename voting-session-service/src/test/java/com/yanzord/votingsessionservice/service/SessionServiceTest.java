package com.yanzord.votingsessionservice.service;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import com.yanzord.votingsessionservice.exception.ClosedSessionException;
import com.yanzord.votingsessionservice.exception.OpenedSessionException;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.model.Session;
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
    public void shouldGetSessionByAgendaId() throws ClosedSessionException, SessionNotFoundException {
        String fakeId = "1";
        long fakeTimeout = 5;
        LocalDateTime fakeStartDate = LocalDateTime.now();

        Session expected = new Session(fakeId, fakeTimeout, fakeStartDate);
        expected.setId(fakeId);
        expected.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));

        List<Session> sessions = new ArrayList<>();
        sessions.add(expected);

        Mockito.when(sessionRepository.findAll()).thenReturn(sessions);

        Session actual = sessionService.getSessionByAgendaId(fakeId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getTimeout(), actual.getTimeout());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }

    @Test
    public void shouldNotGetSessionByAgendaIdWhenSessionIsNotFound() {
        String fakeId = "1";

        Mockito.when(sessionRepository.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(SessionNotFoundException.class,
                () -> sessionService.getSessionByAgendaId(fakeId));

        assertNotNull(exception);
    }

    @Test
    public void shouldNotGetSessionByIdWhenSessionIsTimedOut() {
        String fakeId = "1";
        long fakeTimeout = 2;
        LocalDateTime fakeStartDate = LocalDateTime.now().minusMinutes(5);

        Session expected = new Session(fakeId, fakeTimeout, fakeStartDate);
        expected.setId(fakeId);
        expected.setEndDate(fakeStartDate.plusMinutes(fakeTimeout));

        List<Session> sessions = new ArrayList<>();
        sessions.add(expected);

        Mockito.when(sessionRepository.findAll()).thenReturn(sessions);

        Exception exception = assertThrows(ClosedSessionException.class,
                () -> sessionService.getSessionByAgendaId(fakeId));

        assertNotNull(exception);
    }
}
