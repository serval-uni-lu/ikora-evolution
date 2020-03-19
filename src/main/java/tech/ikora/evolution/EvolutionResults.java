package tech.ikora.evolution;

import org.apache.commons.collections4.list.SetUniqueList;
import tech.ikora.analytics.Clone;
import tech.ikora.analytics.CloneDetection;
import tech.ikora.analytics.Clones;
import tech.ikora.analytics.Difference;
import tech.ikora.evolution.differences.DifferentiableMatcher;
import tech.ikora.evolution.differences.TimeLine;
import tech.ikora.model.*;

import java.util.*;

public class EvolutionResults {
    public enum CoEvolutionType{
        CoEvolution, NoCoEvolution, NoChange, Invalid
    }

    private SetUniqueList<Projects> versions;

    private Map<Projects, List<Sequence>> sequences;
    private Map<Projects, List<Difference>> differences;
    private Map<Projects, List<Difference>> sequenceDifferences;

    private List<TimeLine> timeLines;
    private DifferentiableMatcher timeLineMatcher;
    private List<TimeLine> timeLineNotChanged;
    private Map<Projects, Clones<UserKeyword>> keywordClones;
    private Map<Projects, Clones<TestCase>> testCaseClones;

    private Map<Differentiable, CoEvolutionType> coEvolutionTypes;

    EvolutionResults(){
        versions = SetUniqueList.setUniqueList(new ArrayList<>());
        sequences = new LinkedHashMap<>();

        differences = new LinkedHashMap<>();
        sequenceDifferences = new LinkedHashMap<>();
        timeLines = new ArrayList<>();

        timeLineMatcher = null;
        timeLineNotChanged = null;
        keywordClones = null;
        testCaseClones = null;

        coEvolutionTypes = null;
    }

    public void addVersion(Projects version) {
        if(version == null){
            return;
        }

        versions.add(version);
    }

    public void addSequence(Projects version, Sequence sequence){
        if(sequence == null){
            return;
        }

        List<Sequence> sequenceList = sequences.getOrDefault(version, new ArrayList<>());
        sequenceList.add(sequence);

        sequences.put(version, sequenceList);
    }

    public void addDifference(Projects version, Difference difference) {
        if(difference == null){
            return;
        }

        updateTimeLine(difference);

        if(difference.isEmpty()){
            return;
        }

        update(version, difference, differences);
    }

    public void addDifference(Projects version, Difference difference, Difference sequenceDifference) {
        addDifference(version, difference);

        if(sequenceDifference == null){
            return;
        }

        if(sequenceDifference.isEmpty()){
            return;
        }

        update(version, sequenceDifference, sequenceDifferences);
    }

    public List<Projects> getVersions(){
        return versions;
    }

    public List<Difference> getDifferences(Projects version){
        return differences.getOrDefault(version,  new ArrayList<>());
    }

    public List<Difference> getSequenceDifferences(Projects version){
        return sequenceDifferences.getOrDefault(version,  new ArrayList<>());
    }

    public List<Sequence> getSequence(Project project){
        return sequences.getOrDefault(project, new ArrayList<>());
    }

    private void update(Projects version, Difference difference, Map<Projects, List<Difference>> container){
        List<Difference> differences = container.getOrDefault(version, new ArrayList<>());
        differences.add(difference);
        container.put(version, differences);
    }

    private void updateTimeLine(Difference difference){
        if(difference.getLeft() != null){
            for(TimeLine timeLine : timeLines){
                if(timeLine.add(difference)){
                    return;
                }
            }
        }

        TimeLine timeLine = new TimeLine();
        timeLine.add(difference);

        timeLines.add(timeLine);
    }

    public List<TimeLine> getTimeLines() {
        return timeLines;
    }

    public DifferentiableMatcher getTimeLinesMatches() {
        if(timeLineMatcher == null){
            List<TimeLine> timeLineChanged = new ArrayList<>(timeLines);
            timeLineChanged.removeAll(getNotChanged());

            timeLineMatcher = DifferentiableMatcher.match(timeLineChanged, 0.8);
        }

        return timeLineMatcher;
    }

    public List<TimeLine> getNotChanged(){
        if(timeLineNotChanged == null){
            timeLineNotChanged = new ArrayList<>();

            for(TimeLine timeLine: timeLines){
                if(!timeLine.hasChanged()){
                    timeLineNotChanged.add(timeLine);
                }
            }
        }

        return timeLineNotChanged;
    }

    public CoEvolutionType getCoEvolutionType(Differentiable differentiable){
        if(coEvolutionTypes == null){
            coEvolutionTypes = new HashMap<>();
            for(TimeLine notChanged: getNotChanged()){
                for(Differentiable notChangeItem: notChanged.getItems()){
                    coEvolutionTypes.put(notChangeItem, CoEvolutionType.NoChange);
                }
            }

            for(Map.Entry<Differentiable, Set<Differentiable>> timeLines: getTimeLinesMatches().getMatched().entrySet()){
                CoEvolutionType type = timeLines.getValue().size() > 0 ? CoEvolutionType.CoEvolution: CoEvolutionType.NoCoEvolution;

                for(Differentiable coEvolution: ((TimeLine)timeLines.getKey()).getItems()){
                    coEvolutionTypes.put(coEvolution, type);
                }
            }
        }

        return coEvolutionTypes.getOrDefault(differentiable, CoEvolutionType.Invalid);
    }

    public <T extends Node> Clones<T> getKeywordClones(Projects version){
        if(keywordClones == null){
            keywordClones = new HashMap<>();

            for(Projects currentVersion: versions){
                Clones<UserKeyword> clones = CloneDetection.findClones(currentVersion, UserKeyword.class);
                keywordClones.put(currentVersion, clones);
            }
        }

        return (Clones<T>) keywordClones.get(version);
    }

    public <T extends Node> Clones<T> getTestCaseClones(Projects version){
        if(testCaseClones == null){
            testCaseClones = new HashMap<>();

            for(Projects currentVersion: versions){
                Clones<TestCase> clones = CloneDetection.findClones(currentVersion, TestCase.class);
                testCaseClones.put(currentVersion, clones);
            }
        }

        return (Clones<T>) testCaseClones.get(version);
    }

    public <T extends Node> int getTotalElement(Class<T> nodeType, Clone.Type cloneType, CoEvolutionType coEvolutionType){
        int total = 0;

        for(Projects version: versions){
            Set<T> nodes = version.getNodes(nodeType);

            if(nodes == null){
                continue;
            }

            for (Node node : nodes){
                if(!checkCloneCriterion(version, node, cloneType)){
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

    private <T extends Node> boolean checkCloneCriterion(Projects version, T node, Clone.Type cloneType){
        Clones<T> clones = null;
        if(node.getClass() == UserKeyword.class){
            clones = getKeywordClones(version);
        }
        else if(node.getClass() == TestCase.class){
            clones = getTestCaseClones(version);
        }

        if(clones == null){
            return false;
        }

        return clones.getCloneType(node) == cloneType;
    }

    private boolean checkCoEvolutionCriterion(Node node, CoEvolutionType type){
        CoEvolutionType found = getCoEvolutionType(node);
        return found == type;
    }
}
