package com.yanzord.votingagendaservice.service;

import com.yanzord.votingagendaservice.dto.ClosedAgendaDTO;
import com.yanzord.votingagendaservice.dto.OpenedAgendaDTO;
import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.Agenda;
import com.yanzord.votingagendaservice.model.AgendaStatus;
import com.yanzord.votingagendaservice.model.Vote;
import com.yanzord.votingagendaservice.model.VoteChoice;
import com.yanzord.votingagendaservice.repository.AgendaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
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
    public void shouldGetAllAgendas() {
        List<Agenda> expected = new ArrayList<>();
        expected.add(new Agenda("1", "New agenda.", AgendaStatus.NEW));
        expected.add(new Agenda("2", "Opened agenda.", AgendaStatus.OPENED));
        expected.add(new Agenda("3", "Closed agenda.", AgendaStatus.CLOSED));

        Mockito.when(agendaRepository.getAllAgendas()).thenReturn(expected);

        List<Agenda> actual = agendaService.getAllAgendas();

        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }

    @Test
    public void shouldGetAgendaById() {
        Agenda expected = new Agenda("1", "New agenda.", AgendaStatus.NEW);
        Mockito.when(agendaRepository.getAgendaById("1")).thenReturn(expected);

        Agenda actual = agendaService.getAgendaById("1");

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldNotReturnAgendaWhenAgendaIsNotFound() {
        Mockito.when(agendaRepository.getAgendaById("1")).thenReturn(null);

        Exception exception = assertThrows(AgendaNotFoundException.class, () -> agendaService.getAgendaById("1"));

        assertNotNull(exception);
    }

    @Test
    public void shouldAddAgenda() {
        Agenda expected = new Agenda("1", "New agenda.", AgendaStatus.NEW);
        Mockito.when(agendaRepository.saveAgenda(expected)).thenReturn(expected);

        Agenda actual = agendaService.addAgenda(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldOpenAgenda() {
        LocalDateTime startDate = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30);

        Agenda agenda = new Agenda("1", "Fake agenda.", AgendaStatus.NEW);
        Agenda expected = new Agenda("1", "Fake agenda.", AgendaStatus.OPENED);
        expected.setStartDate(startDate);

        OpenedAgendaDTO openedAgendaDTO = new OpenedAgendaDTO("1", startDate, AgendaStatus.OPENED);

        Mockito.when(agendaRepository.getAgendaById(openedAgendaDTO.getId())).thenReturn(agenda);
        Mockito.when(agendaRepository.saveAgenda(agenda)).thenReturn(agenda);

        Agenda actual = agendaService.openAgenda(openedAgendaDTO);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldNotOpenAgendaWhenAgendaIsNotFound() {
        Mockito.when(agendaRepository.getAgendaById("1")).thenReturn(null);

        OpenedAgendaDTO openedAgendaDTO = new OpenedAgendaDTO("1",
                LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30),
                AgendaStatus.OPENED);

        Exception exception = assertThrows(AgendaNotFoundException.class, () -> agendaService.openAgenda(openedAgendaDTO));

        assertNotNull(exception);
    }

    @Test
    public void shouldCloseAgenda() {
        List<Vote> votes = new ArrayList<>();
        votes.add(new Vote("1", VoteChoice.SIM));

        LocalDateTime startDate = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2020, Month.JANUARY, 1, 11, 10, 30);

        Agenda expected = new Agenda("1", "Fake agenda.", AgendaStatus.CLOSED);
        expected.setVotes(votes);
        expected.setStartDate(startDate);
        expected.setEndDate(endDate);

        Agenda agenda = new Agenda("1", "Fake agenda.", AgendaStatus.OPENED);
        agenda.setStartDate(startDate);

        ClosedAgendaDTO closedAgendaDTO = new ClosedAgendaDTO("1", votes, endDate, AgendaStatus.CLOSED);

        Mockito.when(agendaRepository.getAgendaById(closedAgendaDTO.getId())).thenReturn(agenda);
        Mockito.when(agendaRepository.saveAgenda(agenda)).thenReturn(agenda);

        Agenda actual = agendaService.closeAgenda(closedAgendaDTO);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getVotes().size(), actual.getVotes().size());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldNotCloseAgendaWhenAgendaIsNotFound() {
        List<Vote> votes = new ArrayList<>();
        votes.add(new Vote("1", VoteChoice.SIM));

        LocalDateTime endDate = LocalDateTime.of(2020, Month.JANUARY, 1, 11, 10, 30);

        ClosedAgendaDTO closedAgendaDTO = new ClosedAgendaDTO("1", votes, endDate, AgendaStatus.CLOSED);

        Mockito.when(agendaRepository.getAgendaById("1")).thenReturn(null);

        Exception exception = assertThrows(AgendaNotFoundException.class, () -> agendaService.closeAgenda(closedAgendaDTO));

        assertNotNull(exception);
    }
}
