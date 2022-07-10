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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FixResult {
    private boolean isValid;
    private final SmellMetric.Type type;
    private final Projects version;
    private final List<SourceNode> history;

    public FixResult(SmellMetric.Type type, Projects version, List<SourceNode>history){
        this.isValid = true;
        this.type = type;
        this.version = version;
        this.history = history;
    }

    public static FixResult noFix(){
        FixResult result = new FixResult(null, null, null);
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
        final Instant end = version.getDate();
        final Instant start = getIntroductionDate();

        if(start == null || end == null){
            return Duration.ZERO;
        }

        return Duration.between(start, end);
    }

    private Instant getIntroductionDate(){
        List<Instant> dates = history.stream()
                .map(SourceNode::getProject)
                .filter(Objects::nonNull)
                .map(Project::getDate)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if(dates.isEmpty()){
            return version.getDate();
        }

        if(dates.size() == 1){
            return dates.iterator().next();
        }

        return Collections.min(dates);
    }
}
