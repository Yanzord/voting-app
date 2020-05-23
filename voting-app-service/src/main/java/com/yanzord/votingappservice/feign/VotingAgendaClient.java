package com.yanzord.votingappservice.feign;

import com.yanzord.votingappservice.dto.AgendaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("voting-agenda-service")
public interface VotingAgendaClient {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    AgendaDTO addAgenda(AgendaDTO agendaDTO);
}
