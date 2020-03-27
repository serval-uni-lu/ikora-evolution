package tech.ikora.evolution.results;

import tech.ikora.model.TestCase;
import tech.ikora.smells.SmellMetric;

public class SmellRecord implements CsvRecord {
    private final String version;
    private final String testCase;
    private final String smellMetricName;
    private final double smellMetricValue;
    private final int changesCount;

    public SmellRecord(String version, TestCase testCase, SmellMetric smell, int changesCount) {
        this.version = version;
        this.testCase = testCase.toString();
        this.smellMetricName = smell.getType().name();
        this.smellMetricValue = smell.getValue();
        this.changesCount = changesCount;
    }

    public String getVersion() {
        return version;
    }

    public String getTestCase() {
        return testCase;
    }

    public String getSmellMetricName() {
        return smellMetricName;
    }

    public double getSmellMetricValue() {
        return smellMetricValue;
    }

    public int getChangesCount() {
        return changesCount;
    }

    public Object[] getValues(){
        return new Object[] {
                this.getVersion(),
                this.getTestCase(),
                this.getSmellMetricName(),
                String.valueOf(this.getSmellMetricValue()),
                String.valueOf(this.changesCount)
        };
    }

    public String[] getHeaders() {
        return new String[] {
                "version",
                "test_case",
                "smell_name",
                "smell_metric",
                "changes"
        };
    }
}
