package com.walmart.app;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.neo4j.ogm.exception.TransactionManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutionException;

/**
 * Created by wal-mart on 5/17/16.
 *
 * @author: wal-mart
 * @author: grosal3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class TestService {

    private static final Logger LOG = LoggerFactory.getLogger(TestService.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired private Service1 test1;

    @Test
    public void testTransaction() throws ExecutionException, InterruptedException {
        LOG.warn("Testing using a future when require new is used...");
        String test = test1.aCall();
        Assert.assertEquals("test1", test);
    }

    @Test
    public void testTransactionWithError() throws ExecutionException, InterruptedException {
        thrown.expect(TransactionManagerException.class);
        LOG.warn("Testing expect exception when require new is used...");
        test1.aCallWithError();
    }


}
