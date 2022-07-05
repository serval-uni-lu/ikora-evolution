package lu.uni.serval.ikora.evolution.utils;

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

import lu.uni.serval.ikora.core.model.SourceNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.Set;

public class VersionUtils {
    private VersionUtils() {}

    public static Optional<SourceNode> findOther(Set<Pair<? extends SourceNode, ? extends SourceNode>> pairs, SourceNode node){
        for(Pair<? extends SourceNode, ? extends SourceNode> pair: pairs){
            if(pair.getLeft() == node){
                return Optional.ofNullable(pair.getRight());
            }

            if(pair.getRight() == node) {
                return Optional.ofNullable(pair.getLeft());
            }
        }

        return Optional.empty();
    }

    public static Optional<SourceNode> matchPrevious(Set<Pair<? extends SourceNode, ? extends SourceNode>> pairs, SourceNode node) {
        for(Pair<? extends SourceNode, ? extends SourceNode> pair: pairs){
            if(pair.getRight() == node) {
                return Optional.ofNullable(pair.getLeft());
            }
        }

        return Optional.empty();
    }
}
