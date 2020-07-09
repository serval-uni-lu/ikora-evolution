package tech.ikora.evolution.differences;

import org.apache.commons.lang3.tuple.Pair;
import tech.ikora.analytics.clones.Clone;
import tech.ikora.analytics.clones.CloneDetection;
import tech.ikora.model.*;

import java.io.File;
import java.util.*;

public class NodeMatcher {
    enum Edit{
        ChangeName, ChangeFolder, ChangeFile, ChangeAll
    }

    public static <T extends SourceNode> List<Pair<T,T>> getPairs(Class<T> type, Projects version1, Projects version2) {
        List<Pair<T,T>> pairs = new ArrayList<>();

        Set<T> nodes1 = version1.getNodes(type);
        Set<T> nodes2 = version2.getNodes(type);

        List<T> unmatched = new ArrayList<>();

        while(!nodes1.isEmpty()){
            T node1 = nodes1.iterator().next();
            Set<T> nodesFound2 = matchNode(nodes2, node1);

            if(nodesFound2.isEmpty()){
                unmatched.add(node1);
            }
            else{
                //TODO: Find best match if multiple hits
                pairs.add(Pair.of(node1, nodesFound2.iterator().next()));
                nodes2.remove(nodesFound2.iterator().next());
            }

            nodes1.remove(node1);
        }

        while(!nodes2.isEmpty()){
            T keyword2 = nodes2.iterator().next();
            T keyword1 = findBestCandidate(keyword2, unmatched);

            pairs.add(Pair.of(keyword1, keyword2));
            nodes2.remove(keyword2);
        }

        while(!unmatched.isEmpty()){
            T keyword1 = unmatched.iterator().next();

            pairs.add(Pair.of(keyword1, null));
            unmatched.remove(keyword1);
        }

        return pairs;
    }

    private static <T extends SourceNode> Set<T> matchNode(Set<T> nodeList, T node){
        Set<T> nodesFound = new HashSet<>();

        for(T currentNode: nodeList){
            if(matches(node, currentNode)){
                nodesFound.add(node);
            }
        }

        return nodesFound;
    }

    private static boolean matches(SourceNode node1, SourceNode node2){
        if(!isSameProject(node1, node2)){
            return false;
        }

        if(!isSameFile(node1, node2)){
            return false;
        }

        return node1.matches(node2.getNameToken());
    }

    private static boolean isSameProject(SourceNode node1, SourceNode node2){
        Project project1 = node1.getProject();
        Project project2 = node2.getProject();

        if(project1 == project2){
            return true;
        }

        if(project1 == null || project2 == null){
            return false;
        }

        return project1.getName().equalsIgnoreCase(project2.getName());
    }

    private static boolean isSameFile(SourceNode node1, SourceNode node2){
        return node1.getLibraryName().equalsIgnoreCase(node2.getLibraryName());
    }

    private static <T extends SourceNode> Map<Edit, List<T>> findPotentialCandidates(T keyword, List<T> unmatched) {
        String fileName = new File(keyword.getLibraryName()).getName();
        Map<Edit, List<T>> candidates = new HashMap<>();

        for (T current: unmatched){
            if(CloneDetection.getCloneType(keyword, current) != Clone.Type.TYPE_1){
                continue;
            }

            String currentFileName = current.getLibraryName();

            if(current.getLibraryName().equals(keyword.getLibraryName())){
                List<T> list = candidates.getOrDefault(Edit.ChangeName, new ArrayList<>());
                list.add(current);
                candidates.put(Edit.ChangeName, list);
            }
            else if(current.getName().equals(keyword.getName()) && currentFileName.equals(fileName)){
                List<T> list = candidates.getOrDefault(Edit.ChangeFolder, new ArrayList<>());
                list.add(current);
                candidates.put(Edit.ChangeFolder, list);
            }
            else if(current.getName().equals(keyword.getName())){
                List<T> list = candidates.getOrDefault(Edit.ChangeFile, new ArrayList<>());
                list.add(current);
                candidates.put(Edit.ChangeFile, list);
            }
            else{
                List<T> list = candidates.getOrDefault(Edit.ChangeAll, new ArrayList<>());
                list.add(current);
                candidates.put(Edit.ChangeAll, list);
            }
        }

        return candidates;
    }

    private static <T extends SourceNode> T findBestCandidate(T keyword, List<T> unmatched){
        Map<Edit, List<T>> candidates = findPotentialCandidates(keyword, unmatched);

        T bestCandidate = null;

        if(!candidates.getOrDefault(Edit.ChangeName, new ArrayList<>()).isEmpty()){
            bestCandidate = candidates.get(Edit.ChangeName).get(0);
        }
        else if(!candidates.getOrDefault(Edit.ChangeFolder, new ArrayList<>()).isEmpty()){
            bestCandidate = candidates.get(Edit.ChangeFolder).get(0);
        }
        else if(!candidates.getOrDefault(Edit.ChangeFile, new ArrayList<>()).isEmpty()){
            bestCandidate = candidates.get(Edit.ChangeFile).get(0);
        }
        else if(!candidates.getOrDefault(Edit.ChangeAll, new ArrayList<>()).isEmpty()){
            bestCandidate = candidates.get(Edit.ChangeAll).get(0);
        }

        if(bestCandidate != null){
            unmatched.remove(bestCandidate);
        }

        return bestCandidate;
    }
}
