package tech.ikora;

import tech.ikora.model.Differentiable;

import java.util.*;

public class DifferentiableMatcher {
    private Map<Differentiable, Set<Differentiable>> matched;
    private List<? extends Differentiable> differentiableList;
    private double threshold;

    private DifferentiableMatcher(List<? extends Differentiable> differentiableList, double threshold){
        this.differentiableList = differentiableList;
        this.matched = new HashMap<>();
        this.threshold = threshold;
    }

    public static DifferentiableMatcher match(List<? extends Differentiable> differentiableList, double threshold){
        DifferentiableMatcher matcher = new DifferentiableMatcher(differentiableList, threshold);

        for(Differentiable differentiable1: matcher.differentiableList){
            for(Differentiable differentiable2: matcher.differentiableList){
                if(differentiable1 == differentiable2){
                    continue;
                }

                matcher.compare(differentiable1, differentiable2);
            }
        }

        return matcher;
    }

    private void compare(Differentiable differentiable1, Differentiable differentiable2){
        Set<Differentiable> set = this.matched.getOrDefault(differentiable1, new HashSet<>());

        if((1 - differentiable1.distance(differentiable2)) >= threshold){
            set.add(differentiable2);
        }

        this.matched.put(differentiable1, set);
    }

    public Map<Differentiable, Set<Differentiable>> getMatched(){
        return this.matched;
    }
}
