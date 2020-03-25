package tech.ikora.evolution.results;

import tech.ikora.model.TestCase;
import tech.ikora.smells.SmellMetric;

import java.util.*;

public class SmellResults implements Iterable<SmellResults.Record> {
    private final Set<SmellMetric.Type> smellTypes = new HashSet<>();
    private final List<Record> records = new ArrayList<>();

    void setSmells(String versionId, TestCase testCase, Map<SmellMetric.Type, SmellMetric> smellMetrics){
        smellTypes.addAll(smellMetrics.keySet());
        records.add(new Record(versionId, testCase, smellMetrics));
    }

    public Set<SmellMetric.Type> getSmellTypes(){
        return smellTypes;
    }

    @Override
    public Iterator<Record> iterator() {
        return records.iterator();
    }

    public class Record{
        private final String version;
        private final TestCase testCase;
        private final Map<SmellMetric.Type, SmellMetric> smells;

        public Record(String version, TestCase testCase, Map<SmellMetric.Type, SmellMetric> smells) {
            this.version = version;
            this.testCase = testCase;
            this.smells = smells;
        }

        public String getVersion() {
            return version;
        }

        public TestCase getTestCase() {
            return testCase;
        }

        public double getSmellMetrics(SmellMetric.Type type) {
            return smells.getOrDefault(type, SmellMetric.nan(type)).getValue();
        }
    }
}
