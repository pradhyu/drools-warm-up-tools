package com.github.tkobayas.drools.warmup.sandbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.rule.constraint.MvelConstraint;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;

import com.github.tkobayas.drools.warmup.KieBaseAnalyzer;
import com.github.tkobayas.drools.warmup.WarmUpHelper;
import com.sample.Employee;
import com.sample.Person;

/**
 * Not JUnit TestCase at this moment
 */
public class SandBox5 {

    public static final void main(String[] args) {
        try {

            System.setProperty("drools.dump.dir", "/home/tkobayas/tmp");

            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem();
            kfs.write("src/main/resources/Sample_OR.drl", ks.getResources().newClassPathResource("Sample_OR.drl"));
            ks.newKieBuilder( kfs ).buildAll();
            KieContainer kContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
            KieBase kbase = kContainer.getKieBase();
            
            WarmUpHelper helper = new WarmUpHelper();
            helper.analyze(kbase, true);
            helper.optimizeAlphaNodeConstraints();
            
            System.out.println();

            KieSession kSession = kbase.newKieSession();

            // go !
            Person john = new Person("John", 88);
            kSession.insert(john);

            kSession.fireAllRules();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
