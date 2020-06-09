package tech.ikora.evolution.results;

import tech.ikora.analytics.clones.Clone;
import tech.ikora.analytics.clones.CloneDetection;
import tech.ikora.analytics.clones.Clones;
import tech.ikora.model.*;

import java.util.HashMap;
import java.util.Map;

public class CloneResults {
    private Map<Projects, Clones<UserKeyword>> keywordClones;
    private Map<Projects, Clones<TestCase>> testCaseClones;

    public CloneResults(){
        this.keywordClones = new HashMap<>();
        this.testCaseClones = new HashMap<>();
    }

    public Clones<UserKeyword> getKeywordClones(Projects version){
        if(keywordClones.get(version) == null){
            Clones<UserKeyword> clones = CloneDetection.findClones(version, UserKeyword.class);
            keywordClones.put(version, clones);
        }

        return keywordClones.get(version);
    }

    public Clones<TestCase> getTestCaseClones(Projects version){
        if(testCaseClones.get(version) == null){
            Clones<TestCase> clones = CloneDetection.findClones(version, TestCase.class);
            testCaseClones.put(version, clones);
        }

        return testCaseClones.get(version);
    }

    public <T extends SourceNode> boolean checkCloneCriterion(Projects version, T node, Clone.Type cloneType){
        Clones<T> clones = null;
        if(node.getClass() == UserKeyword.class){
            clones = (Clones<T>) getKeywordClones(version);
        }
        else if(node.getClass() == TestCase.class){
            clones = (Clones<T>) getTestCaseClones(version);
        }

        if(clones == null){
            return false;
        }

        return clones.getCloneType(node) == cloneType;
    }
}
