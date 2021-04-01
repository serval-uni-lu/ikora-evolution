package lu.uni.serval.ikora.evolution.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ConfigurationParser {
    private static final Logger logger = LogManager.getLogger(ConfigurationParser.class);

    private ConfigurationParser() {}

    public static EvolutionConfiguration parse(String config) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());

        EvolutionConfiguration configuration;

        final File file = new File(config);

        if(file.exists()){
            logger.log(Level.INFO, "Loading configuration from '{}'...", config);
            configuration = mapper.readValue(file, EvolutionConfiguration.class);
        }
        else if(config.trim().startsWith("{")){
            logger.info("Loading configuration from content string...");
            configuration = mapper.readValue(config, EvolutionConfiguration.class);
        }
        else{
            throw new IOException(String.format("Failed to read configuration file provide a valid path or a valid json file:%n%s", config));
        }

        logger.info("Configuration loaded.");

        return configuration;
    }
}
