package lu.uni.serval.ikora.evolution.results;

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

import lu.uni.serval.ikora.core.analytics.clones.Clones;
import lu.uni.serval.ikora.core.analytics.clones.KeywordCloneDetection;
import lu.uni.serval.ikora.core.model.*;

import java.util.HashMap;
import java.util.Map;

public class CloneResults {
    private final Map<Projects, Clones<KeywordDefinition>> keywords;

    public CloneResults(){
        this.keywords = new HashMap<>();
    }

    public Clones<KeywordDefinition> getKeywords(Projects version){
        return keywords.computeIfAbsent(version, KeywordCloneDetection::findClones);
    }
}
