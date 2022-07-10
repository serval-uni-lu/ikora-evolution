package lu.uni.serval.ikora.evolution.configuration;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import lu.uni.serval.ikora.evolution.export.Exporter;

import java.io.File;

public class OutputConfiguration extends Configuration {
    @JsonProperty(value = "smells")
    private File smellsCsvFile;
    @JsonProperty(value = "projects")
    private File projectsCsvFile;
    @JsonProperty(value = "tests")
    private File testCsvFile;

    @JsonProperty(value = "strategy", defaultValue = "CSV")
    private Exporter.Strategy strategy = Exporter.Strategy.CSV;
    @JsonProperty(value = "hash names", defaultValue = "false")
    private boolean hashNames = false;

    public File getSmellsCsvFile() {
        if(smellsCsvFile != null && !smellsCsvFile.isAbsolute()){
            return new File(getFolder(), smellsCsvFile.getPath());
        }

        return smellsCsvFile;
    }

    public void setSmellsCsvFile(File smellsCsvFile) {
        this.smellsCsvFile = smellsCsvFile;
    }

    public File getProjectsCsvFile() {
        if(projectsCsvFile != null && !projectsCsvFile.isAbsolute()){
            return new File(getFolder(), projectsCsvFile.getPath());
        }

        return projectsCsvFile;
    }

    public void setProjectsCsvFile(File projectsCsvFile) {
        this.projectsCsvFile = projectsCsvFile;
    }

    public File getTestCsvFile() {
        if(testCsvFile != null && !testCsvFile.isAbsolute()){
            return new File(getFolder(), testCsvFile.getPath());
        }

        return testCsvFile;
    }

    public void setTestCsvFile(File testCsvFile) {
        this.testCsvFile = testCsvFile;
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
