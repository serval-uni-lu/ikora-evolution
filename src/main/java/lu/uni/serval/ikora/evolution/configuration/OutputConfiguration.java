package lu.uni.serval.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lu.uni.serval.ikora.evolution.export.Exporter;

import java.io.File;

public class OutputConfiguration {
    @JsonProperty(value = "smells")
    private File smellsCsvFile;
    @JsonProperty(value = "projects")
    private File projectsCsvFile;
    @JsonProperty(value = "tests")
    private File testCsvFile;
    @JsonProperty(value = "variables changes")
    private File variableChangesCsvFile;
    @JsonProperty(value = "strategy", defaultValue = "CSV")
    private Exporter.Strategy strategy = Exporter.Strategy.CSV;
    @JsonProperty(value = "hash names", defaultValue = "false")
    private boolean hashNames = false;

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

    public File getTestCsvFile() {
        return testCsvFile;
    }

    public void setTestCsvFile(File testCsvFile) {
        this.testCsvFile = testCsvFile;
    }

    public File getVariableChangesCsvFile() {
        return variableChangesCsvFile;
    }

    public void setVariableChangesCsvFile(File variableChangesCsvFile) {
        this.variableChangesCsvFile = variableChangesCsvFile;
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

    public boolean isHashNames() {
        return hashNames;
    }

    public void setHashNames(boolean hashNames) {
        this.hashNames = hashNames;
    }
}
