package tech.ikora.evolution;

import org.apache.commons.lang3.tuple.Pair;
import tech.ikora.analytics.Action;
import tech.ikora.analytics.Difference;
import tech.ikora.analytics.clones.KeywordCloneDetection;
import tech.ikora.analytics.clones.Clones;
import tech.ikora.evolution.differences.NodeMatcher;
import tech.ikora.evolution.export.EvolutionExport;
import tech.ikora.evolution.export.Exporter;
import tech.ikora.evolution.results.SmellRecordAccumulator;
import tech.ikora.evolution.results.VersionRecord;
import tech.ikora.evolution.versions.FolderProvider;
import tech.ikora.evolution.versions.VersionProvider;
import tech.ikora.model.*;
import tech.ikora.smells.SmellConfiguration;
import tech.ikora.smells.SmellDetector;
import tech.ikora.smells.SmellMetric;
import tech.ikora.smells.SmellResults;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EvolutionRunner {
    private final VersionProvider versionProvider;
    private final EvolutionExport exporter;
    private final SmellConfiguration smellConfiguration;

    public EvolutionRunner(VersionProvider versionProvider, EvolutionExport exporter, SmellConfiguration smellConfiguration){
        this.versionProvider = versionProvider;
        this.exporter = exporter;
        this.smellConfiguration = smellConfiguration;
    }

    public Map<EvolutionExport.Statistics, Exporter> getExporter() {
        return exporter.getExporters();
    }

    public void execute() throws IOException {
        Projects previousVersion = null;
        SmellRecordAccumulator previousRecords = null;

        for(Projects version: versionProvider){
            computeVersionStatistics(version);

            previousRecords = computeSmells(previousVersion, version, previousRecords == null ? null : previousRecords.getNodes());

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

    private SmellRecordAccumulator computeSmells(Projects previousVersion, Projects version, Map<SmellMetric.Type, Set<SourceNode>> previousNodes) throws IOException {
        if(!this.exporter.contains(EvolutionExport.Statistics.SMELL)){
            return new SmellRecordAccumulator();
        }

        boolean ignoreProjectName = versionProvider instanceof FolderProvider;

        Set<Action> edits = findEdits(previousVersion, version, ignoreProjectName);
        SmellRecordAccumulator smellRecordAccumulator = findSmells(version, edits, previousNodes);
        this.exporter.export(EvolutionExport.Statistics.SMELL, smellRecordAccumulator.getRecords());

        return smellRecordAccumulator;
    }

    private SmellRecordAccumulator findSmells(Projects version, Set<Action> edits, Map<SmellMetric.Type, Set<SourceNode>> previousNodes){
        SmellRecordAccumulator smellRecordAccumulator = new SmellRecordAccumulator();

        final SmellDetector detector = SmellDetector.all();
        final String versionId = version.getVersionId();
        final Clones<KeywordDefinition> clones = KeywordCloneDetection.findClones(version);

        this.smellConfiguration.setClones(clones);

        for(Project project: version){
            for(TestCase testCase: project.getTestCases()){
                final SmellResults smellResults = detector.computeMetrics(testCase, this.smellConfiguration);
                smellRecordAccumulator.addTestCase(versionId, testCase, smellResults, edits, previousNodes, this.smellConfiguration);
            }
        }

        return smellRecordAccumulator;
    }

    private Set<Action> findEdits(Projects version1, Projects version2, boolean ignoreProjectName){
        Set<Action> results = new HashSet<>();

        if(version1 == null || version2 == null){
            return results;
        }

        if(version1.isEmpty() || version2.isEmpty()){
            return results;
        }

        for(Pair<UserKeyword,UserKeyword> keywordPair: NodeMatcher.getPairs(version1.getUserKeywords(), version2.getUserKeywords(), ignoreProjectName)){
            UserKeyword keyword1 = getElement(keywordPair, version1);
            UserKeyword keyword2 = getElement(keywordPair, version2);

            results.addAll(Difference.of(keyword1, keyword2).getActions());
        }

        for(Pair<TestCase,TestCase> testCasePair: NodeMatcher.getPairs(version1.getTestCases(), version2.getTestCases(), ignoreProjectName)) {
            TestCase testCase1 = getElement(testCasePair, version1);
            TestCase testCase2 = getElement(testCasePair, version2);

            results.addAll(Difference.of(testCase1, testCase2).getActions());
        }

        for(Pair<VariableAssignment,VariableAssignment> variablePair: NodeMatcher.getPairs(version1.getVariableAssignments(), version2.getVariableAssignments(), ignoreProjectName)) {
            VariableAssignment variable1 = getElement(variablePair, version1);
            VariableAssignment variable2 = getElement(variablePair, version2);

            results.addAll(Difference.of(variable1, variable2).getActions());
        }

        return results;
    }

    private <T extends SourceNode> T getElement(Pair<T,T> pair, Projects version){
        if(pair.getRight() != null && version.contains(pair.getRight().getProject())) {
            return pair.getRight();
        }
        else if(pair.getLeft() != null && version.contains(pair.getLeft().getProject())){
            return pair.getLeft();
        }

        return null;
    }
}
