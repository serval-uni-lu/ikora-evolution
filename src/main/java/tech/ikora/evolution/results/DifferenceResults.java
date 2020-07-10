package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.analytics.visitor.FindTestCaseVisitor;
import tech.ikora.analytics.visitor.PathMemory;
import tech.ikora.model.Differentiable;
import tech.ikora.model.SourceNode;
import tech.ikora.model.TestCase;

import java.util.*;

public class DifferenceResults {
    private final Set<Difference> differences;
    private final Map<TestCase, Set<Difference>> testsToDifferences;
    private final Map<TestCase, TestCase> previousTestCases;

    public DifferenceResults(){
        differences = new HashSet<>();
        testsToDifferences = new HashMap<>();
        previousTestCases = new HashMap<>();
    }

    public Set<Difference> getDifferences(){
        return differences;
    }

    public Set<Difference> getDifferences(TestCase testCase) {
        return testsToDifferences.getOrDefault(testCase, new HashSet<>());
    }

    public Optional<TestCase> getPrevious(TestCase testCase){
        return Optional.ofNullable(previousTestCases.get(testCase));
    }

    public void update(Difference difference){
        if(difference == null){
            return;
        }

        if(difference.getRight() instanceof TestCase && (difference.getLeft() == null || difference.getLeft() instanceof TestCase)){
            previousTestCases.putIfAbsent((TestCase)difference.getRight(), (TestCase)difference.getLeft());
        }

        if(difference.isEmpty()){
            return;
        }

        final Set<TestCase> testCases = findTestCases(difference.getRight());

        for(TestCase testCase: testCases){
            testsToDifferences.putIfAbsent(testCase, new HashSet<>());
            testsToDifferences.get(testCase).add(difference);
        }
    }

    private Set<TestCase> findTestCases(Differentiable node){
        if(node == null || !SourceNode.class.isAssignableFrom(node.getClass())){
            return Collections.emptySet();
        }

        FindTestCaseVisitor visitor = new FindTestCaseVisitor();
        visitor.visit((SourceNode) node, new PathMemory());

        return visitor.getTestCases();
    }
}
