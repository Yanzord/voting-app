package com.yanzord.votingagendaservice.repository;

import com.yanzord.votingagendaservice.model.VotingAgenda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class VotingAgendaRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public VotingAgenda getVotingAgendaById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));

        return mongoTemplate.findOne(query, VotingAgenda.class);
    }

    public VotingAgenda saveVotingAgenda(VotingAgenda votingAgenda){
        return mongoTemplate.save(votingAgenda);
    }
}
