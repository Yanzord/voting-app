package com.yanzord.votingagendaservice.service;

import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.*;
import com.yanzord.votingagendaservice.repository.AgendaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
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
        Agenda expected = new Agenda("1", "New agenda.", AgendaStatus.NEW);
        Mockito.when(agendaRepository.getAgendaById("1")).thenReturn(Optional.of(expected));

        Agenda actual = agendaService.getAgendaById("1");

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldNotReturnAgendaWhenAgendaIsNotFound() {
        String fakeId = "1";

        Mockito.when(agendaRepository.getAgendaById(fakeId)).thenReturn(null);

        Exception exception = assertThrows(AgendaNotFoundException.class, () -> agendaService.getAgendaById(fakeId));

        assertNotNull(exception);
    }

    @Test
    public void shouldUpdateAgendaToFinished() throws AgendaNotFoundException {
        String fakeId = "1";
        String fakeDescription = "Fake agenda.";
        Agenda agenda = new Agenda(fakeId, fakeDescription, AgendaStatus.NEW);

        AgendaResult agendaResult = new AgendaResult(5, 3, "SIM");
        Agenda expected = new Agenda(fakeId, fakeDescription, AgendaStatus.FINISHED);
        expected.setAgendaResult(agendaResult);

        Agenda updatedAgenda = new Agenda(fakeId, fakeDescription, AgendaStatus.FINISHED);
        updatedAgenda.setAgendaResult(agendaResult);

        Mockito.when(agendaRepository.getAgendaById(updatedAgenda.getId())).thenReturn(Optional.of(agenda));
        Mockito.when(agendaRepository.save(agenda)).thenReturn(agenda);

        Agenda actual = agendaService.updateAgenda(updatedAgenda);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getAgendaResult().toString(), actual.getAgendaResult().toString());
    }

    @Test
    public void shouldNotUpdateAgendaWhenAgendaIsNotFound() {
        AgendaResult agendaResult = new AgendaResult(5, 3, "SIM");

        Agenda agenda = new Agenda("1",
                "Default agenda.",
                AgendaStatus.NEW);

        agenda.setAgendaResult(agendaResult);
        agenda.setStatus(AgendaStatus.FINISHED);

        Mockito.when(agendaRepository.getAgendaById(agenda.getId())).thenReturn(null);

        Exception exception = assertThrows(AgendaNotFoundException.class, () -> agendaService.updateAgenda(agenda));

        assertNotNull(exception);
    }
}
