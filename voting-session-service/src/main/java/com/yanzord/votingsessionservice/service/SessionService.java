package com.yanzord.votingsessionservice.service;

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
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    public Session openSession(Session session) throws OpenedSessionException {
        List<Session> openedSessions = new ArrayList<>();
        sessionRepository.findAll().forEach(openedSessions::add);

        if(openedSessions.stream().anyMatch(s -> s.getAgendaId().equals(session.getAgendaId()))) {
            throw new OpenedSessionException("Session already opened, can't open it again.");
        }

        LocalDateTime endDate = session.getStartDate().plusMinutes(session.getTimeout());
        session.setEndDate(endDate);
        sessionRepository.save(session);

        return session;
    }

    public Session getSessionByAgendaId(String agendaId) throws SessionNotFoundException, ClosedSessionException {
        Session session = Optional.ofNullable(sessionRepository.findByAgendaId(agendaId))
                .orElseThrow(() -> new SessionNotFoundException("Session not found for agenda ID: " + agendaId));

        if(LocalDateTime.now().isAfter(session.getEndDate())) {
            sessionRepository.deleteById(session.getId());
            throw new ClosedSessionException("Voting timeout expired, session is currently closed.");
        }

        return session;
    }
}
