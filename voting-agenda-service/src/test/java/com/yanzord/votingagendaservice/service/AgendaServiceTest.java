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
    public void shouldSaveAgenda() {
        String agendaId = "1";
        String description = "New agenda.";

        Agenda expected = new Agenda(agendaId, description, AgendaStatus.NEW);
        Mockito.when(agendaRepository.save(expected)).thenReturn(expected);

        Agenda actual = agendaService.saveAgenda(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldGetAgendaById() throws AgendaNotFoundException {
        String agendaId = "1";
        String description = "New agenda.";

        Agenda expected = new Agenda(agendaId, description, AgendaStatus.NEW);
        Mockito.when(agendaRepository.getAgendaById(agendaId)).thenReturn(Optional.of(expected));

        Agenda actual = agendaService.getAgendaById(agendaId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldThrowExceptionWhenAgendaIsNotFound() {
        String agendaId = "1";

        Mockito.when(agendaRepository.getAgendaById(agendaId)).thenReturn(Optional.empty());

        assertThrows(AgendaNotFoundException.class, () -> agendaService.getAgendaById(agendaId));
    }
}
