package com.yanzord.votingsessionservice.repository;

import com.yanzord.votingsessionservice.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SessionRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Optional<Session> getSessionByAgendaId(String agendaId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("agendaId").is(agendaId));

        return Optional.ofNullable(mongoTemplate.findOne(query, Session.class));
    }

    public Session save(Session session) {
        return mongoTemplate.save(session);
    }
}
