package lu.uni.serval.ikora.evolution.results;

import lu.uni.serval.ikora.core.analytics.clones.Clones;
import lu.uni.serval.ikora.core.analytics.clones.KeywordCloneDetection;
import lu.uni.serval.ikora.core.model.*;

import java.util.HashMap;
import java.util.Map;

public class CloneResults {
    private final Map<Projects, Clones<KeywordDefinition>> keywords;

    public CloneResults(){
        this.keywords = new HashMap<>();
    }

    public Clones<KeywordDefinition> getKeywords(Projects version){
        return keywords.computeIfAbsent(version, KeywordCloneDetection::findClones);
    }
}
