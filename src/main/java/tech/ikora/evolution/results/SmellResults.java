package tech.ikora.evolution.results;

import tech.ikora.model.TestCase;
import tech.ikora.smells.SmellMetric;

import java.util.*;

public class SmellResults implements Iterable<SmellResults.Record> {
    private final List<Record> records = new ArrayList<>();

    void setSmells(String versionId, TestCase testCase, Set<SmellMetric> smellMetrics){
        for(SmellMetric smellMetric: smellMetrics){
            records.add(new Record(versionId, testCase, smellMetric));
        }
    }

    @Override
    public Iterator<Record> iterator() {
        return records.iterator();
    }

    public static class Record{
        private final String version;
        private final TestCase testCase;
        private final SmellMetric smellMetric;

        public Record(String version, TestCase testCase, SmellMetric smell) {
            this.version = version;
            this.testCase = testCase;
            this.smellMetric = smell;
        }

        public String getVersion() {
            return version;
        }

        public TestCase getTestCase() {
            return testCase;
        }

        public SmellMetric getSmellMetric() {
            return smellMetric;
        }
    }
}
