package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.model.SourceNode;
import tech.ikora.model.TestCase;
import tech.ikora.smells.*;

import java.util.*;

public class SmellRecordAccumulator {
    private final List<Record> records = new ArrayList<>();
    private final Map<TestCase, SmellResults> results = new HashMap<>();

    public void addTestCase(String version, TestCase testCase, SmellResults smells, DifferenceResults differences, Map<TestCase, SmellResults> previousResults, SmellConfiguration configuration){
        results.put(testCase, smells);

        for(SmellResult smell: smells){
            long fixes = computeFixes(smell.getType(), testCase, differences, previousResults, configuration);
            records.add(new SmellRecord(version, testCase, smell.getType().name(), smell.getValue(), fixes));
        }
    }

    public List<Record> getRecords() {
        return records;
    }

    public Map<TestCase, SmellResults> getResults() {
        return this.results;
    }

    private long computeFixes(SmellMetric.Type type, TestCase testCase, DifferenceResults differences, Map<TestCase, SmellResults> previousResults, SmellConfiguration configuration){
        if(previousResults == null){
            return 0;
        }

        final Optional<TestCase> previousTestCase = differences.getPrevious(testCase);

        if(!previousTestCase.isPresent()){
            return 0;
        }

        final Set<Difference> changes = differences.getDifferences(testCase);
        final Set<SourceNode> smellyNodes = previousResults.getOrDefault(previousTestCase.get(), new SmellResults()).getNodes(type);

        return changes.stream().filter(c -> SmellDetector.isFix(type, smellyNodes, c, configuration)).count();
    }
}
