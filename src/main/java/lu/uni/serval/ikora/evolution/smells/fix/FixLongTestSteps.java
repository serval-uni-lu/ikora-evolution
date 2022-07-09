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

import lu.uni.serval.ikora.core.analytics.KeywordStatistics;
import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.KeywordDefinition;
import lu.uni.serval.ikora.core.model.Projects;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.core.model.Step;
import lu.uni.serval.ikora.core.utils.Cfg;
import lu.uni.serval.ikora.evolution.smells.History;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.util.Optional;

public class FixLongTestSteps extends FixDetection{
    protected FixLongTestSteps(SmellConfiguration configuration, History history) {
        super(SmellMetric.Type.LONG_TEST_STEPS, configuration, history);
    }

    @Override
    public FixResult getFix(Projects version, Edit edit) {
        final KeywordDefinition previousStep = getPreviousStep(edit, version);

        if(previousStep == null){
            return FixResult.noFix();
        }

        if(KeywordStatistics.getSequenceSize(previousStep) > configuration.getMaximumStepSize()){
            return getFixResult(version, edit);
        }

        return FixResult.noFix();
    }

    private KeywordDefinition getPreviousStep(Edit edit, Projects version){
        if(edit.getType() != Edit.Type.REMOVE_STEP){
            return null;
        }

        if(!Step.class.isAssignableFrom(edit.getLeft().getClass())){
            return null;
        }

        return getRelevantStep((Step) edit.getLeft(), version);
    }

    private KeywordDefinition getRelevantStep(Step step, Projects version){
        for(SourceNode node: getPreviousSmellyNodes(version)){
            final Optional<KeywordDefinition> parent = Cfg.getCallerByName(step, node.getDefinitionToken());

            if(parent.isPresent() ){
                return parent.get();
            }
        }

        return null;
    }
}
