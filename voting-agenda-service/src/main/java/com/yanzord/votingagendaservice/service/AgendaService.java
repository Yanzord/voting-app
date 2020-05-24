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
import java.util.Optional;

@Service
public class AgendaService {
    @Autowired
    private AgendaRepository agendaRepository;
    private final Agenda DEFAULT_AGENDA = new Agenda("1", "Default description.", AgendaStatus.CLOSED);

    @HystrixCommand(fallbackMethod = "defaultAgendas")
    public List<Agenda> getAllAgendas() {
        return agendaRepository.getAllAgendas();
    }

    @HystrixCommand(
            fallbackMethod = "defaultGetAgendaById",
            ignoreExceptions = { AgendaNotFoundException.class })
    public Agenda getAgendaById(String id) throws AgendaNotFoundException {
        return Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));
    }

    @HystrixCommand(fallbackMethod = "defaultAddAgenda")
    public Agenda addAgenda(Agenda agenda) {
        return agendaRepository.saveAgenda(agenda);
    }

    @HystrixCommand(
            fallbackMethod = "defaultOpenAgenda",
            ignoreExceptions = { AgendaNotFoundException.class })
    public Agenda openAgenda(Agenda openedAgenda) throws AgendaNotFoundException {
        String id = openedAgenda.getId();
        Agenda agenda = Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));

        agenda.setStartDate(openedAgenda.getStartDate());
        agenda.setStatus(openedAgenda.getStatus());

        return agendaRepository.saveAgenda(agenda);
    }

    @HystrixCommand(
            fallbackMethod = "defaultCloseAgenda",
            ignoreExceptions = { AgendaNotFoundException.class })
    public Agenda closeAgenda(Agenda closedAgenda) throws AgendaNotFoundException {
        String id = closedAgenda.getId();
        Agenda agenda = Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));

        agenda.setVotes(closedAgenda.getVotes());
        agenda.setEndDate(closedAgenda.getEndDate());
        agenda.setStatus(closedAgenda.getStatus());

        return agendaRepository.saveAgenda(agenda);
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

    public Agenda defaultOpenAgenda(Agenda openedAgenda) {
        return DEFAULT_AGENDA;
    }

    public Agenda defaultCloseAgenda(Agenda closedAgenda) {
        return DEFAULT_AGENDA;
    }
}
