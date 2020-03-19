package tech.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import tech.ikora.evolution.versions.Frequency;

import java.util.Date;
import java.util.Set;

public class GitConfiguration {
    @JsonProperty(value = "urls", required = true)
    private Set<String> urls;
    @JsonProperty(value = "url", defaultValue = "master")
    private String branch;
    @JsonProperty(value = "url", required = true)
    private String token;
    @JsonProperty(value = "start date")
    private Date startDate;
    @JsonProperty(value = "end date")
    private Date endDate;
    @JsonProperty(value = "ignore commits")
    private Set<String> ignoreCommits;
    @JsonProperty(value = "maximum number of commits")
    private int maximumCommitsNumber;
    @JsonProperty(value = "frequency", defaultValue = "ALL")
    private Frequency frequency;

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> url) {
        this.urls = url;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
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
