package com.walmart.app;

import com.walmart.app.repo.Service3Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by wal-mart on 5/16/16.
 *
 * @author: wal-mart
 * @author: grosal3
 */
@Component
public class Service3 {

    @Autowired private Service3Repository repository;

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Logger LOG = LoggerFactory.getLogger(Service3.class);

    @Transactional
    public String anotherCall() throws ExecutionException, InterruptedException {
        LOG.debug("anotherCall in Service 3 inside a future is called:");
        Future<String> result = executorService.submit(()-> repository.query("CREATE (n:Test1 {mya:'test1'}) return n.mya as test"));
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw e;
        } finally {
            executorService.shutdown();
        }
    }

    @Transactional
    public String anotherCallWithError() throws ExecutionException, InterruptedException {
        LOG.debug("anotherCall in Service 3 with error is called:");
        return repository.query("CREATE (n:Test1 {mya:'test1'}) return n.mya as test");
    }
}
