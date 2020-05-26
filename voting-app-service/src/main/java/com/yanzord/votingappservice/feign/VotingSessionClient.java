package com.yanzord.votingappservice.feign;

import com.yanzord.votingappservice.model.Session;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("voting-session-service")
public interface VotingSessionClient {
    @RequestMapping(value = "/session/", method = RequestMethod.PUT)
    Session saveSession(@RequestBody Session session);

    @RequestMapping(value = "/session/{agendaId}", method = RequestMethod.GET)
    Session getSessionByAgendaId(@PathVariable("agendaId") String agendaId);
}
