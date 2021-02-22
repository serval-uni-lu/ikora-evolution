package lu.uni.serval.ikora.evolution.results;

import lu.uni.serval.ikora.analytics.difference.Edit;
import lu.uni.serval.ikora.model.SourceNode;
import lu.uni.serval.ikora.model.TestCase;
import lu.uni.serval.ikora.smells.*;

import java.util.*;

public class SmellRecordAccumulator {
    private final List<Record> records = new ArrayList<>();
    private final Map<SmellMetric.Type, Set<SourceNode>> nodes = new HashMap<>();

    public void addTestCase(String version, TestCase testCase, SmellResults smells, Set<Edit> edits, Map<SmellMetric.Type, Set<SourceNode>> previousNodes, SmellConfiguration configuration){
        updateNodes(smells);

        for(SmellResult smell: smells){
            long fixes = computeFixes(smell.getType(), edits, previousNodes, configuration);
            records.add(new SmellRecord(version, testCase, smell.getType().name(), smell.getValue(), fixes));
        }
    }

    public List<Record> getRecords() {
        return records;
    }

    public Map<SmellMetric.Type, Set<SourceNode>> getNodes() {
        return nodes;
    }

    private void updateNodes(SmellResults smells){
        for(SmellResult smell: smells){
            final Set<SourceNode> nodesByType = nodes.getOrDefault(smell.getType(), new HashSet<>());
            nodesByType.addAll(smell.getNodes());
            nodes.putIfAbsent(smell.getType(), nodesByType);
        }
    }

    private long computeFixes(SmellMetric.Type type, Set<Edit> edits, Map<SmellMetric.Type, Set<SourceNode>> previousNodes, SmellConfiguration configuration){
        if(previousNodes == null){
            return 0;
        }

        return edits.stream().filter(c -> SmellDetector.isFix(type, previousNodes.getOrDefault(type, new HashSet<>()), c, configuration)).count();
    }
}
