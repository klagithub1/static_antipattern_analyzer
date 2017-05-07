package com.walmart.app.repo;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

/**
 * Created by wal-mart on 5/16/16.
 *
 * @author: wal-mart
 * @author: grosal3
 */
@Repository
public class Service3RepoImpl implements  Service3Repository
{
    @Autowired
    private Neo4jOperations ops;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String query(String s) {
        Result result = ops.query(s, new HashMap<>());
        return result.queryResults().iterator().next().get("test").toString();
    }
}
