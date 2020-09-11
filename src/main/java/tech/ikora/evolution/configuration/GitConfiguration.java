package tech.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import tech.ikora.gitloader.git.Frequency;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class GitConfiguration {
    private String url;
    private String group;
    @JsonProperty(value = "locations", required = true)
    private Set<GitLocation> locations = Collections.emptySet();
    @JsonProperty(value = "default branch", defaultValue = "master")
    private String defaultBranch = "master";
    @JsonProperty("branch exceptions")
    private Map<String, String> branchExceptions;
    @JsonProperty(value = "token", required = true)
    private String token;
    @JsonProperty(value = "start date")
    private Date startDate;
    @JsonProperty(value = "end date")
    private Date endDate;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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
