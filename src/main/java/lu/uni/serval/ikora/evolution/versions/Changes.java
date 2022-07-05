package lu.uni.serval.ikora.evolution.versions;

import lu.uni.serval.ikora.core.analytics.difference.Difference;
import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.analytics.difference.NodeMatcher;
import lu.uni.serval.ikora.core.model.KeywordDefinition;
import lu.uni.serval.ikora.core.model.Projects;
import lu.uni.serval.ikora.core.model.SourceNode;
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
        final Optional<KeywordDefinition> keyword = Ast.getParentByType(node, KeywordDefinition.class);

        if(keyword.isEmpty()){
            return Optional.empty();
        }

        final Optional<KeywordDefinition> previousKeyword = pairs.stream()
                .filter(p -> p.getRight() == keyword.get())
                .map(Pair::getLeft)
                .map(KeywordDefinition.class::cast)
                .findAny();

        if(previousKeyword.isEmpty()){
            return Optional.empty();
        }

        return findPreviousNode(node, previousKeyword.get(), keyword.get());
    }

    private Optional<SourceNode> findPreviousNode(SourceNode node, KeywordDefinition k1, KeywordDefinition k2){
        final Deque<SourceNode> parents = new LinkedList<>();
        SourceNode parent = node;

        while(true){
            parents.push(parent);

            if(parent == k2 || parent == null){
                break;
            }

            parent = parent.getAstParent();
        }

        List<SourceNode> candidates = k1.getAstChildren();
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
