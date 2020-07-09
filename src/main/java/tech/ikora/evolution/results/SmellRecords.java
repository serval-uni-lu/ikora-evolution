package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.model.TestCase;
import tech.ikora.smells.SmellResult;
import tech.ikora.smells.SmellResults;

import java.util.*;

public class SmellRecords {
    private final List<SmellRecord> records = new ArrayList<>();

    public void addTestCase(Date date, TestCase testCase, SmellResults smellResults, Set<Difference> changes){
        for(SmellResult smellResult: smellResults){
            records.add(new SmellRecord(date, testCase, smellResult.getType().name(), smellResult.getValue(), changes.size()));
        }
    }

    public List<? extends CsvRecord> getRecords() {
        return records;
    }
}
