package com.yanzord.votingagendaservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.AgendaStatus;
import com.yanzord.votingagendaservice.model.Agenda;
import com.yanzord.votingagendaservice.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AgendaService {
    @Autowired
    private AgendaRepository agendaRepository;
    private final Agenda DEFAULT_AGENDA = new Agenda("1", "Default description.", AgendaStatus.FINISHED);

    @HystrixCommand(
            fallbackMethod = "defaultGetAgendaById",
            ignoreExceptions = { AgendaNotFoundException.class })
    public Agenda getAgendaById(String id) throws AgendaNotFoundException {
        return agendaRepository.getAgendaById(id)
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));
    }

    @HystrixCommand(fallbackMethod = "defaultAddAgenda")
    public Agenda registerAgenda(Agenda agenda) {
        agenda.setStatus(AgendaStatus.NEW);
        return agendaRepository.save(agenda);
    }

    @HystrixCommand(
            fallbackMethod = "defaultOpenAgenda",
            ignoreExceptions = { AgendaNotFoundException.class })
    public Agenda updateAgenda(Agenda updatedAgenda) throws AgendaNotFoundException {
        String id = updatedAgenda.getId();
        Agenda agenda = agendaRepository.getAgendaById(id)
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));

        agenda.setAgendaResult(updatedAgenda.getAgendaResult());
        agenda.setStatus(updatedAgenda.getStatus());

        return agendaRepository.save(agenda);
    }

    public List<Agenda> defaultAgendas() {
        return new ArrayList<>();
    }

    public Agenda defaultGetAgendaById(String id) {
        return DEFAULT_AGENDA;
    }

    public Agenda defaultAddAgenda(Agenda agenda) {
        return DEFAULT_AGENDA;
    }

    public Agenda defaultUpdateAgenda(Agenda updatedAgenda) {
        return DEFAULT_AGENDA;
    }
}
