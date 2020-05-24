package com.yanzord.votingappservice.controller;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.dto.SessionDTO;
import com.yanzord.votingappservice.dto.VoteDTO;
import com.yanzord.votingappservice.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
        return appService.openSession(session);
    }

    @RequestMapping(value = "/session/{sessionId}", method = RequestMethod.POST)
    public SessionDTO registerVote(@RequestBody VoteDTO vote, @PathParam("sessionId") String sessionId) {
        return appService.registerVote(vote, sessionId);
    }

    @RequestMapping(value = "/result/{agendaId}", method = RequestMethod.GET)
    public AgendaDTO getAgendaResult(@PathParam("agendaId") String agendaId) {
        return appService.getAgendaResult(agendaId);
    }
}
