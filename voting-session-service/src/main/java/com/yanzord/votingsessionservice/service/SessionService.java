package com.yanzord.votingsessionservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.exception.ClosedSessionException;
import com.yanzord.votingsessionservice.exception.OpenedSessionException;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            fallbackMethod = "defaultGetSessionByAgendaId",
            ignoreExceptions = { OpenedSessionException.class, SessionNotFoundException.class })
    public Session getSessionByAgendaId(String agendaId) throws SessionNotFoundException, ClosedSessionException {
        List<Session> sessions = new ArrayList<>();
        sessionRepository.findAll().forEach(sessions::add);

        Session session = sessions.stream()
                .filter(s -> s.getAgendaId().equals(agendaId))
                .findAny()
                .orElseThrow(() -> new SessionNotFoundException("Session not found for agenda ID: " + agendaId));

        if(LocalDateTime.now().isAfter(session.getEndDate())) {
            sessionRepository.deleteById(session.getId());
            throw new ClosedSessionException("Voting timeout expired, session is currently closed.");
        }

        return session;
    }

    public Session defaultOpenedSession(Session session) {
        return new Session();
    }

    public Session defaultGetSessionByAgendaId(String agendaId) {
        return new Session();
    }
}
