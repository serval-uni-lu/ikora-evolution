package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.evolution.differences.SmellFixes;
import tech.ikora.model.TestCase;
import tech.ikora.smells.SmellMetric;
import tech.ikora.smells.SmellResult;
import tech.ikora.smells.SmellResults;

import java.util.*;

public class SmellRecords {
    private final List<Record> records = new ArrayList<>();
    private final Map<TestCase, SmellResults> testCaseToSmellResults = new HashMap<>();

    public void addTestCase(Date date, TestCase testCase, SmellResults smells, DifferenceResults differences, SmellRecords previous){
        testCaseToSmellResults.put(testCase, smells);

        for(SmellResult smell: smells){
            long changes = computeChanges(smell.getType(), testCase, differences, previous);
            records.add(new SmellRecord(date, testCase, smell.getType().name(), smell.getValue(), changes));
        }
    }

    public List<Record> getRecords() {
        return records;
    }

    public SmellResults getSmellResults(TestCase testCase){
        return testCaseToSmellResults.getOrDefault(testCase, new SmellResults());
    }

    private long computeChanges(SmellMetric.Type type, TestCase testCase, DifferenceResults differences, SmellRecords previous){
        if(previous == null){
            return 0;
        }

        final Optional<TestCase> previousTestCase = differences.getPrevious(testCase);

        if(!previousTestCase.isPresent()){
            return 0;
        }

        final Set<Difference> testDifferences = differences.getDifferences(testCase);
        final SmellResults previousSmellResults = previous.getSmellResults(previousTestCase.get());

        return SmellFixes.count(type, previousSmellResults.getNodes(type), testDifferences);
    }
}
