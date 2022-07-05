package lu.uni.serval.ikora.evolution.smells;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.evolution.versions.Changes;
import lu.uni.serval.ikora.smells.SmellMetric;
import lu.uni.serval.ikora.smells.SmellResult;

import java.util.*;

public class History {

    private final List<Sequence> sequences;

    public History() {
        sequences = new LinkedList<>();
    }

    public void addSmell(SmellResult smell, Changes changes) throws IllegalStateException {
        for(SourceNode node: smell.getNodes()){
            final Optional<SourceNode> previousNode = changes.findPreviousNode(node);

            boolean create = true;
            if(previousNode.isPresent()){
                final Optional<Sequence> sequence = findSequence(smell.getType(), previousNode.get());

                if(sequence.isPresent()){
                    sequence.get().addNode(node);
                    create = false;
                }
            }

            if (create) {
                Sequence newSequence = new Sequence(smell.getType());
                newSequence.addNode(node);
                sequences.add(newSequence);
            }
        }

    }

    public Optional<Sequence> findSequence(SmellMetric.Type type, SourceNode node){
        return sequences.stream()
                .filter(s -> s.getType() == type)
                .filter(s -> s.contains(2, node))
                .findAny();
    }

    public Optional<Sequence> findSequence(SmellMetric.Type type, Edit edit) {
        return sequences.stream()
                .filter(s -> s.getType() == type)
                .filter(s -> s.contains(2, edit.getLeft()) || s.contains(2, edit.getRight()))
                .findAny();
    }
}
