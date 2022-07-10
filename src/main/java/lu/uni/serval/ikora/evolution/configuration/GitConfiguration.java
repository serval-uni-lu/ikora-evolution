package lu.uni.serval.ikora.evolution.configuration;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import lu.uni.serval.commons.git.utils.Frequency;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class GitConfiguration extends Configuration {
    private String url;
    private String group;
    @JsonProperty(value = "locations", required = true)
    private Set<GitLocation> locations = Collections.emptySet();
    @JsonProperty(value = "default branch", defaultValue = "master")
    private String defaultBranch = null;
    @JsonProperty("branch exceptions")
    private Map<String, String> branchExceptions = Collections.emptyMap();
    @JsonProperty(value = "token")
    private String token;
    @JsonProperty(value = "start date")
    private Instant startDate;
    @JsonProperty(value = "end date")
    private Instant endDate;
    @JsonProperty(value = "ignore commits")
    private Set<String> ignoreCommits;
    @JsonProperty(value = "maximum number of commits", defaultValue = "0")
    private int maximumCommitsNumber = 0;
    @JsonProperty(value = "frequency", defaultValue = "UNIQUE")
    private Frequency frequency = Frequency.UNIQUE;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Set<GitLocation> getLocations() {
        return locations;
    }

    public void setLocations(Set<GitLocation> locations) {
        this.locations = locations;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public Map<String, String> getBranchExceptions() {
        return branchExceptions;
    }

    public void setBranchExceptions(Map<String, String> branchExceptions) {
        this.branchExceptions = branchExceptions;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Set<String> getIgnoreCommits() {
        return ignoreCommits;
    }

    public void setIgnoreCommits(Set<String> ignoreCommits) {
        this.ignoreCommits = ignoreCommits;
    }

    public int getMaximumCommitsNumber() {
        return maximumCommitsNumber;
    }

    public void setMaximumCommitsNumber(int maximumCommitsNumber) {
        this.maximumCommitsNumber = maximumCommitsNumber;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }
}
