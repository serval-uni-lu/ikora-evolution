package lu.uni.serval.ikora.evolution.export;

/*-
 * #%L
 * Ikora Evolution
 * %%
 * Copyright (C) 2020 - 2021 University of Luxembourg
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
import lu.uni.serval.ikora.evolution.configuration.OutputConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class ExporterFactory {
    private ExporterFactory() {}

    public static EvolutionExport fromConfiguration(EvolutionConfiguration configuration){
        OutputConfiguration outputConfiguration = configuration.getOutputConfiguration();
        Map<EvolutionExport.Statistics, File> outputFiles = new EnumMap<>(EvolutionExport.Statistics.class);

        File smellsCsvFile = outputConfiguration.getSmellsCsvFile();
        if(smellsCsvFile != null){
            outputFiles.put(EvolutionExport.Statistics.SMELL, smellsCsvFile);
        }

        File projectsCsvFile = outputConfiguration.getProjectsCsvFile();
        if(projectsCsvFile != null){
            outputFiles.put(EvolutionExport.Statistics.PROJECT, projectsCsvFile);
        }

        File testsCsvFile = outputConfiguration.getTestCsvFile();
        if(testsCsvFile != null){
            outputFiles.put(EvolutionExport.Statistics.TEST, testsCsvFile);
        }

        File variableChangesCsVFile = outputConfiguration.getVariableChangesCsvFile();
        if(projectsCsvFile != null){
            outputFiles.put(EvolutionExport.Statistics.VARIABLE_CHANGES, variableChangesCsVFile);
        }

        return new EvolutionExport(outputConfiguration.getStrategy(), outputFiles, outputConfiguration.isHashNames());
    }

    public static Exporter create(Exporter.Strategy strategy, String absolutePath, boolean isHashNames) throws IOException {
        switch (strategy){
            case IN_MEMORY: return new InMemoryExporter(absolutePath, isHashNames);
            case CSV: return new CsvExporter(absolutePath, isHashNames);
        }

        return null;
    }
}
