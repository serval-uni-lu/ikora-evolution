package tech.ikora.evolution.results;

import tech.ikora.model.TestCase;
import tech.ikora.smells.SmellMetric;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SmellResults {
    private final Map<TestCase, Set<SmellMetric>> testCaseSmells = new HashMap<>();

    void setSmells(TestCase testCase, Set<SmellMetric> smellMetrics){
        testCaseSmells.compute(testCase, (k, v) -> {
            if(v == null){
                return smellMetrics;
            }
            else{
                v.addAll(smellMetrics);
                return v;
            }
        });
    }

    Set<SmellMetric> getSmells(TestCase testCase){
        return testCaseSmells.getOrDefault(testCase, Collections.emptySet());
    }
}
