package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellConfiguration;

import java.util.Arrays;
import java.util.Set;

public abstract class FixDetection {
    protected final SmellConfiguration configuration;

    protected FixDetection(SmellConfiguration configuration) {
        this.configuration = configuration;
    }

    protected boolean isDefaultFix(Set<SourceNode> nodes, Edit edit, Edit.Type... types){
        if(Arrays.stream(types).noneMatch(t -> edit.getType() == t)){
            return false;
        }

        return nodes.contains(edit.getLeft());
    }

    public abstract boolean isFix(Set<SourceNode> nodes, Edit edit);
}
