package tech.ikora.evolution;

import org.apache.commons.lang3.tuple.Pair;
import tech.ikora.analytics.Difference;
import tech.ikora.analytics.KeywordStatistics;
import tech.ikora.evolution.differences.NodeMatcher;
import tech.ikora.evolution.versions.VersionProvider;
import tech.ikora.model.*;

import java.io.IOException;

public class EvolutionRunner {
    private final VersionProvider versionProvider;
    private final EvolutionResults results;

    public EvolutionRunner(VersionProvider versionProvider){
        this.versionProvider = versionProvider;
        this.results = new EvolutionResults();
    }

    public void execute() throws IOException {
        Projects version1 = null;

        for(Projects version2: versionProvider){
            findDifferences(version1, version2);
            version1 = version2;
        }

        versionProvider.clean();
    }

    private void findDifferences(Projects version1, Projects version2){
            if(version1 == null || version2 == null){
                return;
            }

            for(Pair<UserKeyword,UserKeyword> keywordPair: NodeMatcher.getPairs(UserKeyword.class, version1, version2)){
                UserKeyword keyword1 = getElement(keywordPair, version1);
                UserKeyword keyword2 = getElement(keywordPair, version2);

                results.addDifference(version1, Difference.of(keyword1, keyword2));
            }

            for(Pair<TestCase,TestCase> testCasePair: NodeMatcher.getPairs(TestCase.class, version1, version2)) {
                TestCase testCase1 = getElement(testCasePair, version1);
                TestCase testCase2 = getElement(testCasePair, version2);

                Sequence sequence1 = testCase1 != null ? KeywordStatistics.getSequence(testCase1) : null;
                Sequence sequence2 = testCase2 != null ? KeywordStatistics.getSequence(testCase2) : null;

                results.addDifference(version1, Difference.of(testCase1, testCase2), Difference.of(sequence1, sequence2));

                results.addSequence(version1, sequence1);
                results.addSequence(version2, sequence2);
            }

            for(Pair<Variable,Variable> variablePair: NodeMatcher.getPairs(Variable.class, version1, version2)) {
                Variable variable1 = getElement(variablePair, version1);
                Variable variable2 = getElement(variablePair, version2);

                results.addDifference(version1, Difference.of(variable1, variable2));
            }

            results.addVersion(version1);
            results.addVersion(version2);
    }

    private <T extends Node> T getElement(Pair<T,T> pair, Projects version){
        if(pair.getRight() != null && version.contains(pair.getRight().getProject())) {
            return pair.getRight();
        }
        else if(pair.getLeft() != null && version.contains(pair.getLeft().getProject())){
            return pair.getLeft();
        }

        return null;
    }
}
