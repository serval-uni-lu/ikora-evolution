package lu.uni.serval.ikora.evolution.configuration;

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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ConfigurationParser {
    private static final Logger logger = LogManager.getLogger(ConfigurationParser.class);

    private ConfigurationParser() {}

    public static <T extends Configuration> T parse(String config, Class<T> type) throws IOException {
        File file = new File(config);

        if(!file.exists()){
            throw new IOException(String.format("Configuration file '%s' does not exist!", file.getAbsolutePath()));
        }

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new Jdk8Module(), new FolderModule(file.getParentFile()));
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        final T configuration = mapper.readValue(file, type);

        logger.printf(Level.INFO,
                "Configuration loaded from '%s'",
                config
        );

        return configuration;
    }

    public static class FolderModule extends SimpleModule {
        private final File folder;

        FolderModule(File folder){
            this.folder = folder;
        }

        @Override
        public void setupModule(SetupContext context) {
            super.setupModule(context);
            context.addBeanDeserializerModifier(new BeanDeserializerModifier()
            {
                @Override public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
                {
                    if (Configuration.class.isAssignableFrom(beanDesc.getBeanClass())){
                        return new Modifier((JsonDeserializer<Configuration>)deserializer, folder);
                    }

                    return deserializer;
                }
            });
        }
    }

    public static class Modifier extends StdDeserializer<Configuration> implements ResolvableDeserializer {
        private final transient JsonDeserializer<Configuration> defaultDeserializer;
        private final File folder;

        public Modifier(JsonDeserializer<Configuration> defaultDeserializer, File folder) {
            super(Configuration.class);

            this.defaultDeserializer = defaultDeserializer;
            this.folder = folder;
        }

        @Override
        public void resolve(DeserializationContext context) throws JsonMappingException {
            ((ResolvableDeserializer) defaultDeserializer).resolve(context);
        }

        @Override
        public Configuration deserialize(JsonParser p, DeserializationContext context) throws IOException {
            Configuration configuration = defaultDeserializer.deserialize(p, context);
            configuration.setFolder(this.folder);

            return configuration;
        }
    }
}
