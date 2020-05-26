package com.yanzord.votingagendaservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.AgendaStatus;
import com.yanzord.votingagendaservice.model.Agenda;
import com.yanzord.votingagendaservice.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaService {
    @Autowired
    private AgendaRepository agendaRepository;
    private final Agenda DEFAULT_AGENDA = new Agenda("1", "Default description.", AgendaStatus.FINISHED);

    @HystrixCommand(fallbackMethod = "defaultSaveAgenda")
    public Agenda saveAgenda(Agenda agenda) {
        return agendaRepository.save(agenda);
    }

    @HystrixCommand(
            fallbackMethod = "defaultGetAgendaById",
            ignoreExceptions = { AgendaNotFoundException.class })
    public Agenda getAgendaById(String id) throws AgendaNotFoundException {
        return agendaRepository.getAgendaById(id)
                .orElseThrow(AgendaNotFoundException::new);
    }

    public Agenda defaultSaveAgenda(Agenda agenda) {
        return DEFAULT_AGENDA;
    }

    public Agenda defaultGetAgendaById(String id) {
        return DEFAULT_AGENDA;
    }
}
