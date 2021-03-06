package com.yanzord.votingsessionservice.controller;

import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/session")
public class SessionController {
    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    private Session saveSession(@RequestBody Session session) {
        return sessionService.saveSession(session);
    }

    @RequestMapping(value = "/{agendaId}", method = RequestMethod.GET)
    private Session getSessionByAgendaId(@PathVariable("agendaId") String agendaId) {
        try {
            return sessionService.getSessionByAgendaId(agendaId);
        } catch (SessionNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
