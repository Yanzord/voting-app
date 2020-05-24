package com.yanzord.votingappservice.controller;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app")
public class AppController {
    @Autowired
    private AppService appService;

    @RequestMapping(value = "/agenda/register", method = RequestMethod.POST)
    public AgendaDTO registerAgenda(@RequestBody AgendaDTO agendaDTO) {
        return appService.registerAgenda(agendaDTO);
    }

    @RequestMapping(value = "/agenda", method = RequestMethod.GET)
    public List<AgendaDTO> getAllAgendas() {
        return appService.getAllAgendas();
    }
}
