package tech.ikora.evolution.results;

import org.apache.commons.collections4.list.SetUniqueList;
import tech.ikora.analytics.Difference;
import tech.ikora.analytics.clones.Clone;
import tech.ikora.evolution.differences.TimeLine;
import tech.ikora.model.*;
import tech.ikora.smells.SmellMetric;

import java.util.*;

public class EvolutionResults {
    public enum CoEvolutionType{
        CoEvolution, NoCoEvolution, NoChange, Invalid
    }

    private final Map<Differentiable, CoEvolutionType> coEvolutionTypes;
    private final SetUniqueList<Projects> versions;

    private final SmellResults smellResults;
    private final DifferenceResults differenceResults;
    private final SequenceResults sequenceResults;
    private final TimeLineResults timeLineResults;
    private final CloneResults cloneResults;

    public EvolutionResults() {
        this.coEvolutionTypes = new HashMap<>();
        this.versions = SetUniqueList.setUniqueList(new ArrayList<>());

        this.smellResults = new SmellResults();
        this.differenceResults = new DifferenceResults();
        this.sequenceResults = new SequenceResults();
        this.timeLineResults = new TimeLineResults();
        this.cloneResults = new CloneResults();
    }

    public void addVersion(Projects version) {
        if(version == null){
            return;
        }

        versions.add(version);
    }

    public List<Projects> getVersions(){
        return versions;
    }

    public void addDifference(Projects version, Difference difference){
        this.timeLineResults.update(difference);
        this.differenceResults.update(version, difference);
    }

    public void addSequence(TestCase testCase, Sequence sequence) {
        this.sequenceResults.addSequence(testCase, sequence);
    }

    public void addSequenceDifference(TestCase testCase, Difference sequenceDifference) {
        this.sequenceResults.addDifference(testCase, sequenceDifference);
    }

    public void setSmells(TestCase testCase, Set<SmellMetric> computeMetrics) {
        this.smellResults.setSmells(testCase, computeMetrics);
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

    public <T extends Node> int getTotalElement(Class<T> nodeType, Clone.Type cloneType, CoEvolutionType coEvolutionType){
        int total = 0;

        for(Projects version: versions){
            Set<T> nodes = version.getNodes(nodeType);

            if(nodes == null){
                continue;
            }

            for (Node node : nodes){
                if(!this.cloneResults.checkCloneCriterion(version, node, cloneType)){
                    continue;
                }

                if(!checkCoEvolutionCriterion(node, coEvolutionType)){
                    continue;
                }

                ++total;
            }
        }

        return total;
    }
}
