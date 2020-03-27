package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.model.TestCase;
import tech.ikora.smells.SmellMetric;

import java.util.*;

public class SmellResults {
    private final List<SmellRecord> records = new ArrayList<>();

    public void addTestCase(Date date, TestCase testCase, Set<SmellMetric> smellMetrics, Set<Difference> changes){
        for(SmellMetric smellMetric: smellMetrics){
            records.add(new SmellRecord(date, testCase, smellMetric, changes.size()));
        }
    }

    public List<? extends CsvRecord> getRecords() {
        return records;
    }
}
