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
import lu.uni.serval.ikora.core.model.Projects;
import lu.uni.serval.ikora.core.model.TestCase;
import lu.uni.serval.ikora.core.model.Variable;
import lu.uni.serval.ikora.evolution.smells.History;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;

public class FixOnTheFly extends FixDetection{
    protected FixOnTheFly(SmellConfiguration configuration, History history) {
        super(SmellMetric.Type.ON_THE_FLY, configuration, history);
    }

    @Override
    public FixResult getFix(Projects version, TestCase testCase, Edit edit) {
        if(edit.getLeft() == null){
            return FixResult.noFix();
        }

        if(Variable.class.isAssignableFrom(edit.getLeft().getClass())){
            return getDefaultFix(version, edit, Edit.Type.CHANGE_VALUE_TYPE);
        }

        return FixResult.noFix();
    }
}
