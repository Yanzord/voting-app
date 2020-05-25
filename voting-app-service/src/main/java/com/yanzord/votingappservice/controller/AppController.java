package com.yanzord.votingappservice.controller;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.dto.AgendaResult;
import com.yanzord.votingappservice.dto.SessionDTO;
import com.yanzord.votingappservice.dto.VoteDTO;
import com.yanzord.votingappservice.exception.*;
import com.yanzord.votingappservice.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/app")
public class AppController {
    @Autowired
    private AppService appService;

    @RequestMapping(value = "/agenda", method = RequestMethod.POST)
    public AgendaDTO registerAgenda(@RequestBody AgendaDTO agenda) {
        return appService.registerAgenda(agenda);
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public SessionDTO createSession(@RequestBody SessionDTO session) {
        try {
            return appService.createSession(session);
        } catch (SessionException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/session/{agendaId}", method = RequestMethod.POST)
    public SessionDTO registerVote(@RequestBody VoteDTO vote, @PathVariable("agendaId") String agendaId) {
        try {
            return appService.registerVote(vote, agendaId);
        } catch (ClosedSessionException | FinishedAgendaException | VoteException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/result/{agendaId}", method = RequestMethod.GET)
    public AgendaResult getAgendaResult(@PathVariable("agendaId") String agendaId) {
        try {
            return appService.getAgendaResult(agendaId);
        } catch (NewAgendaException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
