package tech.ikora;

import org.apache.commons.lang3.tuple.Pair;
import tech.ikora.analytics.Difference;
import tech.ikora.analytics.KeywordStatistics;
import tech.ikora.model.*;

import java.util.*;

public class EvolutionAnalyzer {
    private List<Project> projects;

    public EvolutionAnalyzer(){
        projects = new ArrayList<>();
    }

    public static EvolutionAnalyzer fromGit(String gitUrl, String branch, String username, String password) {
        EvolutionAnalyzer analyzer = new EvolutionAnalyzer();

        GitRepository repository = new GitRepository(gitUrl, branch, username, password);
        List<GitCommit> commits = repository.getRevisions();

        for(GitCommit commit: commits){
            repository.checkout(commit.getId(), true);
            Project project = repository.getProject();

            analyzer.projects.add(project);
        }

        return analyzer;
    }


    public EvolutionResults findDifferences(){
        EvolutionResults results = new EvolutionResults();

        Project project1 = null;
        for(Project project2: projects){
            if(project1 == null){
                project1 = project2;
                continue;
            }

            for(Pair<UserKeyword,UserKeyword> keywordPair: NodeMatcher.getPairs(UserKeyword.class, project1, project2)){
                UserKeyword keyword1 = getElement(keywordPair, project1);
                UserKeyword keyword2 = getElement(keywordPair, project2);

                results.addDifference(project1, Difference.of(keyword1, keyword2));
            }

            for(Pair<TestCase,TestCase> testCasePair: NodeMatcher.getPairs(TestCase.class, project1, project2)) {
                TestCase testCase1 = getElement(testCasePair, project1);
                TestCase testCase2 = getElement(testCasePair, project2);

                Sequence sequence1 = testCase1 != null ? KeywordStatistics.getSequence(testCase1) : null;
                Sequence sequence2 = testCase2 != null ? KeywordStatistics.getSequence(testCase2) : null;

                results.addDifference(project1, Difference.of(testCase1, testCase2), Difference.of(sequence1, sequence2));

                results.addSequence(project1, sequence1);
                results.addSequence(project2, sequence2);
            }

            for(Pair<Variable,Variable> variablePair: NodeMatcher.getPairs(Variable.class, project1, project2)) {
                Variable variable1 = getElement(variablePair, project1);
                Variable variable2 = getElement(variablePair, project2);

                results.addDifference(project1, Difference.of(variable1, variable2));
            }

            results.addProject(project1);
            results.addProject(project2);

            project1 = project2;
        }

        return results;
    }

    private <T extends Node> T getElement(Pair<T,T> pair, Project project){
        if(pair.getRight() != null && pair.getRight().getFile() != null && pair.getRight().getFile().getProject() == project) {
            return pair.getRight();
        }
        else if(pair.getLeft() != null && pair.getLeft().getFile() != null && pair.getLeft().getFile().getProject() == project){
            return pair.getLeft();
        }

        return null;
    }
}
