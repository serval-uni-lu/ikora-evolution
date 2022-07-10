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

import lu.uni.serval.ikora.core.model.Project;
import lu.uni.serval.ikora.core.model.Projects;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class FixResult {
    private boolean isValid;
    private final SmellMetric.Type type;
    private final Projects version;
    private final SourceNode node;
    private final List<SourceNode> history;

    public FixResult(SmellMetric.Type type, Projects version, SourceNode node, List<SourceNode>history){
        this.isValid = true;
        this.type = type;
        this.version = version;
        this.node = node;
        this.history = history;
    }

    public static FixResult noFix(){
        FixResult result = new FixResult(null, null, null, null);
        result.isValid = false;

        return result;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public SmellMetric.Type getType() {
        return type;
    }

    public int getNumberVersions() {
        return history.size();
    }

    public Duration getDuration() {
        final Optional<Instant> end = getDate();

        if(end.isEmpty()){
            return Duration.ZERO;
        }

        Instant start = history.stream().map(n -> n.getProject().getDate()).min(Instant::compareTo).orElse(end.get());
        return Duration.between(start, end.get());
    }

    public Optional<Instant> getDate(){
        if(this.node != null){
            return Optional.of(this.node.getProject().getDate());
        }

        return this.version.asSet().stream().max(Project::compareTo).map(Project::getDate);
    }
}
