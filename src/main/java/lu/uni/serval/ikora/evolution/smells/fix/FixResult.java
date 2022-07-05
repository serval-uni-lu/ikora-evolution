package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.evolution.smells.Sequence;
import lu.uni.serval.ikora.smells.SmellMetric;

public class FixResult {
    private boolean isValid;
    private final SmellMetric.Type type;
    private final SourceNode node;
    private final Sequence sequence;

    public FixResult(SmellMetric.Type type, SourceNode node, Sequence sequence){
        this.isValid = true;
        this.type = type;
        this.node = node;
        this.sequence = sequence;
    }

    public static FixResult noFix(){
        FixResult result = new FixResult(null, null, null);
        result.isValid = false;

        return result;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public SmellMetric.Type getType() {
        return type;
    }

    public SourceNode getNode() {
        return node;
    }

    public Sequence getSequence(){
        return this.sequence;
    }
}
