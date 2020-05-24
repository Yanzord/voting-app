package com.yanzord.votingappservice.controller;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.dto.SessionDTO;
import com.yanzord.votingappservice.dto.VoteDTO;
import com.yanzord.votingappservice.exception.ClosedAgendaException;
import com.yanzord.votingappservice.exception.OpenedAgendaException;
import com.yanzord.votingappservice.exception.UnknownAgendaStatusException;
import com.yanzord.votingappservice.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.websocket.server.PathParam;
import java.util.List;

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
        } catch (OpenedAgendaException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Agenda is already opened.", e);
        } catch (ClosedAgendaException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Agenda is closed.", e);
        } catch (UnknownAgendaStatusException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unknown agenda status.", e);
        }
    }

    @RequestMapping(value = "/session/{agendaId}", method = RequestMethod.POST)
    public SessionDTO registerVote(@RequestBody VoteDTO vote, @PathParam("agendaId") String agendaId) {
        return appService.registerVote(vote, agendaId);
    }

    @RequestMapping(value = "/result/{agendaId}", method = RequestMethod.GET)
    public AgendaDTO getAgendaResult(@PathParam("agendaId") String agendaId) {
        return appService.getAgendaResult(agendaId);
    }
}
