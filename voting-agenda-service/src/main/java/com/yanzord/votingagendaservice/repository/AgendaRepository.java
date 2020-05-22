package com.yanzord.votingagendaservice.repository;

import com.yanzord.votingagendaservice.model.Agenda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class AgendaRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public Agenda getAgendaById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));

        return mongoTemplate.findOne(query, Agenda.class);
    }

    public Agenda saveAgenda(Agenda agenda){
        return mongoTemplate.save(agenda);
    }
}
