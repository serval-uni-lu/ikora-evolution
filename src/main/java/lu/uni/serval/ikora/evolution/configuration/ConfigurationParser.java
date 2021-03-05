package lu.uni.serval.ikora.evolution.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ConfigurationParser {
    private static final Logger logger = LogManager.getLogger(ConfigurationParser.class);

    public static EvolutionConfiguration parse(String config) throws IOException {
        logger.info(String.format("Loading configuration from '%s'...", config));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());

        File file = new File(config);

        final EvolutionConfiguration configuration = mapper.readValue(file, EvolutionConfiguration.class);
        logger.info("Configuration loaded.");

        return configuration;
    }
}
