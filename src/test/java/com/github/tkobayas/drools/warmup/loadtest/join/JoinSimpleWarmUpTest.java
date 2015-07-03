package com.github.tkobayas.drools.warmup.loadtest.join;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import com.github.tkobayas.drools.warmup.MvelConstraintOptimizer;
import com.sample.Employee;
import com.sample.Person;

/**
 * This is a sample class to launch a rule.
 */
public class JoinSimpleWarmUpTest extends JoinMultiThreadTestBase {
    
    @Test
    public void testRule() throws Exception {

        final KieBase kBase = setupKieBase();
        
        //------------------------------------
        MvelConstraintOptimizer optimizer = new MvelConstraintOptimizer();
        optimizer.analyze(kBase);
        Person p = new Person("John", Integer.MAX_VALUE);
        Object[] facts = new Object[]{p};
        HashMap<String, Object> globalMap = new HashMap<String, Object>();
        globalMap.put("resultList", new ArrayList<String>());
        optimizer.warmUpWithFacts(facts, globalMap);
        //------------------------------------
        
        runTest(kBase);
        
        assertTrue(true);

    }

}
