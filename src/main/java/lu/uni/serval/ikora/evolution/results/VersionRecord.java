package lu.uni.serval.ikora.evolution.results;

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

import lu.uni.serval.ikora.core.model.Projects;

import java.time.Instant;

public class VersionRecord implements BaseRecord {
    private final Instant date;
    private final int projects;
    private final int testCases;
    private final int userKeywords;
    private final int variables;
    private final int lines;

    public VersionRecord(Projects version){
        this.date = version.getDate() == null ? Instant.now() : version.getDate();
        this.projects = version.size();
        this.testCases = version.getTestCases().size();
        this.userKeywords = version.getUserKeywords().size();
        this.variables = version.getVariableAssignments().size();
        this.lines = version.getLoc();
    }

    public Instant getDate() {
        return date;
    }

    public int getProjects() {
        return projects;
    }

    public int getTestCases() {
        return testCases;
    }

    public int getUserKeywords() {
        return userKeywords;
    }

    public int getVariables() {
        return variables;
    }

    public int getLines() {
        return lines;
    }

    @Override
    public String[] getKeys() {
        return new String[]{
                "date",
                "number_projects",
                "number_test_cases",
                "number_keywords",
                "number_variables",
                "number_lines"
        };
    }

    @Override
    public Object[] getValues(boolean isHashNames) {
        return new Object[]{
                this.getDate().toString(),
                this.getProjects(),
                this.getTestCases(),
                this.getUserKeywords(),
                this.getVariables(),
                this.getLines()
        };
    }
}
