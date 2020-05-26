package com.yanzord.votingsessionservice.service;

import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.model.SessionStatus;
import com.yanzord.votingsessionservice.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    public void shouldSaveSession() {
        String sessionId = "1";
        String agendaId = "2";
        long duration = 5;
        LocalDateTime startDate = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30);

        Session expected = new Session(agendaId, duration, startDate);
        expected.setId(sessionId);
        expected.setEndDate(startDate.plusMinutes(duration));
        expected.setStatus(SessionStatus.OPENED);

        Mockito.when(sessionRepository.save(expected)).thenReturn(expected);

        Session actual = sessionService.saveSession(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldGetSessionByAgendaId() throws SessionNotFoundException {
        String agendaId = "2";
        String sessionId = "1";
        long duration = 5;
        LocalDateTime startDate = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30);

        Session expected = new Session(agendaId, 5, startDate);
        expected.setId(sessionId);
        expected.setEndDate(startDate.plusMinutes(duration));
        expected.setStatus(SessionStatus.OPENED);

        Mockito.when(sessionRepository.getSessionByAgendaId(agendaId)).thenReturn(Optional.of(expected));

        Session actual = sessionService.getSessionByAgendaId(agendaId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAgendaId(), actual.getAgendaId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldThrowExceptionWhenSessionIsNotFound() {
        String agendaId = "1";

        Mockito.when(sessionRepository.getSessionByAgendaId(agendaId)).thenReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class, () -> sessionService.getSessionByAgendaId(agendaId));
    }
}
