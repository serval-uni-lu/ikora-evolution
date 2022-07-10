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

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationParserTest {
    @Test
    void testLoadFromFile() throws IOException, URISyntaxException {
        final File emptyConfiguration = lu.uni.serval.ikora.core.utils.FileUtils.getResourceFile("configuration/empty-configuration.json");
        final EvolutionConfiguration configuration = ConfigurationParser.parse(emptyConfiguration.getAbsolutePath(), EvolutionConfiguration.class);

        assertNull(configuration.getGitConfiguration());
        assertNull(configuration.getOutputConfiguration());
        assertNotNull(configuration.getSmellConfiguration());
    }
}
