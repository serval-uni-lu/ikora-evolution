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
import lu.uni.serval.ikora.smells.SmellConfiguration;

public class EvolutionConfiguration extends Configuration {
    @JsonProperty(value = "git")
    private GitConfiguration gitConfiguration;
    @JsonProperty(value = "folder")
    private FolderConfiguration folderConfiguration;
    @JsonProperty(value = "output", required = true)
    private OutputConfiguration outputConfiguration;
    @JsonProperty(value= "smells")
    private SmellConfiguration smellConfiguration = new SmellConfiguration();

    @JsonProperty(value = "git")
    public GitConfiguration getGitConfiguration() {
        return gitConfiguration;
    }

    @JsonProperty(value = "git")
    public void setGitConfiguration(GitConfiguration gitConfiguration) {
        this.gitConfiguration = gitConfiguration;
    }

    @JsonProperty(value = "folder")
    public FolderConfiguration getFolderConfiguration() {
        return folderConfiguration;
    }

    @JsonProperty(value = "folder")
    public void setFolderConfiguration(FolderConfiguration folderConfiguration) {
        this.folderConfiguration = folderConfiguration;
    }

    @JsonProperty(value = "output")
    public OutputConfiguration getOutputConfiguration() {
        return outputConfiguration;
    }

    @JsonProperty(value = "output")
    public void setOutputConfiguration(OutputConfiguration outputConfiguration) {
        this.outputConfiguration = outputConfiguration;
    }

    @JsonProperty(value = "smells")
    public SmellConfiguration getSmellConfiguration() {
        return smellConfiguration;
    }

    @JsonProperty(value = "smells")
    public void setSmellConfiguration(SmellConfiguration smellConfiguration) {
        this.smellConfiguration = smellConfiguration;
    }
}
