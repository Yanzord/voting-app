package com.yanzord.votingappservice.feign;

import com.yanzord.votingappservice.dto.SessionDTO;
import com.yanzord.votingappservice.dto.VoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("voting-session-service")
public interface VotingSessionClient {
    @RequestMapping(value = "/session/", method = RequestMethod.POST)
    SessionDTO openSession(SessionDTO sessionDTO);

    @RequestMapping(value = "/session/{sessionId}", method = RequestMethod.PATCH)
    SessionDTO registerVote(VoteDTO voteDTO, String sessionId);
}
