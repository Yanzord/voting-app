package com.yanzord.votingagendaservice.controller;

import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.Agenda;
import com.yanzord.votingagendaservice.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/agenda")
public class AgendaController {
    @Autowired
    private AgendaService agendaService;

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Agenda saveAgenda(@RequestBody Agenda agenda) {
        return agendaService.saveAgenda(agenda);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Agenda getAgendaById(@PathVariable("id") String id) {
        try {
            return agendaService.getAgendaById(id);
        } catch (AgendaNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
