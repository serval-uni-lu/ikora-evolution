package lu.uni.serval.ikora.evolution.smells;

import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.core.analytics.difference.Edit;

import lu.uni.serval.ikora.smells.*;

import lu.uni.serval.ikora.evolution.results.ChangeRecord;
import lu.uni.serval.ikora.evolution.results.SmellRecord;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class SmellRecordAccumulator {
    private final List<ChangeRecord> records = new ArrayList<>();
    private final Map<SmellMetric.Type, Set<SourceNode>> nodes = new EnumMap<>(SmellMetric.Type.class);

    public void addTestCase(String version,
                            TestCase testCase,
                            SmellResults smells,
                            Set<Edit> edits,
                            Set<Pair<? extends SourceNode, ? extends SourceNode>> pairs,
                            Map<SmellMetric.Type, Set<SourceNode>> previousNodes,
                            SmellConfiguration configuration){
        updateNodes(smells);

        for(SmellResult smell: smells){
            long fixes = 0;

            if(previousNodes != null){
                fixes = FixCounter.count(testCase, smell.getType(), edits, pairs, previousNodes.getOrDefault(smell.getType(), new HashSet<>()), configuration);
            }

            records.add(new SmellRecord(version, testCase, smell.getType().name(), smell.getRawValue(), smell.getNormalizedValue(), fixes));
        }
    }

    public List<ChangeRecord> getRecords() {
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
