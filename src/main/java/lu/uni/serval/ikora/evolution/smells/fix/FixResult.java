package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.time.Duration;
import java.util.List;

public class FixResult {
    private boolean isValid;
    private final SmellMetric.Type type;
    private final SourceNode node;
    private final List<SourceNode> history;

    public FixResult(SmellMetric.Type type, SourceNode node, List<SourceNode>history){
        this.isValid = true;
        this.type = type;
        this.node = node;
        this.history = history;
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

    public int getNumberVersions() {
        return history.size();
    }

    public Duration getDuration() {
        SourceNode first = history.get(0);
        SourceNode last = history.get(history.size() - 1);

        return Duration.between(first.getProject().getDate(), last.getProject().getDate());
    }
}
