package lu.uni.serval.ikora.evolution.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ConfigurationParser {
    private static final Logger logger = LogManager.getLogger(ConfigurationParser.class);

    public static EvolutionConfiguration parse(String config) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        File file = new File(config);

        final EvolutionConfiguration configuration = mapper.readValue(file, EvolutionConfiguration.class);
        logger.info("Configuration loaded from " + config);

        return configuration;
    }
}
