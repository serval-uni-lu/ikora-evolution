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

import lu.uni.serval.ikora.evolution.utils.Hash;

import lu.uni.serval.ikora.core.analytics.KeywordStatistics;
import lu.uni.serval.ikora.core.model.TestCase;

public class SmellRecord implements BaseRecord {
    private final String version;
    private final String projectName;
    private final String testCaseName;
    private final int testCaseSize;
    private final int testCaseSequence;
    private final int testCaseLevel;
    private final String smellMetricName;
    private final double smellMetricRawValue;
    private final double smellMetricNormalizedValue;
    private final long fixesCount;

    public SmellRecord(String version, TestCase testCase, String smellMetricName, double smellMetricRawValue, double smellMetricNormalizedValue, long fixesCount) {
        this.version = version;
        this.projectName = testCase.getProject() != null ? testCase.getProject().getName() : "<NONE>";
        this.testCaseName = testCase.toString();
        this.testCaseSize = KeywordStatistics.getSize(testCase).getTotalSize();
        this.testCaseSequence = KeywordStatistics.getSequenceSize(testCase);
        this.testCaseLevel = KeywordStatistics.getLevel(testCase);
        this.smellMetricName = smellMetricName;
        this.smellMetricRawValue = smellMetricRawValue;
        this.smellMetricNormalizedValue = smellMetricNormalizedValue;
        this.fixesCount = fixesCount;
    }

    public String getVersion() {
        return version;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public int getTestCaseSize() {
        return testCaseSize;
    }

    public int getTestCaseSequence() {
        return testCaseSequence;
    }

    public int getTestCaseLevel() {
        return testCaseLevel;
    }

    public String getSmellMetricName() {
        return smellMetricName;
    }

    public double getSmellMetricRawValue() {
        return smellMetricRawValue;
    }

    public double getSmellMetricNormalizedValue() {
        return smellMetricNormalizedValue;
    }

    public long getFixesCount() {
        return fixesCount;
    }

    @Override
    public Object[] getValues(boolean isHashNames){
        return new Object[] {
                this.getVersion(),
                isHashNames ? Hash.sha512(this.getProjectName()) : this.getProjectName(),
                isHashNames ? Hash.sha512(this.getTestCaseName()) : this.getTestCaseName(),
                String.valueOf(this.getTestCaseSize()),
                String.valueOf(this.getTestCaseSequence()),
                String.valueOf(this.getTestCaseLevel()),
                this.getSmellMetricName(),
                String.valueOf(this.getSmellMetricRawValue()),
                String.valueOf(this.getSmellMetricNormalizedValue()),
                String.valueOf(this.getFixesCount())
        };
    }

    @Override
    public String[] getKeys() {
        return new String[] {
                "version",
                "project_name",
                "test_case_name",
                "test_case_size",
                "test_case_sequence",
                "test_case_level",
                "smell_name",
                "smell_raw_value",
                "smell_normalized_value",
                "fixes"
        };
    }
}
