package com.yanzord.votingsessionservice.controller;

import com.yanzord.votingsessionservice.dto.SessionDTO;
import com.yanzord.votingsessionservice.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
public class SessionController {
    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/open", method = RequestMethod.POST)
    private SessionDTO openSession(@RequestBody SessionDTO sessionDTO) {
        return sessionService.openSession(sessionDTO);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    private SessionDTO getSessionByAgendaId(@PathVariable("id") String id) {
        return sessionService.getSessionByAgendaId(id);
    }
}
