package lu.uni.serval.ikora.evolution.smells;

import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.core.analytics.difference.Edit;

import lu.uni.serval.ikora.smells.*;

import lu.uni.serval.ikora.evolution.results.Record;
import lu.uni.serval.ikora.evolution.results.SmellRecord;

import java.util.*;

public class SmellRecordAccumulator {
    private final List<Record> records = new ArrayList<>();
    private final Map<SmellMetric.Type, Set<SourceNode>> nodes = new EnumMap<>(SmellMetric.Type.class);

    public void addTestCase(String version, TestCase testCase, SmellResults smells, Set<Edit> edits, Map<SmellMetric.Type, Set<SourceNode>> previousNodes, SmellConfiguration configuration){
        updateNodes(smells);

        for(SmellResult smell: smells){
            long fixes = FixCounter.count(smell.getType(), edits, previousNodes, configuration);
            records.add(new SmellRecord(version, testCase, smell.getType().name(), smell.getRawValue(), smell.getNormalizedValue(), fixes));
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
}
