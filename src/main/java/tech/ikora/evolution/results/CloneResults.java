package tech.ikora.evolution.results;

import tech.ikora.analytics.clones.Clones;
import tech.ikora.analytics.clones.KeywordCloneDetection;
import tech.ikora.model.*;

import java.util.HashMap;
import java.util.Map;

public class CloneResults {
    private final Map<Projects, Clones<KeywordDefinition>> keywords;

    public CloneResults(){
        this.keywords = new HashMap<>();
    }

    public Clones<KeywordDefinition> getKeywords(Projects version){
        if(keywords.get(version) == null){
            Clones<KeywordDefinition> clones = KeywordCloneDetection.findClones(version);
            keywords.put(version, clones);
        }

        return keywords.get(version);
    }
}
