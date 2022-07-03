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
import lu.uni.serval.ikora.core.model.Keyword;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.evolution.smells.History;
import lu.uni.serval.ikora.smells.NodeUtils;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.util.Set;

public class FixConditionalAssertion extends FixDetection{
    protected FixConditionalAssertion(SmellConfiguration configuration, History history) {
        super(SmellMetric.Type.CONDITIONAL_ASSERTION, configuration, history);
    }

    @Override
    public FixResult getFix(Set<SourceNode> nodes, Edit edit) {
        if(nodes.contains(edit.getLeft()) && NodeUtils.isCallType(edit.getRight(), Keyword.Type.ASSERTION, true)){
            return getFixResult(edit);
        }

        return FixResult.noFix();
    }
}
