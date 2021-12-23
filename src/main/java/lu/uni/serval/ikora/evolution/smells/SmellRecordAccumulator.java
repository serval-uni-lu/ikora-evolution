package lu.uni.serval.ikora.evolution.smells;

/*-
 * #%L
 * Ikora Evolution
 * %%
 * Copyright (C) 2020 - 2021 University of Luxembourg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.core.analytics.difference.Edit;

import lu.uni.serval.ikora.evolution.smells.fix.FixCounter;
import lu.uni.serval.ikora.smells.*;

import lu.uni.serval.ikora.evolution.results.BaseRecord;
import lu.uni.serval.ikora.evolution.results.SmellRecord;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class SmellRecordAccumulator {
    private final List<BaseRecord> records = new ArrayList<>();
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

    public List<BaseRecord> getRecords() {
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
