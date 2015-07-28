package com.github.tkobayas.drools.warmup.loadtest.simple;

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

import com.github.tkobayas.drools.warmup.WarmUpHelper;
import com.sample.Employee;
import com.sample.Person;

/**
 * This is a sample class to launch a rule.
 */
public class FullWarmUp100Test extends MultiThreadTestBase {
    
    @Test
    public void testRule() throws Exception {

        final KieBase kBase = setupKieBase();
        
        //------------------------------------
        WarmUpHelper helper = new WarmUpHelper();
        helper.analyze(kBase);

        Object[] facts = new Object[MultiThreadTestBase.RULE_NUM];
        for (int i = 0; i < MultiThreadTestBase.RULE_NUM; i++) {
            facts[i] = new Person("John" + i, i * 5);
        }
        HashMap<String, Object> globalMap = new HashMap<String, Object>();
        globalMap.put("resultList", new ArrayList<String>());
        helper.warmUpWithFacts(facts, globalMap, 100);
        //------------------------------------
        
        runTest(kBase);
        
        assertTrue(true);

    }

}
