package lu.uni.serval.ikora.evolution;

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

import lu.uni.serval.commons.git.exception.InvalidGitRepositoryException;
import lu.uni.serval.ikora.evolution.export.ExporterFactory;
import lu.uni.serval.ikora.evolution.results.BaseRecord;
import lu.uni.serval.ikora.evolution.results.VariableChangeRecord;
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
    void testHardCodedValuesAnalysis() throws GitAPIException, IOException, InvalidGitRepositoryException {
        final List<SmellRecord> records = executeAnalysis("hard-coded-values", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.HARD_CODED_VALUES.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(0.14285, records.get(0).getSmellMetricNormalizedValue(), 0.0001);
        assertEquals(1., records.get(0).getSmellMetricRawValue(), 0.0001);
        assertEquals(0.0, records.get(1).getSmellMetricNormalizedValue(), 0.0001);
        assertEquals(0.0, records.get(1).getSmellMetricRawValue(), 0.0001);
    }

    @Test
    void testCalculateResultsOnTheFly() throws IOException, InvalidGitRepositoryException, GitAPIException {
        final List<SmellRecord> records = executeAnalysis("calculate-expected-results-on-the-fly", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.ON_THE_FLY.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
    }

    @Test
    void testConditionalAssertionAnalysis() throws GitAPIException, IOException, InvalidGitRepositoryException {
        final List<SmellRecord> records = executeAnalysis("conditional-assertion", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.CONDITIONAL_ASSERTION.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(1).getFixesCount());
    }

    @Test
    void testComplexLocatorAnalysis() throws GitAPIException, IOException, InvalidGitRepositoryException {
        final List<SmellRecord> records = executeAnalysis("sensitive-locator", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.SENSITIVE_LOCATOR.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, records.get(0).getSmellMetricRawValue());
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(0, records.get(1).getSmellMetricRawValue());
    }

    @Test
    void testLackOfEncapsulationAnalysis() throws GitAPIException, IOException, InvalidGitRepositoryException {
        final List<SmellRecord> records = executeAnalysis("lack-of-encapsulation", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.LACK_OF_ENCAPSULATION.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(0.3333, records.get(0).getSmellMetricNormalizedValue(), 0.0001);
        assertEquals(1., records.get(0).getSmellMetricRawValue(), 0.0001);
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(0., records.get(1).getSmellMetricNormalizedValue(), 0.0001);
        assertEquals(0., records.get(1).getSmellMetricRawValue(), 0.0001);
    }

    @Test
    void testCloneAnalysis() throws GitAPIException, IOException, InvalidGitRepositoryException {
        final List<SmellRecord> records = executeAnalysis("clones", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.ARMY_OF_CLONES.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(0.4, records.get(0).getSmellMetricNormalizedValue(), 0.0001);
        assertEquals(2, records.get(1).getFixesCount());
        assertEquals(0., records.get(1).getSmellMetricNormalizedValue(), 0.0001);
    }

    @Test
    void testMiddleManAnalysis() throws GitAPIException, IOException, InvalidGitRepositoryException {
        final List<SmellRecord> records = executeAnalysis("middle-man", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.MIDDLE_MAN.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(0.5, records.get(0).getSmellMetricNormalizedValue(), 0.0001);
        assertEquals(1, records.get(1).getFixesCount());
        assertEquals(0., records.get(1).getSmellMetricNormalizedValue(), 0.0001);
    }

    @Test
    void testMissingAssertion() throws GitAPIException, IOException, InvalidGitRepositoryException {
        final List<SmellRecord> records = executeAnalysis("missing-assertion", EvolutionExport.Statistics.SMELL, SmellRecord.class).stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.MISSING_ASSERTION.name()))
                .collect(Collectors.toList());

        final long numberFixes = records.stream().map(SmellRecord::getFixesCount).reduce(0L, Long::sum);

        assertEquals(4, records.size());
        assertEquals(0, records.get(0).getFixesCount());
        assertEquals(1, numberFixes);
    }

    @Test
    void testVariablesEvolution() throws GitAPIException, IOException, InvalidGitRepositoryException {
        final List<VariableChangeRecord> records = executeAnalysis("sensitive-locator", EvolutionExport.Statistics.VARIABLE_CHANGES, VariableChangeRecord.class);
        assertEquals(1, records.size());
        assertEquals("locator", records.get(0).getBeforeType());
        assertEquals("locator", records.get(0).getAfterType());
        assertEquals("${PASSWORD_FIELD}", records.get(0).getBeforeName());
        assertEquals("${PASSWORD_FIELD}", records.get(0).getAfterName());
        assertEquals("Input Text", records.get(0).getBeforeCall());
        assertEquals("Input Text", records.get(0).getAfterCall());
        assertEquals("[css:.covid-form > div > div.react-grid-Container > div > div > div.react-grid-Header > div > div > div:nth-child(3) > div]", records.get(0).getBeforeValues());
        assertEquals("[password_field]", records.get(0).getAfterValues());
    }

    private <T extends BaseRecord> List<T> executeAnalysis(String resourcesPath, EvolutionExport.Statistics statistics, Class<T> type) throws GitAPIException, IOException, InvalidGitRepositoryException {
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
