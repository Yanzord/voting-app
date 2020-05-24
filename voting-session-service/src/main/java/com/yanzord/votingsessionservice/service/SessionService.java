package com.yanzord.votingsessionservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.exception.ClosedSessionException;
import com.yanzord.votingsessionservice.exception.OpenedSessionException;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.model.Vote;
import com.yanzord.votingsessionservice.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    @HystrixCommand(
            fallbackMethod = "defaultOpenedSession",
            ignoreExceptions = { OpenedSessionException.class })
    public Session openSession(Session session) throws OpenedSessionException {
        List<Session> sessions = new ArrayList<>();
        sessionRepository.findAll().forEach(sessions::add);

        if(sessions.stream().anyMatch(s -> s.getAgendaId().equals(session.getAgendaId()))) {
            throw new OpenedSessionException("Session already opened, can't open it again.");
        }

        LocalDateTime endDate = session.getStartDate().plusMinutes(session.getTimeout());
        session.setEndDate(endDate);

        return sessionRepository.save(session);
    }

    @HystrixCommand(
            fallbackMethod = "defaultRegisterVote",
            ignoreExceptions = { OpenedSessionException.class, SessionNotFoundException.class })
    public Session registerVote(Vote vote, String sessionId) throws SessionNotFoundException, ClosedSessionException {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found. ID: " + sessionId));

        if(LocalDateTime.now().isAfter(session.getEndDate())) {
            sessionRepository.deleteById(session.getId());
            throw new ClosedSessionException("Voting timeout expired, session is currently closed.");
        }

        List<Vote> votes = Optional.ofNullable(session.getVotes())
                .orElse(new ArrayList<>());

        votes.add(vote);
        session.setVotes(votes);
        sessionRepository.save(session);

        return session;
    }

    public Session defaultOpenedSession(Session session) {
        return new Session();
    }

    public Session defaultRegisterVote(Vote vote, String sessionId) {
        return new Session();
    }
}
