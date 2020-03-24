package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.model.*;

import java.util.*;

public class DifferenceResults {
    private final Map<Projects, List<Difference>> differences;

    DifferenceResults(){
        differences = new LinkedHashMap<>();
    }

    public List<Difference> getDifferences(Projects version){
        return differences.getOrDefault(version,  new ArrayList<>());
    }

    public void update(Projects version, Difference difference){
        if(difference == null){
            return;
        }

        if(difference.isEmpty()){
            return;
        }

        differences.compute(version, (k, v) ->{
           if(v == null){
               v = new ArrayList<>();
           }

           v.add(difference);
           return v;
        });
    }


}
