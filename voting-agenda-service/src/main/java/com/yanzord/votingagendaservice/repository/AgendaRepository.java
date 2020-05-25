package com.yanzord.votingagendaservice.repository;

import com.yanzord.votingagendaservice.model.Agenda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AgendaRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Optional<Agenda> getAgendaById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));

        return Optional.ofNullable(mongoTemplate.findOne(query, Agenda.class));
    }

    public Agenda save(Agenda agenda){
        return mongoTemplate.save(agenda);
    }
}
