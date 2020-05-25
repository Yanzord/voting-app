package com.yanzord.votingappservice.feign;

import com.yanzord.votingappservice.dto.SessionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("voting-session-service")
public interface VotingSessionClient {
    @RequestMapping(value = "/session/{agendaId}", method = RequestMethod.GET)
    SessionDTO getSessionByAgendaId(@PathVariable("agendaId") String agendaId);

    @RequestMapping(value = "/session/", method = RequestMethod.POST)
    SessionDTO createSession(@RequestBody SessionDTO sessionDTO);

    @RequestMapping(value = "/session/", method = RequestMethod.PUT)
    SessionDTO updateSession(@RequestBody SessionDTO sessionDTO);
}
