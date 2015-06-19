package com.github.tkobayas.drools.warmup;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.common.BaseNode;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.Sink;
import org.drools.core.rule.constraint.ConditionEvaluator;
import org.drools.core.rule.constraint.MvelConditionEvaluator;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Constraint;
import org.kie.api.KieBase;

public class MvelConstraintCollector {

    private boolean dump = false;
    
    private Set<MvelConstraint> mvelConstraintSet = new HashSet<MvelConstraint>();

    public MvelConstraintCollector() {
        this.dump = false;
    }

    public MvelConstraintCollector(boolean dump) {
        this.dump = dump;
    }

    public void traverseRete(KieBase kbase) {
        traverseRete(((KnowledgeBaseImpl) kbase).getRete());
        
        if (dump) {
            System.out.println();
            System.out.println("-------------------------------");
            System.out.println();
        
            dumpMvelConstraint();
        }
    }

    public void dumpMvelConstraint() {
        for (MvelConstraint mvelConstraint : mvelConstraintSet) {
            boolean jitDone = MvelConstraintUtils.isJitDone(mvelConstraint);
            int invocationCounter = MvelConstraintUtils.getInvocationCounter(mvelConstraint);
            String status = jitDone ? "jit" : "mvel";
            System.out.println("[" + Integer.toHexString(mvelConstraint.hashCode()) + ":" + invocationCounter + ":" + status + "] " + mvelConstraint);
        }
        System.out.println();
        System.out.println("--- mvelConstraintSet.size() = " + mvelConstraintSet.size());
    }


    public void traverseRete(Rete rete) {
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            traverseNode(entryPointNode, "");
        }
    }

    private void traverseNode(BaseNode node, String indent) {
        String additionalInfo = "";
        if (node instanceof AlphaNode) {
            Constraint constraint = ((AlphaNode) node).getConstraint();
            additionalInfo = constraint.getClass().getSimpleName() + " : " + constraint;
            if (constraint instanceof MvelConstraint) {
                mvelConstraintSet.add((MvelConstraint) constraint);
            }
        }
        if (node instanceof BetaNode) {
            BetaNodeFieldConstraint[] constraints = ((BetaNode) node).getConstraints();
            for (BetaNodeFieldConstraint constraint : constraints) {
                additionalInfo += constraint.getClass().getSimpleName() + " : " + constraint + ", ";
                if (constraint instanceof MvelConstraint) {
                    mvelConstraintSet.add((MvelConstraint) constraint);
                }
            }
        }

        if (dump) {
            System.out.println(indent + node + (additionalInfo.isEmpty() ? "" : " ---> " + additionalInfo));
        }

        Sink[] sinks = null;
        if (node instanceof EntryPointNode) {
            EntryPointNode source = (EntryPointNode) node;
            Collection<ObjectTypeNode> otns = source.getObjectTypeNodes().values();
            sinks = otns.toArray(new Sink[otns.size()]);
        } else if (node instanceof ObjectSource) {
            ObjectSource source = (ObjectSource) node;
            sinks = source.getSinkPropagator().getSinks();
        } else if (node instanceof LeftTupleSource) {
            LeftTupleSource source = (LeftTupleSource) node;
            sinks = source.getSinkPropagator().getSinks();
        }
        if (sinks != null) {
            for (Sink sink : sinks) {
                if (sink instanceof BaseNode) {
                    traverseNode((BaseNode) sink, indent + "  ");
                }
            }
        }
    }

    public Set<MvelConstraint> getMvelConstraintSet() {
        return mvelConstraintSet;
    }

    public void setMvelConstraintSet(Set<MvelConstraint> mvelConstraintSet) {
        this.mvelConstraintSet = mvelConstraintSet;
    }
}
