package com.yanzord.votingsessionservice.service;

import com.yanzord.votingsessionservice.dto.SessionDTO;
import com.yanzord.votingsessionservice.exception.OpenedSessionException;
import com.yanzord.votingsessionservice.exception.SessionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private List<SessionDTO> openedSessions;

    public SessionDTO openSession(SessionDTO sessionDTO) {
        Optional<SessionDTO> sessionDTOOptional = openedSessions.stream()
                .filter(session -> session.getAgendaId().equals(sessionDTO.getAgendaId()))
                .findAny();

        if(sessionDTOOptional.isPresent()) {
            throw new OpenedSessionException("Session already opened, can't open it again.");
        }

        LocalDateTime endDate = sessionDTO.getStartDate().plusMinutes(sessionDTO.getTimeout());
        sessionDTO.setEndDate(endDate);
        openedSessions.add(sessionDTO);

        return sessionDTO;
    }

    public SessionDTO getSessionByAgendaId(String id) {
        return openedSessions.stream()
                .filter(sessionDTO -> sessionDTO.getAgendaId().equals(id))
                .findAny()
                .orElseThrow(() -> new SessionNotFoundException("Session not found for agenda ID: " + id));
    }
}
