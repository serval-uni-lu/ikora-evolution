package tech.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class OutputConfiguration {
    @JsonProperty(value = "smells csv file")
    private File smellsCsvFile;
    @JsonProperty(value = "projects csv file")
    private File projectsCsvFile;

    public File getSmellsCsvFile() {
        return smellsCsvFile;
    }

    public void setSmellsCsvFile(File smellsCsvFile) {
        this.smellsCsvFile = smellsCsvFile;
    }

    public File getProjectsCsvFile() {
        return projectsCsvFile;
    }

    public void setProjectsCsvFile(File projectsCsvFile) {
        this.projectsCsvFile = projectsCsvFile;
    }
}
