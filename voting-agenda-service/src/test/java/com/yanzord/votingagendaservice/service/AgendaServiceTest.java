package com.yanzord.votingagendaservice.service;

import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.*;
import com.yanzord.votingagendaservice.repository.AgendaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private AgendaService agendaService;

    @Test
    public void shouldRegisterAgenda() {
        Agenda expected = new Agenda("1", "New agenda.", AgendaStatus.NEW);
        Mockito.when(agendaRepository.save(expected)).thenReturn(expected);

        Agenda actual = agendaService.registerAgenda(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldGetAgendaById() throws AgendaNotFoundException {
        String fakeId = "1";

        Agenda expected = new Agenda(fakeId, "New agenda.", AgendaStatus.NEW);
        Mockito.when(agendaRepository.getAgendaById(fakeId)).thenReturn(Optional.of(expected));

        Agenda actual = agendaService.getAgendaById(fakeId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldNotReturnAgendaWhenAgendaIsNotFound() {
        String fakeId = "1";

        Mockito.when(agendaRepository.getAgendaById(fakeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AgendaNotFoundException.class, () -> agendaService.getAgendaById(fakeId));

        assertNotNull(exception);
    }

    @Test
    public void shouldUpdateAgenda() {
        String fakeId = "1";
        String fakeDescription = "Fake agenda.";

        AgendaResult agendaResult = new AgendaResult(5, 3, "SIM");
        Agenda expected = new Agenda(fakeId, fakeDescription, AgendaStatus.FINISHED);
        expected.setAgendaResult(agendaResult);

        Mockito.when(agendaRepository.save(expected)).thenReturn(expected);

        Agenda actual = agendaService.updateAgenda(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getAgendaResult().getResult(), actual.getAgendaResult().getResult());
    }
}
