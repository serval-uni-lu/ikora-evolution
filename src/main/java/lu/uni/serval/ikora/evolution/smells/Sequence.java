package lu.uni.serval.ikora.evolution.smells;

import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.util.*;

public class Sequence {
    private final SmellMetric.Type type;
    private final List<SourceNode> nodeList;

    public Sequence(SmellMetric.Type type){
        this.type = type;
        this.nodeList = new LinkedList<>();
    }

    public SmellMetric.Type getType(){
        return type;
    }

    public void addNode(SourceNode node){
        nodeList.add(node);
    }

    public int getNumberVersions(){
        return this.nodeList.size();
    }

    public boolean contains(int lastVersions, SourceNode node) {
        if(node == null){
            return false;
        }

        int counter = 0;
        ListIterator<SourceNode> it = nodeList.listIterator(nodeList.size());
        while(lastVersions > counter++ && it.hasPrevious()){
            SourceNode current = it.previous();
            if(current == node){
                return true;
            }
        }

        return false;
    }
}
