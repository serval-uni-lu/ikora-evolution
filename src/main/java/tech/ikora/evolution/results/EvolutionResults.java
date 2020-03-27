package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.evolution.differences.TimeLine;
import tech.ikora.model.*;
import tech.ikora.smells.SmellMetric;

import java.util.*;

public class EvolutionResults {
    public enum CoEvolutionType{
        CoEvolution, NoCoEvolution, NoChange, Invalid
    }

    private final Map<Differentiable, CoEvolutionType> coEvolutionTypes;

    private final SmellResults smellResults;
    private final DifferenceResults differenceResults;
    private final SequenceResults sequenceResults;
    private final TimeLineResults timeLineResults;
    private final CloneResults cloneResults;

    public EvolutionResults() {
        this.coEvolutionTypes = new HashMap<>();

        this.smellResults = new SmellResults();
        this.differenceResults = new DifferenceResults();
        this.sequenceResults = new SequenceResults();
        this.timeLineResults = new TimeLineResults();
        this.cloneResults = new CloneResults();
    }

    public SmellResults getSmellResults() {
        return smellResults;
    }

    public DifferenceResults getDifferenceResults() {
        return differenceResults;
    }

    public void addDifference(Difference difference){
        this.timeLineResults.update(difference);
        this.differenceResults.update(difference);
    }

    public void addSequence(TestCase testCase, Sequence sequence) {
        this.sequenceResults.addSequence(testCase, sequence);
    }

    public void addSequenceDifference(TestCase testCase, Difference sequenceDifference) {
        this.sequenceResults.addDifference(testCase, sequenceDifference);
    }

    public void setSmells(String versionId, TestCase testCase, Set<SmellMetric> computeMetrics, Set<Difference> changes) {
        this.smellResults.setSmells(versionId, testCase, computeMetrics, changes);
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
