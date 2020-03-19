package tech.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EvolutionConfiguration {
    @JsonProperty(value = "git")
    private GitConfiguration gitConfiguration;
    @JsonProperty(value = "folder")
    private FolderConfiguration folderConfiguration;
    @JsonProperty(value = "output", required = true)
    private OutputConfiguration outputConfiguration;

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
}
