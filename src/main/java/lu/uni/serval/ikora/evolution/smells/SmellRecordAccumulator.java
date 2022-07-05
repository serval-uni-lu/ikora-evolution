package lu.uni.serval.ikora.evolution.smells;

/*-
 * #%L
 * Ikora Evolution
 * %%
 * Copyright (C) 2020 - 2022 University of Luxembourg
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

import lu.uni.serval.ikora.evolution.versions.Changes;
import lu.uni.serval.ikora.evolution.smells.fix.FixAccumulator;
import lu.uni.serval.ikora.evolution.smells.fix.FixResult;
import lu.uni.serval.ikora.smells.*;

import lu.uni.serval.ikora.evolution.results.BaseRecord;
import lu.uni.serval.ikora.evolution.results.SmellRecord;

import java.util.*;

public class SmellRecordAccumulator {
    private final List<BaseRecord> records = new ArrayList<>();
    private final Map<SmellMetric.Type, Set<SourceNode>> nodes = new EnumMap<>(SmellMetric.Type.class);

    public void addTestCase(String version,
                            TestCase testCase,
                            SmellResults smells,
                            Changes changes,
                            Map<SmellMetric.Type, Set<SourceNode>> previousNodes,
                            SmellConfiguration configuration,
                            History history){
        updateNodes(smells);

        for(SmellResult smell: smells){
            history.addSmell(smell, changes);
            Set<FixResult> fixes = Collections.emptySet();

            if(previousNodes != null){
                final Set<SourceNode> previous = previousNodes.getOrDefault(smell.getType(), new HashSet<>());
                fixes = FixAccumulator.collect(testCase, smell.getType(), changes, previous, configuration, history);
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
