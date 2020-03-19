package tech.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class OutputConfiguration {
    @JsonProperty(value = "smells evolution")
    private File smellsEvolution;

    public File getSmellsEvolution() {
        return smellsEvolution;
    }

    public void setSmellsEvolution(File smellsEvolution) {
        this.smellsEvolution = smellsEvolution;
    }
}
