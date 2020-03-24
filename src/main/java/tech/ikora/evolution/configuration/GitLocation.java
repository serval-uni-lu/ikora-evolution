package tech.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class GitLocation {
    @JsonProperty(value = "url", required = true)
    String url;
    @JsonProperty(value = "project folders")
    Set<String> projectFolders;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<String> getProjectFolders() {
        return projectFolders;
    }

    public void setSubfolder(Set<String> subfolder) {
        this.projectFolders = subfolder;
    }
}
