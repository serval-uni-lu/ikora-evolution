package lu.uni.serval.ikora.evolution;

/*-
 * #%L
 * Ikora Evolution
 * %%
 * Copyright (C) 2020 - 2022 University of Luxembourg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
