package com.yanzord.votingsessionservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yanzord.votingsessionservice.model.Session;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import com.yanzord.votingsessionservice.model.SessionStatus;
import com.yanzord.votingsessionservice.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;
    private final Session DEFAULT_SESSION = new Session("1", "1", 1, SessionStatus.CLOSED);

    @HystrixCommand(fallbackMethod = "defaultSaveSession")
    public Session saveSession(Session session) {
        return sessionRepository.save(session);
    }

    @HystrixCommand(
            fallbackMethod = "defaultGetSessionByAgendaId",
            ignoreExceptions = { SessionNotFoundException.class })
    public Session getSessionByAgendaId(String agendaId) throws SessionNotFoundException {
        return sessionRepository.getSessionByAgendaId(agendaId)
                .orElseThrow(SessionNotFoundException::new);
    }

    public Session defaultSaveSession(Session session) {
        return DEFAULT_SESSION;
    }

    public Session defaultGetSessionByAgendaId(String agendaId) {
        return DEFAULT_SESSION;
    }
}
