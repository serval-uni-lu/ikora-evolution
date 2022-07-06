package lu.uni.serval.ikora.evolution.versions;

import lu.uni.serval.ikora.core.analytics.difference.Difference;
import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.analytics.difference.NodeMatcher;
import lu.uni.serval.ikora.core.model.KeywordDefinition;
import lu.uni.serval.ikora.core.model.Projects;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.core.model.VariableAssignment;
import lu.uni.serval.ikora.core.utils.Ast;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Changes {
    private final Set<Edit> edits;
    private final Set<Pair<? extends SourceNode, ? extends SourceNode>> pairs;

    private Changes(Set<Edit> edits, Set<Pair<? extends SourceNode, ? extends SourceNode>> pairs) {
        this.edits = edits;
        this.pairs = pairs;
    }

    public static Changes fromVersions(Projects version1, Projects version2, boolean ignoreProjectName){
        if(version1 == null || version2 ==null || version1.isEmpty() || version2.isEmpty()){
            return new Changes(Collections.emptySet(), Collections.emptySet());
        }

        final Set<Pair<? extends SourceNode, ? extends SourceNode>> pairs = new HashSet<>();

        pairs.addAll(NodeMatcher.getPairs(version1.getTestCases(), version2.getTestCases(), ignoreProjectName));
        pairs.addAll(NodeMatcher.getPairs(version1.getUserKeywords(), version2.getUserKeywords(), ignoreProjectName));
        pairs.addAll(NodeMatcher.getPairs(version1.getVariableAssignments(), version2.getVariableAssignments(), ignoreProjectName));

        final Set<Edit> edits = pairs.stream()
                .flatMap(p -> Difference.of(p.getLeft(), p.getRight()).getEdits().stream())
                .collect(Collectors.toSet());

        return new Changes(edits, pairs);
    }

    public Set<Edit> getEdits() {
        return edits;
    }

    public Set<Pair<? extends SourceNode, ? extends SourceNode>> getPairs() {
        return pairs;
    }

    public Optional<SourceNode> findPreviousNode(SourceNode node){
        Optional<SourceNode> parent = getParent(node);

        if(parent.isEmpty()){
            return Optional.empty();
        }

        final Optional<SourceNode> previousParent = pairs.stream()
                .filter(p -> p.getRight() == parent.get())
                .map(Pair::getLeft)
                .map(SourceNode.class::cast)
                .findAny();

        if(previousParent.isEmpty()){
            return Optional.empty();
        }

        return findPreviousNode(node, previousParent.get(), parent.get());
    }

    private Optional<SourceNode> getParent(SourceNode node){
        final Optional<KeywordDefinition> keyword = Ast.getParentByType(node, KeywordDefinition.class);

        if(keyword.isPresent()){
            return keyword.map(SourceNode.class::cast);
        }

        final Optional<VariableAssignment> assignment = Ast.getParentByType(node, VariableAssignment.class);

        if(assignment.isPresent()){
            return assignment.map(SourceNode.class::cast);
        }

        return Optional.empty();
    }

    private Optional<SourceNode> findPreviousNode(SourceNode node, SourceNode p1, SourceNode p2){
        final Deque<SourceNode> parents = new LinkedList<>();
        SourceNode parent = node;

        while(true){
            parents.push(parent);

            if(parent == p2 || parent == null){
                break;
            }

            parent = parent.getAstParent();
        }

        List<SourceNode> candidates = p1.getAstChildren();
        Optional<SourceNode> candidate = Optional.empty();

        while (!parents.isEmpty()){
            final SourceNode current = parents.pop();
            candidate = findBestCandidate(current, candidates);

            if(candidate.isEmpty()){
                return Optional.empty();
            }
        }

        return candidate;
    }

    private Optional<SourceNode> findBestCandidate(SourceNode current, List<SourceNode> candidates){
        for(SourceNode candidate: candidates){
            if(current.differences(candidate).isEmpty()){
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }
}
