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
import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.evolution.smells.History;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;

public class FixMissingAssertion extends FixDetection{
    protected FixMissingAssertion(SmellConfiguration configuration, History history) {
        super(SmellMetric.Type.MISSING_ASSERTION, configuration, history);
    }

    @Override
    public FixResult getFix(Projects version, Edit edit) {
        if(edit.getRight() == null){
            return FixResult.noFix();
        }

        if(edit.getType() == Edit.Type.ADD_STEP && isAddAssertion((Step)edit.getRight())){
            return getFixResult(version, edit);
        }

        if(edit.getType() == Edit.Type.ADD_USER_KEYWORD && isAddAssertion((UserKeyword) edit.getRight())){
            return getFixResult(version, edit);
        }

        return FixResult.noFix();
    }

    private static boolean isAddAssertion(Step step){
        return step.getKeywordCall()
                .flatMap(Step::getKeywordCall)
                .map(KeywordCall::getKeywordType)
                .map(t -> t == Keyword.Type.ASSERTION)
                .orElse(false);
    }

    private static boolean isAddAssertion(UserKeyword keyword){
        return keyword.getSteps().stream()
                .anyMatch(FixMissingAssertion::isAddAssertion);
    }
}
