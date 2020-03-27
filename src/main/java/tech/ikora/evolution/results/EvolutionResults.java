package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.evolution.differences.TimeLine;
import tech.ikora.model.*;

import java.util.*;

public class EvolutionResults {
    public enum CoEvolutionType{
        CoEvolution, NoCoEvolution, NoChange, Invalid
    }

    private final Map<Differentiable, CoEvolutionType> coEvolutionTypes;
    private final TimeLineResults timeLineResults;
    private final CloneResults cloneResults;

    public EvolutionResults() {
        this.coEvolutionTypes = new HashMap<>();

        this.timeLineResults = new TimeLineResults();
        this.cloneResults = new CloneResults();
    }

    public void addDifference(Difference difference){
        this.timeLineResults.update(difference);
    }

    public CoEvolutionType getCoEvolutionType(Differentiable differentiable){
        if(coEvolutionTypes.get(differentiable) == null){
            for(TimeLine notChanged: this.timeLineResults.getNotChanged()){
                for(Differentiable notChangeItem: notChanged.getItems()){
                    coEvolutionTypes.put(notChangeItem, CoEvolutionType.NoChange);
                }
            }

            for(Map.Entry<Differentiable, Set<Differentiable>> timeLines: this.timeLineResults.getTimeLinesMatches().getMatched().entrySet()){
                CoEvolutionType type = timeLines.getValue().size() > 0 ? CoEvolutionType.CoEvolution: CoEvolutionType.NoCoEvolution;

                for(Differentiable coEvolution: ((TimeLine)timeLines.getKey()).getItems()){
                    coEvolutionTypes.put(coEvolution, type);
                }
            }
        }

        return coEvolutionTypes.getOrDefault(differentiable, CoEvolutionType.Invalid);
    }

    private boolean checkCoEvolutionCriterion(Node node, CoEvolutionType type){
        CoEvolutionType found = getCoEvolutionType(node);
        return found == type;
    }
}
