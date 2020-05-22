package com.yanzord.votingagendaservice.controller;

import com.yanzord.votingagendaservice.dto.ClosedAgendaDTO;
import com.yanzord.votingagendaservice.dto.OpenedAgendaDTO;
import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.Agenda;
import com.yanzord.votingagendaservice.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voting-agenda")
public class AgendaController {
    @Autowired
    private AgendaService agendaService;

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Data not found.")
    @ExceptionHandler(AgendaNotFoundException.class)
    public void handleNotFoundException() {
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Agenda> getAllAgendas() {
        return agendaService.getAllAgendas();
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public Agenda getAgendaById(@PathVariable("id") String id) {
        return agendaService.getAgendaById(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Agenda addAgenda(@RequestBody Agenda agenda) {
        return agendaService.addAgenda(agenda);
    }

    @RequestMapping(value = "/open", method = RequestMethod.POST)
    public Agenda openAgenda(@RequestBody OpenedAgendaDTO openedAgendaDTO) {
        return agendaService.openAgenda(openedAgendaDTO);
    }

    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public Agenda closeAgenda(@RequestBody ClosedAgendaDTO closedAgendaDTO) {
        return agendaService.closeAgenda(closedAgendaDTO);
    }
}
