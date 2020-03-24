package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.model.Sequence;
import tech.ikora.model.TestCase;

import java.util.LinkedHashMap;
import java.util.Map;

public class SequenceResults {
    private Map<TestCase, Difference> sequenceDifferences;
    private Map<TestCase, Sequence> sequences;

    public SequenceResults(){
        this.sequenceDifferences = new LinkedHashMap<>();
        this.sequences = new LinkedHashMap<>();
    }

    public Difference getSequenceDifferenceDifference(TestCase testCase){
        return sequenceDifferences.get(testCase);
    }

    public Sequence getSequence(TestCase testCase){
        return sequences.getOrDefault(testCase, new Sequence());
    }

    public void addSequence(TestCase testCase, Sequence sequence){
        if(sequence == null){
            return;
        }

        sequences.put(testCase, sequence);
    }

    public void addDifference(TestCase testCase, Difference sequenceDifference) {
        this.sequenceDifferences.put(testCase, sequenceDifference);
    }
}
