package com.yanzord.votingappservice.feign;

import com.yanzord.votingappservice.dto.AgendaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("voting-agenda-service")
public interface VotingAgendaClient {
    @RequestMapping(value = "/agenda/", method = RequestMethod.POST)
    AgendaDTO registerAgenda(AgendaDTO agendaDTO);

    @RequestMapping(value = "/agenda/", method = RequestMethod.GET)
    List<AgendaDTO> getAllAgendas();
}
