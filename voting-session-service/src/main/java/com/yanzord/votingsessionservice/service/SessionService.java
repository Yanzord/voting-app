package com.yanzord.votingsessionservice.service;

import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.exception.ClosedSessionException;
import com.yanzord.votingsessionservice.exception.OpenedSessionException;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionService {
    @Autowired
    private List<Session> openedSessions;

    public Session openSession(Session session) throws OpenedSessionException {
        if(openedSessions.stream().anyMatch(s -> s.getAgendaId().equals(session.getAgendaId()))) {
            throw new OpenedSessionException("Session already opened, can't open it again.");
        }

        LocalDateTime endDate = session.getStartDate().plusMinutes(session.getTimeout());
        session.setEndDate(endDate);
        openedSessions.add(session);

        return session;
    }

    public Session getSessionByAgendaId(String id) throws SessionNotFoundException, ClosedSessionException {
        Session session = openedSessions.stream()
                .filter(s -> s.getAgendaId().equals(id))
                .findAny()
                .orElseThrow(() -> new SessionNotFoundException("Session not found for agenda ID: " + id));

        if(LocalDateTime.now().isAfter(session.getEndDate())) {
            openedSessions.remove(session);
            throw new ClosedSessionException("Voting timeout expired, session is currently closed.");
        }

        return session;
    }
}
