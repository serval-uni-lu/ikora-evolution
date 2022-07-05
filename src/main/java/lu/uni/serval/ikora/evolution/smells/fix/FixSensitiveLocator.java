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
import lu.uni.serval.ikora.core.builder.resolver.ValueResolver;
import lu.uni.serval.ikora.core.model.Argument;
import lu.uni.serval.ikora.core.model.Literal;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.core.model.VariableAssignment;
import lu.uni.serval.ikora.evolution.smells.History;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;
import lu.uni.serval.ikora.smells.utils.LocatorUtils;

import java.util.Set;
import java.util.stream.Stream;

public class FixSensitiveLocator extends FixDetection{
    protected FixSensitiveLocator(SmellConfiguration configuration, History history) {
        super(SmellMetric.Type.SENSITIVE_LOCATOR, configuration, history);
    }

    @Override
    public FixResult getFix(Set<SourceNode> nodes, Edit edit) {
        if(isContaining(nodes, edit)
                && Literal.class.isAssignableFrom(edit.getRight().getClass())
                && !LocatorUtils.isComplex(edit.getRight().getName(), configuration.getMaximumLocatorSize())){
            return getFixResult(edit);
        }

        return FixResult.noFix();
    }

    private boolean isContaining(Set<SourceNode> nodes, Edit edit){
        return nodes.contains(edit.getLeft()) ||
        nodes.stream().filter(Argument.class::isInstance)
                .map(Argument.class::cast)
                .flatMap(n -> ValueResolver.getValueNodes(n).stream())
                .flatMap(n -> n instanceof VariableAssignment ? ((VariableAssignment)n).getValues().stream() : Stream.of(n))
                .anyMatch(n -> n == edit.getLeft() || n == edit.getLeft().getAstParent(false));
    }
}
