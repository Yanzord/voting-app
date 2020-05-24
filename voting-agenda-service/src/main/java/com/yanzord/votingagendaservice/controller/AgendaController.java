package com.yanzord.votingagendaservice.controller;

import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.Agenda;
import com.yanzord.votingagendaservice.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/agenda")
public class AgendaController {
    @Autowired
    private AgendaService agendaService;
    private static final String NOT_FOUND_MESSAGE = "Agenda not found.";

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Agenda registerAgenda(@RequestBody Agenda agenda) {
        return agendaService.registerAgenda(agenda);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Agenda> getAllAgendas() {
        return agendaService.getAllAgendas();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Agenda getAgendaById(@PathVariable("id") String id) {
        try {
            return agendaService.getAgendaById(id);
        } catch (AgendaNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE, e);
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.PATCH)
    public Agenda updateAgenda(@RequestBody Agenda updatedAgenda) {
        try {
            return agendaService.updateAgenda(updatedAgenda);
        } catch (AgendaNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE, e);
        }
    }
}
