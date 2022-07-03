package lu.uni.serval.ikora.evolution.smells.fix;

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

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.evolution.smells.History;
import lu.uni.serval.ikora.evolution.smells.Sequence;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.util.Arrays;
import java.util.Set;

public abstract class FixDetection {
    protected final SmellMetric.Type type;
    protected final SmellConfiguration configuration;
    protected final History history;

    protected FixDetection(SmellMetric.Type type, SmellConfiguration configuration, History history) {
        this.type = type;
        this.configuration = configuration;
        this.history = history;
    }

    protected FixResult getDefaultFix(Set<SourceNode> nodes, Edit edit, Edit.Type... types){
        if(Arrays.stream(types).anyMatch(t -> edit.getType() == t) && nodes.contains(edit.getLeft())){
            return getFixResult(edit);
        }

        return FixResult.noFix();
    }

    protected FixResult getFixResult(Edit edit){
        final SourceNode fixed = edit.getRight();
        final Sequence sequence = history.findSequence(edit).orElse(new Sequence(type));
        return new FixResult(type, fixed, sequence);
    }

    public abstract FixResult getFix(Set<SourceNode> nodes, Edit edit);
}
