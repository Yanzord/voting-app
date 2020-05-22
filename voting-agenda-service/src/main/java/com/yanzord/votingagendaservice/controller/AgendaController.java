package com.yanzord.votingagendaservice.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.Agenda;
import com.yanzord.votingagendaservice.model.AgendaStatus;
import com.yanzord.votingagendaservice.model.Vote;
import com.yanzord.votingagendaservice.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/voting-agenda")
public class AgendaController {
    @Autowired
    private AgendaService agendaService;
    @Autowired
    private Gson gson;

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Data not found.")
    @ExceptionHandler(AgendaNotFoundException.class)
    public void handleNotFoundException() {
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Agenda> getAllAgendas() {
        return agendaService.getAllAgendas();
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public Agenda getAgendaById(@PathVariable("id") String id) {
        return agendaService.getAgendaById(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Agenda addAgenda(@RequestBody Agenda agenda) {
        return agendaService.addAgenda(agenda);
    }

    @RequestMapping(value = "/open-agenda", method = RequestMethod.POST)
    public Agenda openAgenda(@RequestBody String payload) {
        JsonObject jsonObject = gson.fromJson(payload, JsonObject.class);
        AgendaStatus status = AgendaStatus.valueOf(jsonObject.get("status").getAsString());
        String id = jsonObject.get("id").getAsString();
        String startDate = jsonObject.get("startDate").getAsString();

        return agendaService.openAgenda(id, LocalDateTime.parse(startDate, getFormatter()), status);
    }

    @RequestMapping(value = "/close-agenda", method = RequestMethod.POST)
    public Agenda closeAgenda(@RequestBody String payload) {
        JsonObject jsonObject = gson.fromJson(payload, JsonObject.class);
        AgendaStatus status = AgendaStatus.valueOf(jsonObject.get("status").getAsString());
        List<Vote> votes = new ArrayList<>();
        String id = jsonObject.get("id").getAsString();
        String endDate = jsonObject.get("endDate").getAsString();
        JsonArray votesJsonArray = jsonObject.getAsJsonArray("votes");

        for (JsonElement element : votesJsonArray) {
            Vote vote = gson.fromJson(element, Vote.class);
            votes.add(vote);
        }

        return agendaService.closeAgenda(id, votes, LocalDateTime.parse(endDate, getFormatter()), status);
    }

    private DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }
}
