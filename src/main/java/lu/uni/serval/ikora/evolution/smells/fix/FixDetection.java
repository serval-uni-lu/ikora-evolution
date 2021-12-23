package lu.uni.serval.ikora.evolution.smells.fix;

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

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellConfiguration;

import java.util.Arrays;
import java.util.Set;

public abstract class FixDetection {
    protected final SmellConfiguration configuration;

    protected FixDetection(SmellConfiguration configuration) {
        this.configuration = configuration;
    }

    protected boolean isDefaultFix(Set<SourceNode> nodes, Edit edit, Edit.Type... types){
        if(Arrays.stream(types).noneMatch(t -> edit.getType() == t)){
            return false;
        }

        return nodes.contains(edit.getLeft());
    }

    public abstract boolean isFix(Set<SourceNode> nodes, Edit edit);
}
