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
import lu.uni.serval.ikora.evolution.export.ExporterFactory;
import lu.uni.serval.ikora.evolution.results.BaseRecord;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import lu.uni.serval.ikora.evolution.configuration.EvolutionConfiguration;
import lu.uni.serval.ikora.evolution.export.EvolutionExport;
import lu.uni.serval.ikora.evolution.export.InMemoryExporter;
import lu.uni.serval.ikora.evolution.results.SmellRecord;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EvolutionRunnerTest {
    @Test
    void testArmyOfClonesFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("army-of-clones", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.ARMY_OF_CLONES.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(2, records.get(1).getFixesCount());
        assertEquals(1, records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testConditionalAssertionFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("conditional-assertion", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.CONDITIONAL_ASSERTION.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testHardCodedEnvironmentConfigurationFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("hardcoded-environment", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.HARDCODED_ENVIRONMENT_CONFIGURATIONS.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testHardCodedValuesFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("hardcoded-values", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.HARD_CODED_VALUES.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testHiddenTestDataFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("hidden-test-data", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.HIDING_TEST_DATA.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testLackOfEncapsulationFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("lack-of-encapsulation", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.LACK_OF_ENCAPSULATION.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testLongTestStepsFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("long-test-steps", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.LONG_TEST_STEPS.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(2, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testMiddleManFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("middle-man", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.MIDDLE_MAN.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testMissingAssertionFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("missing-assertion", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.MISSING_ASSERTION.name()))
                .collect(Collectors.toList());

        assertEquals(3, records.size());

        assertEquals(0, records.get(0).getFixesCount());

        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());

        assertEquals(0, records.get(2).getFixesCount());
        assertEquals(Double.NaN, records.get(2).getBeforeFixVersionCount());
    }

    @Test
    void testNarcissisticFix() throws GitAPIException, InvalidGitRepositoryException, IOException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("narcissistic", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.NARCISSISTIC.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testNoisyLoggingFix() throws GitAPIException, InvalidGitRepositoryException, IOException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("noisy-logging", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.NOISY_LOGGING.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testOnTheFlyFix() throws IOException, InvalidGitRepositoryException, GitAPIException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("on-the-fly", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.ON_THE_FLY.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testOverCheckingFix() throws IOException, InvalidGitRepositoryException, GitAPIException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("over-checking", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.OVER_CHECKING.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(3, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testSensitiveLocatorFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("sensitive-locator", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.SENSITIVE_LOCATOR.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testStickySynchronizationSyndromeFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("stinky-synchronization-syndrome", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.STINKY_SYNCHRONIZATION_SYNDROME.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testSneakyCheckingFix() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("sneaky-checking", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.SNEAKY_CHECKING.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(1., records.get(1).getBeforeFixVersionCount());
    }

    @Test
    void testVersionCount() throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final List<SmellRecord> records = executeAnalysis("history", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.HARD_CODED_VALUES.name()))
                .collect(Collectors.toList());

        assertEquals(3, records.size());
        assertEquals(1, records.get(2).getFixesCount());
        assertEquals(2., records.get(2).getBeforeFixVersionCount());
    }

    private <T extends BaseRecord> List<T> executeAnalysis(String resourcesPath, EvolutionExport.Statistics statistics, Class<T> type) throws GitAPIException, IOException, InvalidGitRepositoryException, InterruptedException {
        final EvolutionConfiguration configuration = Helpers.createConfiguration(resourcesPath, statistics);


        try(EvolutionExport exporter = ExporterFactory.fromConfiguration(configuration)){
            final EvolutionRunner evolutionRunner = new EvolutionRunner(exporter, configuration);
            evolutionRunner.execute();

            return ((InMemoryExporter)exporter.getExporters().get(statistics)).getRecords().stream()
                    .filter(r -> type.isAssignableFrom(r.getClass()))
                    .map(r -> (T)r)
                    .collect(Collectors.toList());
        }

    }
}
