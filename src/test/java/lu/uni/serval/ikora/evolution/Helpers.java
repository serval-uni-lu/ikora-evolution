package lu.uni.serval.ikora.evolution;

import lu.uni.serval.ikora.evolution.configuration.EvolutionConfiguration;
import lu.uni.serval.ikora.evolution.configuration.FolderConfiguration;
import lu.uni.serval.ikora.evolution.configuration.OutputConfiguration;
import lu.uni.serval.ikora.evolution.export.EvolutionExport;
import lu.uni.serval.ikora.evolution.export.Exporter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class Helpers {
    static EvolutionConfiguration createConfiguration(String resourcesPath, EvolutionExport.Statistics statistics) throws IOException {
        File projectFolder = null;
        try {
            projectFolder = lu.uni.serval.ikora.core.utils.FileUtils.getResourceFile(resourcesPath);
        } catch (Exception e) {
            fail(String.format("Failed to load '%s': %s", resourcesPath, e.getMessage()));
        }

        final FolderConfiguration folderConfiguration = new FolderConfiguration();
        folderConfiguration.setRootFolder(projectFolder);
        folderConfiguration.setNameFormat(FolderConfiguration.NameFormat.VERSION);

        final File baseFolder = new File(new File(System.getProperty("java.io.tmpdir")), resourcesPath);
        if(baseFolder.exists()){
            FileUtils.deleteDirectory(baseFolder);
        }

        final File outputFolder = new File(baseFolder, statistics.name());

        if(!outputFolder.mkdirs()){
            fail(String.format("Failed to create output folder: %s", outputFolder.getAbsolutePath()));
        }

        final OutputConfiguration outputConfiguration = new OutputConfiguration();
        outputConfiguration.setProjectsCsvFile(new File(outputFolder, "projects.csv"));
        outputConfiguration.setSmellsCsvFile(new File(outputFolder, "smells.csv"));
        outputConfiguration.setVariableChangesCsvFile(new File(outputFolder, "variable_changes.csv"));
        outputConfiguration.setStrategy(Exporter.Strategy.IN_MEMORY);

        final EvolutionConfiguration evolutionConfiguration = new EvolutionConfiguration();
        evolutionConfiguration.setFolderConfiguration(folderConfiguration);
        evolutionConfiguration.setOutputConfiguration(outputConfiguration);

        return evolutionConfiguration;
    }
}
