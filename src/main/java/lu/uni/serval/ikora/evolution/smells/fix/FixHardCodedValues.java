package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellConfiguration;

import java.util.Set;

public class FixHardCodedValues extends FixDetection{
    public FixHardCodedValues(SmellConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isFix(Set<SourceNode> nodes, Edit edit) {
        return isDefaultFix(nodes, edit, Edit.Type.CHANGE_VALUE_TYPE);
    }
}
