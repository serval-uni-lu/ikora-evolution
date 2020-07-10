package tech.ikora.evolution;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import tech.ikora.evolution.configuration.EvolutionConfiguration;
import tech.ikora.evolution.export.EvolutionExport;
import tech.ikora.evolution.export.Exporter;
import tech.ikora.evolution.export.InMemoryExporter;
import tech.ikora.evolution.results.Record;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EvolutionRunnerTest {
    @Test
    void testHardCodedValuesAnalysis() throws GitAPIException, IOException {
        final List<Record> records = executeAnalysis("hard-coded-values");
        assertFalse(records.isEmpty());
    }

    @Test
    void testConditionalTestLogicAnalysis() throws GitAPIException, IOException {
        final List<Record> records = executeAnalysis("conditional-assertion");
        assertFalse(records.isEmpty());
    }


    private List<Record> executeAnalysis(String resourcesPath) throws GitAPIException, IOException {
        final EvolutionConfiguration conditionalAssertion = Helpers.createConfiguration(resourcesPath);
        final EvolutionRunner evolutionRunner = EvolutionRunnerFactory.fromConfiguration(conditionalAssertion);

        evolutionRunner.execute();

        final Map<EvolutionExport.Statistics, Exporter> exporters = evolutionRunner.getExporter();
        final InMemoryExporter exporter = (InMemoryExporter)exporters.get(EvolutionExport.Statistics.SMELL);

        return exporter.getRecords();
    }
}