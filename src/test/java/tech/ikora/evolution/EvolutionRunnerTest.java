package tech.ikora.evolution;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import tech.ikora.evolution.configuration.EvolutionConfiguration;
import tech.ikora.evolution.export.EvolutionExport;
import tech.ikora.evolution.export.Exporter;
import tech.ikora.evolution.export.InMemoryExporter;
import tech.ikora.evolution.results.SmellRecord;
import tech.ikora.smells.SmellMetric;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EvolutionRunnerTest {
    @Test
    void testHardCodedValuesAnalysis() throws GitAPIException, IOException {
        final List<SmellRecord> records = executeAnalysis("hard-coded-values").stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.HARD_CODED_VALUES.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
    }

    @Test
    void testConditionalTestLogicAnalysis() throws GitAPIException, IOException {
        final List<SmellRecord> records = executeAnalysis("conditional-assertion").stream()
                .filter(r -> r.getSmellMetricName().equals(SmellMetric.Type.CONDITIONAL_ASSERTION.name()))
                .collect(Collectors.toList());

        assertEquals(2, records.size());
    }


    private List<SmellRecord> executeAnalysis(String resourcesPath) throws GitAPIException, IOException {
        final EvolutionConfiguration conditionalAssertion = Helpers.createConfiguration(resourcesPath);
        final EvolutionRunner evolutionRunner = EvolutionRunnerFactory.fromConfiguration(conditionalAssertion);

        evolutionRunner.execute();

        final Map<EvolutionExport.Statistics, Exporter> exporters = evolutionRunner.getExporter();
        final InMemoryExporter exporter = (InMemoryExporter)exporters.get(EvolutionExport.Statistics.SMELL);

        return exporter.getRecords().stream()
                .filter(r -> SmellRecord.class.isAssignableFrom(r.getClass()))
                .map(r -> (SmellRecord)r)
                .collect(Collectors.toList());
    }
}