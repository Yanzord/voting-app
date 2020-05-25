package com.yanzord.votingappservice.controller;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.dto.AgendaResult;
import com.yanzord.votingappservice.dto.SessionDTO;
import com.yanzord.votingappservice.dto.VoteDTO;
import com.yanzord.votingappservice.exception.ClosedSessionException;
import com.yanzord.votingappservice.exception.FinishedAgendaException;
import com.yanzord.votingappservice.exception.UnknownAgendaStatusException;
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
    public SessionDTO openSession(@RequestBody SessionDTO session) {
        try {
            return appService.openSession(session);
        } catch (FinishedAgendaException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Agenda is finished, can't open session.", e);
        } catch (UnknownAgendaStatusException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unknown agenda status.", e);
        }
    }

    @RequestMapping(value = "/session/{agendaId}", method = RequestMethod.POST)
    public SessionDTO registerVote(@RequestBody VoteDTO vote, @PathVariable("agendaId") String agendaId) {
        try {
            return appService.registerVote(vote, agendaId);
        } catch (ClosedSessionException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Session is finished, can't register vote.", e);
        } catch (FinishedAgendaException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Agenda is finished, can't register vote.", e);
        }
    }

    @RequestMapping(value = "/result/{agendaId}", method = RequestMethod.GET)
    public AgendaResult getAgendaResult(@PathVariable("agendaId") String agendaId) {
        return appService.getAgendaResult(agendaId);
    }
}
