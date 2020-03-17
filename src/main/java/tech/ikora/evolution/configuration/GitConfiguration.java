package tech.ikora.evolution.configuration;

import java.util.Date;
import java.util.List;

public class GitConfiguration {
    private String url;
    private String branch;
    private String token;
    private Date startDate;
    private Date endDate;
    private List<String> ignoreReleases;
    private int maximumReleasesNumber;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public List<String> getIgnoreReleases() {
        return ignoreReleases;
    }

    public void setIgnoreReleases(List<String> ignoreReleases) {
        this.ignoreReleases = ignoreReleases;
    }

    public int getMaximumReleasesNumber() {
        return maximumReleasesNumber;
    }

    public void setMaximumReleasesNumber(int maximumReleasesNumber) {
        this.maximumReleasesNumber = maximumReleasesNumber;
    }
}
