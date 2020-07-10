package tech.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import tech.ikora.evolution.export.Exporter;

import java.io.File;

public class OutputConfiguration {
    @JsonProperty(value = "smells csv file")
    private File smellsCsvFile;
    @JsonProperty(value = "projects csv file")
    private File projectsCsvFile;
    @JsonProperty(value = "strategy")
    private Exporter.Strategy strategy = Exporter.Strategy.CSV;

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

    public Exporter.Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        if(strategy.equalsIgnoreCase(Exporter.Strategy.IN_MEMORY.name())){
            this.strategy = Exporter.Strategy.IN_MEMORY;
        }
        else{
            this.strategy = Exporter.Strategy.CSV;
        }
    }

    public void setStrategy(Exporter.Strategy strategy){
        this.strategy = strategy;
    }
}
