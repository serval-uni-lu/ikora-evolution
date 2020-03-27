package tech.ikora.evolution;

import org.apache.commons.lang3.tuple.Pair;
import tech.ikora.analytics.Difference;
import tech.ikora.analytics.KeywordStatistics;
import tech.ikora.analytics.visitor.FindTestCaseVisitor;
import tech.ikora.analytics.visitor.PathMemory;
import tech.ikora.evolution.differences.NodeMatcher;
import tech.ikora.evolution.export.EvolutionExport;
import tech.ikora.evolution.results.DifferenceResults;
import tech.ikora.evolution.results.SequenceResults;
import tech.ikora.evolution.results.SmellResults;
import tech.ikora.evolution.results.VersionRecord;
import tech.ikora.evolution.versions.VersionProvider;
import tech.ikora.model.*;
import tech.ikora.smells.SmellDetector;
import tech.ikora.smells.SmellMetric;

import java.io.IOException;
import java.util.*;

public class EvolutionRunner {
    private final VersionProvider versionProvider;
    private final EvolutionExport exporter;

    public EvolutionRunner(VersionProvider versionProvider, EvolutionExport exporter){
        this.versionProvider = versionProvider;
        this.exporter = exporter;
    }

    public void execute() throws IOException {
        Projects previousVersion = null;

        for(Projects version: versionProvider){
            computeVersionStatistics(version);

            if(previousVersion != null){
                computeSmells(previousVersion, version);
            }

            previousVersion = version;
        }

        versionProvider.clean();
    }

    private void computeVersionStatistics(Projects version) throws IOException {
        if(!this.exporter.contains(EvolutionExport.Statistics.PROJECT)){
            return;
        }

        this.exporter.export(EvolutionExport.Statistics.PROJECT, new VersionRecord(version));
    }

    private void computeSmells(Projects version, Projects nextVersion) throws IOException {
        if(!this.exporter.contains(EvolutionExport.Statistics.SMELL)){
            return;
        }

        DifferenceResults differenceResults = findDifferences(version, nextVersion);
        SmellResults smellResults = findSmells(nextVersion, differenceResults);
        this.exporter.export(EvolutionExport.Statistics.SMELL, smellResults.getRecords());
    }

    private SmellResults findSmells(Projects version, DifferenceResults differenceResults){
        SmellResults smellResults = new SmellResults();

        final Set<SmellMetric.Type> metrics = new HashSet<>(4);
        metrics.add(SmellMetric.Type.RESOURCE_OPTIMISM);
        metrics.add(SmellMetric.Type.HARD_CODED_VALUES);
        metrics.add(SmellMetric.Type.EAGER_TEST);
        metrics.add(SmellMetric.Type.CONDITIONAL_TEST_LOGIC);

        final SmellDetector detector = new SmellDetector(metrics);

        for(Project project: version){
            Map<TestCase, Set<Difference>> changes = computeChanges(project.getTestCases(), differenceResults.getDifferences());

            for(TestCase testCase: project.getTestCases()){
                smellResults.addTestCase(version.getDate(), testCase, detector.computeMetrics(testCase), getChanges(testCase, changes));
            }
        }

        return smellResults;
    }

    private DifferenceResults findDifferences(Projects version1, Projects version2){
        DifferenceResults results = new DifferenceResults();

        if(version1 == null || version2 == null){
            return results;
        }

        if(version1.isEmpty() || version2.isEmpty()){
            return results;
        }

        for(Pair<UserKeyword,UserKeyword> keywordPair: NodeMatcher.getPairs(UserKeyword.class, version1, version2)){
            UserKeyword keyword1 = getElement(keywordPair, version1);
            UserKeyword keyword2 = getElement(keywordPair, version2);

            results.update(Difference.of(keyword1, keyword2));
        }

        for(Pair<TestCase,TestCase> testCasePair: NodeMatcher.getPairs(TestCase.class, version1, version2)) {
            TestCase testCase1 = getElement(testCasePair, version1);
            TestCase testCase2 = getElement(testCasePair, version2);

            results.update(Difference.of(testCase1, testCase2));
        }

        for(Pair<VariableAssignment,VariableAssignment> variablePair: NodeMatcher.getPairs(VariableAssignment.class, version1, version2)) {
            VariableAssignment variable1 = getElement(variablePair, version1);
            VariableAssignment variable2 = getElement(variablePair, version2);

            results.update(Difference.of(variable1, variable2));
        }

        return results;
    }

    private SequenceResults findSequences(Projects version1, Projects version2){
        SequenceResults sequenceResults = new SequenceResults();

        for(Pair<TestCase,TestCase> testCasePair: NodeMatcher.getPairs(TestCase.class, version1, version2)) {
            TestCase testCase1 = getElement(testCasePair, version1);
            TestCase testCase2 = getElement(testCasePair, version2);

            Sequence sequence1 = testCase1 != null ? KeywordStatistics.getSequence(testCase1) : null;
            Sequence sequence2 = testCase2 != null ? KeywordStatistics.getSequence(testCase2) : null;

            sequenceResults.addSequence(testCase1, sequence1);
            sequenceResults.addSequence(testCase2, sequence2);
            sequenceResults.addDifference(testCase1, Difference.of(sequence1, sequence2));
        }

        return sequenceResults;
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

    private Map<TestCase, Set<Difference>> computeChanges(List<TestCase> testCases, Set<Difference> differences){
        Map<TestCase, Set<Difference>> changes = new HashMap<>(testCases.size());
        for(TestCase testCase: testCases){
            changes.put(testCase, new HashSet<>());
        }

        for(Difference difference: differences){
            if(difference.getRight() instanceof Node || difference.getLeft() instanceof Node){
                continue;
            }

            final Set<TestCase> changedTestCases = findTestCases((Node) (difference.getLeft() != null ? difference.getLeft() : difference.getRight()));

            for(TestCase testCase: changedTestCases){
                changes.get(testCase).add(difference);
            }
        }

        return changes;
    }

    Set<TestCase> findTestCases(Node node){
        FindTestCaseVisitor visitor = new FindTestCaseVisitor();
        visitor.visit(node, new PathMemory());

        return visitor.getTestCases();
    }

    private Set<Difference> getChanges(TestCase testCase, Map<TestCase, Set<Difference>> changes){
        return changes.getOrDefault(testCase, Collections.emptySet());
    }
}
