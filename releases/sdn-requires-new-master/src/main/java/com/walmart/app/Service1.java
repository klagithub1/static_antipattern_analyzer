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
public class Service1 {

    private static final Logger LOG = LoggerFactory.getLogger(Service1.class);

    @Autowired private Service2 sv2;

    @Transactional
    public String aCall() throws ExecutionException, InterruptedException {
        LOG.debug("aCall in Service 1 is Called:");
        String result = sv2.aSecondCall();
        return result;
    }

    @Transactional
    public String aCallWithError() throws ExecutionException, InterruptedException {
        LOG.debug("aCall in Service 1 with Error is Called:");
        String result = sv2.aSecondCallWithError();
        return result;
    }

}
