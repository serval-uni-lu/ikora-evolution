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

import lu.uni.serval.commons.git.exception.InvalidGitRepositoryException;
import lu.uni.serval.ikora.evolution.configuration.EvolutionConfiguration;
import lu.uni.serval.ikora.evolution.results.TestRecord;
import lu.uni.serval.ikora.evolution.export.EvolutionExport;
import lu.uni.serval.ikora.evolution.smells.History;
import lu.uni.serval.ikora.evolution.smells.SmellRecordAccumulator;
import lu.uni.serval.ikora.evolution.results.VersionRecord;
import lu.uni.serval.ikora.evolution.versions.FolderProvider;
import lu.uni.serval.ikora.evolution.versions.VersionProvider;

import lu.uni.serval.ikora.evolution.versions.VersionProviderFactory;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellDetector;
import lu.uni.serval.ikora.smells.SmellResults;

import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.core.analytics.clones.KeywordCloneDetection;
import lu.uni.serval.ikora.core.analytics.clones.Clones;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public class EvolutionRunner {
    private static final Logger logger = LogManager.getLogger(EvolutionRunner.class);

    private final EvolutionExport exporter;
    private final EvolutionConfiguration configuration;

    private final History history;

    public EvolutionRunner(EvolutionExport exporter, EvolutionConfiguration configuration){
        this.exporter = exporter;
        this.configuration = configuration;
        this.history = new History();
    }

    public void execute() throws IOException, GitAPIException, InvalidGitRepositoryException, InterruptedException {
        try (VersionProvider versionProvider = VersionProviderFactory.fromConfiguration(configuration)) {
            this.history.setIgnoreProjectName(versionProvider instanceof FolderProvider);

            for(Projects version: versionProvider){
                logger.log(Level.INFO, "Starting analysis for version {}...", version.getVersionId());

                computeVersionStatistics(version);
                computeTestStatistics(version);

                this.history.addVersion(version);
                computeSmells(version);

                logger.log(Level.INFO, "Analysis for version {} done.", version.getVersionId());
            }
        }
    }

    private void computeVersionStatistics(Projects version) throws IOException {
        if(!this.exporter.contains(EvolutionExport.Statistics.PROJECT)){
            return;
        }

        this.exporter.export(EvolutionExport.Statistics.PROJECT, new VersionRecord(version));
    }

    private void computeSmells(Projects version) throws IOException, InterruptedException {
        if(!this.exporter.contains(EvolutionExport.Statistics.SMELL)){
            new SmellRecordAccumulator();
            return;
        }

        SmellRecordAccumulator smellRecordAccumulator = findSmells(version);
        this.exporter.export(EvolutionExport.Statistics.SMELL, smellRecordAccumulator.getRecords());

    }

    private void computeTestStatistics(Projects version) throws IOException {
        if(!this.exporter.contains(EvolutionExport.Statistics.TEST)){
            return;
        }

        for(TestCase testCase: version.getTestCases()){
            this.exporter.export(EvolutionExport.Statistics.TEST, new TestRecord(testCase));
        }
    }

    private SmellRecordAccumulator findSmells(Projects version) throws InterruptedException {
        final SmellConfiguration smellConfiguration = this.configuration.getSmellConfiguration();
        final SmellRecordAccumulator smellRecordAccumulator = new SmellRecordAccumulator();
        final SmellDetector detector = SmellDetector.all();
        final Clones<KeywordDefinition> clones = KeywordCloneDetection.findClones(version);

        smellConfiguration.setClones(clones);

        for(Project project: version){
            for(TestCase testCase: project.getTestCases()){
                final SmellResults smellResults = detector.computeMetrics(testCase, smellConfiguration);
                history.addSmells(version, smellResults);
                smellRecordAccumulator.addTestCase(version, testCase, smellResults, smellConfiguration, history);
            }
        }

        return smellRecordAccumulator;
    }
}
