package com.walmart.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

/**
 * Created by wal-mart on 5/16/16.
 *
 * @author: wal-mart
 * @author: grosal3
 */
@Component
public class Service2 {

    @Autowired private Service3 sv3;
    private static final Logger LOG = LoggerFactory.getLogger(Service2.class);

    @Transactional
    public String aSecondCall() throws ExecutionException, InterruptedException {
        LOG.debug("aSecondCall is called in Service 2");
        String result = sv3.anotherCall();
        return result;
    }

    @Transactional
    public String aSecondCallWithError() throws ExecutionException, InterruptedException {
        LOG.debug("aSecondCallWithError is called in Service 2");
        String result = sv3.anotherCallWithError();
        return result;
    }

}
