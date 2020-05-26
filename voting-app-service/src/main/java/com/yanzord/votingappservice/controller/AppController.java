package com.yanzord.votingappservice.controller;

import com.yanzord.votingappservice.model.Agenda;
import com.yanzord.votingappservice.model.AgendaResult;
import com.yanzord.votingappservice.model.Session;
import com.yanzord.votingappservice.model.Vote;
import com.yanzord.votingappservice.exception.*;
import com.yanzord.votingappservice.service.AppService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/app")
public class AppController {
    private static final Logger logger = Logger.getLogger(AppController.class);

    @Autowired
    private AppService appService;

    @RequestMapping(value = "/agenda", method = RequestMethod.POST)
    public Agenda registerAgenda(@RequestBody Agenda agenda) {
        return appService.registerAgenda(agenda);
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public Session createSession(@RequestBody Session session) {
        try {
            return appService.createSession(session);
        } catch (CreatedSessionException e) {
            logger.error("Error occurred creating session: " + e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/session/{agendaId}", method = RequestMethod.POST)
    public Session registerVote(@RequestBody Vote vote, @PathVariable("agendaId") String agendaId) {
        try {
            return appService.registerVote(vote, agendaId);
        } catch (ClosedSessionException | VoteException | InvalidCpfException e) {
            logger.error("Error occurred registering vote: " + e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/result/{agendaId}", method = RequestMethod.GET)
    public AgendaResult getAgendaResult(@PathVariable("agendaId") String agendaId) {
        try {
            return appService.getAgendaResult(agendaId);
        } catch (NewAgendaException e) {
            logger.error("Error occurred requesting agenda result: " + e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
