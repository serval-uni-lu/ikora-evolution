package tech.ikora.evolution.results;

import tech.ikora.analytics.Edit;
import tech.ikora.model.SourceNode;
import tech.ikora.model.TestCase;
import tech.ikora.smells.*;

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
