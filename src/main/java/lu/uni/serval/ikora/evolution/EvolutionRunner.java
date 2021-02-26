package lu.uni.serval.ikora.evolution;

import lu.uni.serval.ikora.evolution.results.VariableChangeRecord;
import lu.uni.serval.ikora.evolution.export.EvolutionExport;
import lu.uni.serval.ikora.evolution.export.Exporter;
import lu.uni.serval.ikora.evolution.results.SmellRecordAccumulator;
import lu.uni.serval.ikora.evolution.results.VersionRecord;
import lu.uni.serval.ikora.evolution.versions.FolderProvider;
import lu.uni.serval.ikora.evolution.versions.VersionProvider;

import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellDetector;
import lu.uni.serval.ikora.smells.SmellMetric;
import lu.uni.serval.ikora.smells.SmellResults;

import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.core.utils.ArgumentUtils;
import lu.uni.serval.ikora.core.utils.LevenshteinDistance;
import lu.uni.serval.ikora.core.analytics.clones.KeywordCloneDetection;
import lu.uni.serval.ikora.core.analytics.clones.Clones;
import lu.uni.serval.ikora.core.analytics.difference.Difference;
import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.analytics.difference.NodeMatcher;

import org.apache.commons.lang3.tuple.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EvolutionRunner {
    private static final Logger logger = LogManager.getLogger(EvolutionRunner.class);

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
            logger.info(String.format("Starting analysis for version %s...", version.getVersionId()));

            computeVersionStatistics(version);
            previousRecords = computeSmells(previousVersion, version, previousRecords == null ? null : previousRecords.getNodes());
            previousVersion = version;

            logger.info(String.format("Analysis for version %s done.", version.getVersionId()));
        }

        exporter.close();
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

        Set<Edit> edits = findEdits(previousVersion, version, ignoreProjectName);
        SmellRecordAccumulator smellRecordAccumulator = findSmells(version, edits, previousNodes);
        this.exporter.export(EvolutionExport.Statistics.SMELL, smellRecordAccumulator.getRecords());

        return smellRecordAccumulator;
    }

    private SmellRecordAccumulator findSmells(Projects version, Set<Edit> edits, Map<SmellMetric.Type, Set<SourceNode>> previousNodes){
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

    private Set<Edit> findEdits(Projects version1, Projects version2, boolean ignoreProjectName) throws IOException {
        Set<Edit> results = new HashSet<>();

        if(version1 == null || version2 == null){
            return results;
        }

        if(version1.isEmpty() || version2.isEmpty()){
            return results;
        }

        for(Pair<UserKeyword,UserKeyword> keywordPair: NodeMatcher.getPairs(version1.getUserKeywords(), version2.getUserKeywords(), ignoreProjectName)){
            UserKeyword keyword1 = getElement(keywordPair, version1);
            UserKeyword keyword2 = getElement(keywordPair, version2);

            results.addAll(Difference.of(keyword1, keyword2).getEdits());
            storeValueEdits(keyword1, keyword2);
        }

        for(Pair<TestCase,TestCase> testCasePair: NodeMatcher.getPairs(version1.getTestCases(), version2.getTestCases(), ignoreProjectName)) {
            TestCase testCase1 = getElement(testCasePair, version1);
            TestCase testCase2 = getElement(testCasePair, version2);

            results.addAll(Difference.of(testCase1, testCase2).getEdits());
            storeValueEdits(testCase1, testCase2);
        }

        for(Pair<VariableAssignment,VariableAssignment> variablePair: NodeMatcher.getPairs(version1.getVariableAssignments(), version2.getVariableAssignments(), ignoreProjectName)) {
            VariableAssignment variable1 = getElement(variablePair, version1);
            VariableAssignment variable2 = getElement(variablePair, version2);

            results.addAll(Difference.of(variable1, variable2).getEdits());
        }

        return results;
    }

    private void storeValueEdits(KeywordDefinition keyword1, KeywordDefinition keyword2) throws IOException {
        if(keyword1 == null || keyword2 == null){
            return;
        }

        final List<Pair<Step, Step>> stepPairs = LevenshteinDistance.getMapping(keyword1.getSteps(), keyword2.getSteps());

        for(Pair<Step, Step> stepPair: stepPairs){
            final NodeList<Argument> beforeArguments = stepPair.getLeft().getArgumentList();
            final NodeList<Argument> afterArguments = stepPair.getRight().getArgumentList();
            final List<Pair<Argument, Argument>> argPairs = LevenshteinDistance.getMapping(beforeArguments, afterArguments);

            for(Pair<Argument, Argument> argPair: argPairs){
                final List<String> beforeValues = ArgumentUtils.getArgumentValues(argPair.getLeft()).stream().map(Pair::getLeft).collect(Collectors.toList());
                final List<String> afterValues = ArgumentUtils.getArgumentValues(argPair.getRight()).stream().map(Pair::getLeft).collect(Collectors.toList());

                if(beforeValues.size() > 0 && afterValues.size() > 0 && Collections.disjoint(beforeValues, afterValues)){
                    final VariableChangeRecord record = new VariableChangeRecord(argPair.getLeft(), beforeValues, argPair.getRight(), afterValues);
                    this.exporter.export(EvolutionExport.Statistics.VARIABLE_CHANGES, record);
                }
            }
        }
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
