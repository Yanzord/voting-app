package com.yanzord.votingsessionservice.repository;

import com.yanzord.votingsessionservice.model.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends CrudRepository<Session, String> {
    Session findByAgendaId(String id);
}
