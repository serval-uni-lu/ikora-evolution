package lu.uni.serval.ikora.evolution.results;

import lu.uni.serval.ikora.core.analytics.KeywordStatistics;
import lu.uni.serval.ikora.core.model.TestCase;
import lu.uni.serval.ikora.evolution.utils.Hash;

import java.util.List;
import java.util.stream.Collectors;

public class TestRecord implements ChangeRecord {
    final String project;
    final String suite;
    final String name;
    final int level;
    final int statementCount;
    final int sequence;
    final List<Integer> stepSequences;

    public TestRecord(TestCase testCase){
        this.project = testCase.getName();
        this.suite = testCase.getLibraryName();
        this.name = testCase.getName();
        this.level = KeywordStatistics.getLevel(testCase);
        this.statementCount = KeywordStatistics.getStatementCount(testCase);
        this.sequence = KeywordStatistics.getSequenceSize(testCase);
        this.stepSequences = testCase.getSteps().stream()
                .map(KeywordStatistics::getSequenceSize)
                .collect(Collectors.toList());
    }

    @Override
    public String[] getKeys() {
        return new String[]{
                "project",
                "suite",
                "name",
                "level",
                "statement_count",
                "sequence_size",
                "step_sequences_sizes"
        };
    }

    @Override
    public Object[] getValues(boolean isHashNames) {
        return new Object[]{
                isHashNames ? Hash.sha512(this.project) : this.project ,
                isHashNames ? Hash.sha512(this.suite) : this.suite,
                isHashNames ? Hash.sha512(this.name) : this.name,
                this.level,
                this.statementCount,
                this.sequence,
                "[" + this.stepSequences.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]"
        };
    }
}
