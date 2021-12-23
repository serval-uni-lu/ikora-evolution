package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellConfiguration;

import java.util.Set;

public class FixLackOfEncapsulation extends FixDetection{
    protected FixLackOfEncapsulation(SmellConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isFix(Set<SourceNode> nodes, Edit edit) {
        return isDefaultFix(nodes, edit, Edit.Type.REMOVE_STEP, Edit.Type.CHANGE_STEP);
    }
}
