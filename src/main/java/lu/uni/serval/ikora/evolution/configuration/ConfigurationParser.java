package lu.uni.serval.ikora.evolution.configuration;

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
