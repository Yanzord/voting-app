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
        Mockito.when(agendaRepository.saveAgenda(expected)).thenReturn(expected);

        Agenda actual = agendaService.registerAgenda(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldGetAllAgendas() {
        List<Agenda> expected = new ArrayList<>();
        expected.add(new Agenda("1", "New agenda.", AgendaStatus.NEW));
        expected.add(new Agenda("2", "Finished agenda.", AgendaStatus.FINISHED));
        expected.add(new Agenda("3", "Another new agenda.", AgendaStatus.NEW));

        Mockito.when(agendaRepository.getAllAgendas()).thenReturn(expected);

        List<Agenda> actual = agendaService.getAllAgendas();

        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }

    @Test
    public void shouldGetAgendaById() throws AgendaNotFoundException {
        Agenda expected = new Agenda("1", "New agenda.", AgendaStatus.NEW);
        Mockito.when(agendaRepository.getAgendaById("1")).thenReturn(expected);

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

        Result result = new Result(5, 3, "SIM");
        Agenda expected = new Agenda(fakeId, fakeDescription, AgendaStatus.FINISHED);
        expected.setResult(result);

        Agenda updatedAgenda = new Agenda(fakeId, fakeDescription, AgendaStatus.FINISHED);
        updatedAgenda.setResult(result);

        Mockito.when(agendaRepository.getAgendaById(updatedAgenda.getId())).thenReturn(agenda);
        Mockito.when(agendaRepository.saveAgenda(agenda)).thenReturn(agenda);

        Agenda actual = agendaService.updateAgenda(updatedAgenda);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getResult().toString(), actual.getResult().toString());
    }

    @Test
    public void shouldNotUpdateAgendaWhenAgendaIsNotFound() {
        Result result = new Result(5, 3, "SIM");

        Agenda agenda = new Agenda("1",
                "Default agenda.",
                AgendaStatus.NEW);

        agenda.setResult(result);
        agenda.setStatus(AgendaStatus.FINISHED);

        Mockito.when(agendaRepository.getAgendaById(agenda.getId())).thenReturn(null);

        Exception exception = assertThrows(AgendaNotFoundException.class, () -> agendaService.updateAgenda(agenda));

        assertNotNull(exception);
    }
}
