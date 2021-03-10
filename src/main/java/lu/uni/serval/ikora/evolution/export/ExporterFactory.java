package lu.uni.serval.ikora.evolution.export;

import lu.uni.serval.ikora.evolution.configuration.EvolutionConfiguration;
import lu.uni.serval.ikora.evolution.configuration.OutputConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExporterFactory {
    public static EvolutionExport fromConfiguration(EvolutionConfiguration configuration){
        OutputConfiguration outputConfiguration = configuration.getOutputConfiguration();
        Map<EvolutionExport.Statistics, File> outputFiles = new HashMap<>();

        File smellsCsvFile = outputConfiguration.getSmellsCsvFile();
        if(smellsCsvFile != null){
            outputFiles.put(EvolutionExport.Statistics.SMELL, smellsCsvFile);
        }

        File projectsCsvFile = outputConfiguration.getProjectsCsvFile();
        if(projectsCsvFile != null){
            outputFiles.put(EvolutionExport.Statistics.PROJECT, projectsCsvFile);
        }

        File variableChangesCsVFile = outputConfiguration.getVariableChangesCsvFile();
        if(projectsCsvFile != null){
            outputFiles.put(EvolutionExport.Statistics.VARIABLE_CHANGES, variableChangesCsVFile);
        }

        return new EvolutionExport(outputConfiguration.getStrategy(), outputFiles);
    }

    public static Exporter create(Exporter.Strategy strategy, String absolutePath) throws IOException {
        switch (strategy){
            case IN_MEMORY: return new InMemoryExporter(absolutePath);
            case CSV: return new CsvExporter(absolutePath);
        }

        return null;
    }
}
