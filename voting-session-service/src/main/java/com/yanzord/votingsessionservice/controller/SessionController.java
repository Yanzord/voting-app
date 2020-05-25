package com.yanzord.votingsessionservice.controller;

import com.yanzord.votingsessionservice.exception.ClosedSessionException;
import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.exception.OpenedSessionException;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.model.Vote;
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

    @RequestMapping(value = "/", method = RequestMethod.POST)
    private Session openSession(@RequestBody Session session) {
        try {
            return sessionService.openSession(session);
        } catch (OpenedSessionException | ClosedSessionException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/{agendaId}", method = RequestMethod.POST)
    private Session registerVote(@RequestBody Vote vote, @PathVariable("agendaId") String agendaId) {
        try {
            return sessionService.registerVote(vote, agendaId);
        } catch (SessionNotFoundException | ClosedSessionException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
