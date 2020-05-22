package com.yanzord.votingagendaservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.Vote;
import com.yanzord.votingagendaservice.model.Agenda;
import com.yanzord.votingagendaservice.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    @HystrixCommand(fallbackMethod = "defaultAgenda")
    public Agenda getVotingAgendaById(String id) {
        return Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));
    }

    @HystrixCommand(fallbackMethod = "defaultAgenda")
    public Agenda addVotingAgenda(Agenda agenda) {
        return agendaRepository.saveAgenda(agenda);
    }

    @HystrixCommand(fallbackMethod = "defaultAgenda")
    public Agenda updateVotingAgenda(String id, LocalDateTime startDate, boolean status) {
        Agenda agenda = Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));

        agenda.setStartDate(startDate);
        agenda.setStatus(status);

        return agendaRepository.saveAgenda(agenda);
    }

    @HystrixCommand(fallbackMethod = "defaultAgenda")
    public Agenda updateVotingAgenda(String id, List<Vote> votes, LocalDateTime endDate, boolean status) {
        Agenda agenda = Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));

        agenda.setVotes(votes);
        agenda.setEndDate(endDate);
        agenda.setStatus(status);

        return agendaRepository.saveAgenda(agenda);
    }

    public Agenda defaultAgenda() {
        return new Agenda("1", "Default agenda.", false);
    }
}
