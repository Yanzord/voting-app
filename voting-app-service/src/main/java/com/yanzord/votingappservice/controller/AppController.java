package com.yanzord.votingappservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanzord.votingappservice.model.Agenda;
import com.yanzord.votingappservice.model.AgendaResult;
import com.yanzord.votingappservice.model.Session;
import com.yanzord.votingappservice.model.Vote;
import com.yanzord.votingappservice.exception.*;
import com.yanzord.votingappservice.service.AppService;
import feign.FeignException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/app")
public class AppController {
    private static final Logger logger = Logger.getLogger(AppController.class);

    @Autowired
    private AppService appService;

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(FeignException.NotFound.class)
    public Map<String, Object> handleFeignNotFoundException(FeignException e, HttpServletResponse response) throws JsonProcessingException {
        logger.error("Error occurred trying to make feign request. Message: " + e.getMessage());
        response.setStatus(e.status());
        return objectMapper.readValue(e.contentUTF8(), new TypeReference<Map<String,Object>>(){});
    }

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
