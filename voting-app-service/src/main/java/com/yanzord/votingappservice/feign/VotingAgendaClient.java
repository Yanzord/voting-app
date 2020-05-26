package com.yanzord.votingappservice.feign;

import com.yanzord.votingappservice.model.Agenda;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("voting-agenda-service")
public interface VotingAgendaClient {
    @RequestMapping(value = "/agenda/", method = RequestMethod.PUT)
    Agenda saveAgenda(@RequestBody Agenda agenda);

    @RequestMapping(value = "/agenda/{id}", method = RequestMethod.GET)
    Agenda getAgendaById(@PathVariable("id") String id);
}
