package com.yanzord.votingappservice.feign;

import com.yanzord.votingappservice.dto.SessionDTO;
import com.yanzord.votingappservice.dto.VoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("voting-session-service")
public interface VotingSessionClient {
    @RequestMapping(value = "/session/", method = RequestMethod.POST)
    SessionDTO openSession(@RequestBody SessionDTO sessionDTO);

    @RequestMapping(value = "/session/{agendaId}", method = RequestMethod.POST)
    SessionDTO registerVote(@RequestBody VoteDTO voteDTO, @PathVariable("agendaId") String agendaId);
}
