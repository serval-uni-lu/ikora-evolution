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
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellConfiguration;

import java.util.Optional;
import java.util.Set;

public class FixHiddenTestData extends FixDetection{
    protected FixHiddenTestData(SmellConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isFix(Set<SourceNode> nodes, Edit edit) {
        if(!edit.getType().equals(Edit.Type.REMOVE_STEP)){
            return false;
        }

        if(isDefaultFix(nodes, edit, Edit.Type.REMOVE_STEP)){
            return true;
        }

        if(edit.getLeft() instanceof Assignment){
            final Optional<KeywordCall> keywordCall = ((Assignment) edit.getLeft()).getKeywordCall();

            if(keywordCall.isPresent()){
                return nodes.contains(keywordCall.get());
            }
        }

        return false;
    }
}
