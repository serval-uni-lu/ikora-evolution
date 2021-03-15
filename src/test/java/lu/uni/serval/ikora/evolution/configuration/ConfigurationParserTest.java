package lu.uni.serval.ikora.evolution.configuration;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationParserTest {
    @Test
    void testDefaultValues() throws IOException {
        final String json = "{}";
        final EvolutionConfiguration configuration = ConfigurationParser.parse(json);

        assertNull(configuration.getFolderConfiguration());
        assertNull(configuration.getGitConfiguration());
        assertNull(configuration.getOutputConfiguration());
        assertNotNull(configuration.getSmellConfiguration());
    }

    @Test
    void testLoadFromFile() throws IOException, URISyntaxException {
        final File emptyConfiguration = lu.uni.serval.ikora.core.utils.FileUtils.getResourceFile("configuration/empty-configuration.json");
        final EvolutionConfiguration configuration = ConfigurationParser.parse(emptyConfiguration.getAbsolutePath());

        assertNull(configuration.getGitConfiguration());
        assertNull(configuration.getOutputConfiguration());
        assertNotNull(configuration.getSmellConfiguration());
    }
}