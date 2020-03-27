package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;

import java.util.*;

public class DifferenceResults {
    private final Set<Difference> differences;

    public DifferenceResults(){
        differences = new HashSet<>();
    }

    public Set<Difference> getDifferences(){
        return differences;
    }

    public void update(Difference difference){
        if(difference == null){
            return;
        }

        if(difference.isEmpty()){
            return;
        }

        differences.add(difference);
    }
}
