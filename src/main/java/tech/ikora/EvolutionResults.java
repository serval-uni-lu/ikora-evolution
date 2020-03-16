package tech.ikora;

import tech.ikora.analytics.Clone;
import tech.ikora.analytics.CloneDetection;
import tech.ikora.analytics.Clones;
import tech.ikora.analytics.Difference;
import tech.ikora.model.*;

import java.util.*;

public class EvolutionResults {
    public enum CoEvolutionType{
        CoEvolution, NoCoEvolution, NoChange, Invalid
    }

    private Set<Project> projects;

    private Map<Project, List<Sequence>> sequences;
    private Map<Project, List<Difference>> differences;
    private Map<Project, List<Difference>> sequenceDifferences;

    private List<TimeLine> timeLines;
    private DifferentiableMatcher timeLineMatcher;
    private List<TimeLine> timeLineNotChanged;
    private Map<Project, Clones<UserKeyword>> keywordClones;
    private Map<Project, Clones<TestCase>> testCaseClones;

    private Map<Differentiable, CoEvolutionType> coEvolutionTypes;

    EvolutionResults(){
        projects = new HashSet<>();
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

    public void addProject(Project project) {
        if(project == null){
            return;
        }

        projects.add(project);
    }

    public void addSequence(Project project, Sequence sequence){
        if(sequence == null){
            return;
        }

        List<Sequence> sequenceList = sequences.getOrDefault(project, new ArrayList<>());
        sequenceList.add(sequence);

        sequences.put(project, sequenceList);
    }

    public void addDifference(Project project, Difference difference) {
        if(difference == null){
            return;
        }

        updateTimeLine(difference);

        if(difference.isEmpty()){
            return;
        }

        update(project, difference, differences);
    }

    public void addDifference(Project project, Difference difference, Difference sequenceDifference) {
        addDifference(project, difference);

        if(sequenceDifference == null){
            return;
        }

        if(sequenceDifference.isEmpty()){
            return;
        }

        update(project, sequenceDifference, sequenceDifferences);
    }

    public List<Project> getProjects(){
        List<Project> projectList = new ArrayList<>(projects);

        projectList.sort(Comparator.comparing(Project::getEpoch));

        return projectList;
    }

    public List<Difference> getDifferences(Project project){
        return differences.getOrDefault(project,  new ArrayList<>());
    }

    public List<Difference> getSequenceDifferences(Project project){
        return sequenceDifferences.getOrDefault(project,  new ArrayList<>());
    }

    public List<Sequence> getSequence(Project project){
        return sequences.getOrDefault(project, new ArrayList<>());
    }

    private void update(Project project, Difference difference, Map<Project, List<Difference>> container){
        List<Difference> differences = container.getOrDefault(project, new ArrayList<>());
        differences.add(difference);
        container.put(project, differences);
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

    public Clones getKeywordClones(Project project){
        if(keywordClones == null){
            keywordClones = new HashMap<>();

            for(Project current: projects){
                Clones<UserKeyword> clones = CloneDetection.findClones(current, UserKeyword.class);
                keywordClones.put(current, clones);
            }
        }

        return keywordClones.get(project);
    }

    public Clones getTestCaseClones(Project project){
        if(testCaseClones == null){
            testCaseClones = new HashMap<>();

            for(Project current: projects){
                Clones<TestCase> clones = CloneDetection.findClones(current, TestCase.class);
                testCaseClones.put(current, clones);
            }
        }

        return testCaseClones.get(project);
    }

    public <T extends Node> int getTotalElement(Class<T> nodeType, Clone.Type cloneType, CoEvolutionType coEvolutionType){
        int total = 0;

        for(Project project: projects){
            Set<T> nodes = project.getNodes(nodeType);

            if(nodes == null){
                continue;
            }

            for (Node node : nodes){
                if(!checkCloneCriterion(project, node, cloneType)){
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

    private <T extends Node> boolean checkCloneCriterion(Project project, T node, Clone.Type cloneType){
        Clones<T> clones = null;
        if(node.getClass() == UserKeyword.class){
            clones = getKeywordClones(project);
        }
        else if(node.getClass() == TestCase.class){
            clones = getTestCaseClones(project);
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
