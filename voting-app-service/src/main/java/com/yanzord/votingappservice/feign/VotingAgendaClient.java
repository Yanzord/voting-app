package com.yanzord.votingappservice.feign;

import com.yanzord.votingappservice.dto.AgendaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("voting-agenda-service")
public interface VotingAgendaClient {
    @RequestMapping(value = "/agenda/", method = RequestMethod.POST)
    AgendaDTO registerAgenda(@RequestBody AgendaDTO agendaDTO);

    @RequestMapping(value = "/agenda/{id}", method = RequestMethod.GET)
    AgendaDTO getAgendaById(@PathVariable("id") String id);

    @RequestMapping(value = "/agenda/", method = RequestMethod.POST)
    AgendaDTO updateAgenda(@RequestBody AgendaDTO agendaDTO);
}
