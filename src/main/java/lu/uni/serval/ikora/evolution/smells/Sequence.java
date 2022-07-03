package lu.uni.serval.ikora.evolution.smells;

import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.util.LinkedList;
import java.util.List;

public class Sequence {
    private final SmellMetric.Type type;
    private final List<SourceNode> nodes;

    private SourceNode lastNode;

    public Sequence(SmellMetric.Type type){
        this.type = type;
        this.nodes = new LinkedList<>();
        this.lastNode = null;
    }

    public SmellMetric.Type getType(){
        return type;
    }

    public SourceNode getLastNode(){
        return lastNode;
    }

    List<SourceNode> getNodes(){
        return nodes;
    }

    public void addNode(SourceNode node){
        nodes.add(node);
        lastNode = node;
    }

    public int getNumberVersions(){
        return this.nodes.size();
    }
}
