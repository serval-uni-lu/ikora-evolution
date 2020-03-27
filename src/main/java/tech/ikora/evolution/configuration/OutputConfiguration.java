package tech.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class OutputConfiguration {
    @JsonProperty(value = "smells csv file")
    private File smellsCsvFile;

    public File getSmellsCsvFile() {
        return smellsCsvFile;
    }

    public void setSmellsCsvFile(File smellsCsvFile) {
        this.smellsCsvFile = smellsCsvFile;
    }
}
