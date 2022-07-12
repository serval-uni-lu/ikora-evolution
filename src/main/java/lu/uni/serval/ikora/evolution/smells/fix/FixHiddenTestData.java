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
import lu.uni.serval.ikora.core.model.Assignment;
import lu.uni.serval.ikora.core.model.KeywordCall;
import lu.uni.serval.ikora.core.model.Projects;
import lu.uni.serval.ikora.core.model.TestCase;
import lu.uni.serval.ikora.evolution.smells.History;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.util.Optional;

public class FixHiddenTestData extends FixDetection{
    protected FixHiddenTestData(SmellConfiguration configuration, History history) {
        super(SmellMetric.Type.HIDING_TEST_DATA, configuration, history);
    }

    @Override
    public FixResult getFix(Projects version, TestCase testCase, Edit edit) {
        if(!edit.getType().equals(Edit.Type.REMOVE_STEP)){
            return FixResult.noFix();
        }

        FixResult result = getDefaultFix(version, edit, Edit.Type.REMOVE_STEP);
        if(result.isValid()){
            return getFixResult(version, edit);
        }

        if(edit.getLeft() instanceof Assignment){
            final Optional<KeywordCall> keywordCall = ((Assignment) edit.getLeft()).getKeywordCall();

            if(keywordCall.isPresent() && getPreviousSmellyNodes(version).contains(keywordCall.get())){
                return getFixResult(version, edit);
            }
        }

        return FixResult.noFix();
    }
}
