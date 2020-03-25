package tech.ikora.evolution.export;

import tech.ikora.evolution.configuration.OutputConfiguration;
import tech.ikora.evolution.results.EvolutionResults;
import tech.ikora.evolution.results.SmellResults;

import java.io.File;
import java.io.IOException;

public class EvolutionExport {
    private final File smellEvolutionFile;

    public EvolutionExport(OutputConfiguration configuration){
        this.smellEvolutionFile = configuration.getSmellsEvolution();
    }

    public void export(EvolutionResults results) throws IOException {
        exportSmellEvolution(results.getSmellResults());
    }

    private void exportSmellEvolution(SmellResults results) throws IOException {
        if(smellEvolutionFile == null){
            return;
        }

        ExportSmells.export(smellEvolutionFile.getAbsolutePath(), results);
    }
}
