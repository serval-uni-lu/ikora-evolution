package lu.uni.serval.ikora.evolution.smells;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.evolution.utils.VersionUtils;
import lu.uni.serval.ikora.smells.SmellMetric;
import lu.uni.serval.ikora.smells.SmellResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class History {

    private final List<Sequence> sequences;

    public History() {
        sequences = new LinkedList<>();
    }

    public void addSmell(SmellResult smell,
                    Set<Pair<? extends SourceNode, ? extends SourceNode>> pairs,
                    Map<SmellMetric.Type, Set<SourceNode>> previousNodes) throws IllegalStateException {

        if(smell.getNodes().isEmpty()){
            return;
        }

        final SourceNode currentNode = findCommonParent(smell.getNodes());

        if(currentNode == null){
            return;
        }

        final Optional<SourceNode> previousNode = VersionUtils.matchPrevious(pairs, currentNode);

        if(previousNode.isEmpty()
                || !previousNodes.getOrDefault(smell.getType(), new HashSet<>()).contains(previousNode.get())){
            Sequence sequence = new Sequence(smell.getType());
            sequence.addNode(currentNode);
            sequences.add(sequence);
        }
        else{
            final List<Sequence> targetSequences = sequences.stream()
                    .filter(sequence -> sequence.getType() == smell.getType())
                    .filter(sequence -> sequence.getLastNode() == previousNode.get())
                    .collect(Collectors.toList());

            if(targetSequences.size() == 1){
                targetSequences.get(0).addNode(currentNode);
            }
        }
    }

    public Optional<Sequence> findSequence(Edit edit) {
        return sequences.stream()
                .filter(s -> s.getLastNode() == edit.getLeft())
                .findAny();
    }

    private SourceNode findCommonParent(Set<SourceNode> nodes){
        if(nodes.size() == 1){
            return nodes.iterator().next();
        }

        final Iterator<SourceNode> it = nodes.iterator();
        SourceNode currentParent = it.next();

        while(it.hasNext() && currentParent != null){
            SourceNode node2 = it.next();
            currentParent = findCommonParent(currentParent, node2);
        }

        return currentParent;
    }

    private SourceNode findCommonParent(SourceNode node1, SourceNode node2){
        if(isAncestor(node1, node2)){
            return node1;
        }

        if(isAncestor(node2, node1)){
            return node2;
        }

        return null;
    }

    private boolean isAncestor(SourceNode node1, SourceNode node2){
        SourceNode parent = node2.getAstParent();

        while (parent != null){
            if(parent == node1){
                return true;
            }

            parent = parent.getAstParent();
        }

        return false;
    }
}
