package tech.ikora.evolution.results;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.analytics.KeywordStatistics;
import tech.ikora.model.TestCase;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SmellRecord implements Record {
    private static final Logger logger = LogManager.getLogger(SmellRecord.class);

    private final String version;
    private final String testCaseName;
    private final int testCaseSize;
    private final int testCaseSequence;
    private final int testCaseLevel;
    private final String smellMetricName;
    private final double smellMetricValue;
    private final long fixesCount;
    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to generate MD5 hash generator");
        }
    }

    public SmellRecord(String version, TestCase testCase, String smellMetricName, double smellMetricValue, long fixesCount) {
        this.version = version;
        this.testCaseName = testCase.toString();
        this.testCaseSize = KeywordStatistics.getSize(testCase).getTestCaseSize();
        this.testCaseSequence = KeywordStatistics.getSequenceSize(testCase);
        this.testCaseLevel = KeywordStatistics.getLevel(testCase);
        this.smellMetricName = smellMetricName;
        this.smellMetricValue = smellMetricValue;
        this.fixesCount = fixesCount;
    }

    public String getVersion() {
        return version;
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

    public double getSmellMetricValue() {
        return smellMetricValue;
    }

    public long getFixesCount() {
        return fixesCount;
    }

    @Override
    public Object[] getValues(){
        return new Object[] {
                this.getVersion(),
                hash(this.getTestCaseName()),
                String.valueOf(this.getTestCaseSize()),
                String.valueOf(this.getTestCaseSequence()),
                String.valueOf(this.getTestCaseLevel()),
                this.getSmellMetricName(),
                String.valueOf(this.getSmellMetricValue()),
                String.valueOf(this.fixesCount)
        };
    }

    @Override
    public String[] getKeys() {
        return new String[] {
                "version",
                "test_case_name",
                "test_case_size",
                "test_case_sequence",
                "test_case_level",
                "smell_name",
                "smell_metric",
                "fixes"
        };
    }

    public static String hash(String text){
        md.update(text.getBytes());
        return DatatypeConverter.printHexBinary(md.digest());
    }
}
