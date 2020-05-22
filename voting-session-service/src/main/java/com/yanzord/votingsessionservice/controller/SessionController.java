package com.yanzord.votingsessionservice.controller;

import com.yanzord.votingsessionservice.exception.ClosedSessionException;
import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.exception.OpenedSessionException;
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

    @RequestMapping(value = "/open", method = RequestMethod.POST)
    private Session openSession(@RequestBody Session session) {
        try {
            return sessionService.openSession(session);
        } catch (OpenedSessionException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Session is already opened.", e);
        }
    }

    @RequestMapping(value = "/{agendaId}", method = RequestMethod.GET)
    private Session getSessionByAgendaId(@PathVariable("agendaId") String agendaId) {
        try {
            return sessionService.getSessionByAgendaId(agendaId);
        } catch (SessionNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Session not found.", e);
        } catch (ClosedSessionException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Voting timeout expired, session is currently closed.", e);
        }
    }
}
