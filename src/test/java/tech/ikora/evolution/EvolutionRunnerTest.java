package tech.ikora.evolution;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import tech.ikora.evolution.configuration.EvolutionConfiguration;

import java.io.IOException;

class EvolutionRunnerTest {
    @Test
    void testHardCodedValuesAnalysis() throws GitAPIException, IOException {
        final EvolutionConfiguration hardcodedvalues = Helpers.createConfiguration("hardcodedvalues");
        final EvolutionRunner evolutionRunner = EvolutionRunnerFactory.fromConfiguration(hardcodedvalues);

        evolutionRunner.execute();
    }

}