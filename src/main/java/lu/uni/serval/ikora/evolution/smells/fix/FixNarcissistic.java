package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.core.model.Step;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.utils.NLPUtils;

import java.util.Set;

public class FixNarcissistic extends FixDetection{
    protected FixNarcissistic(SmellConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isFix(Set<SourceNode> nodes, Edit edit) {
        if(edit.getType() != Edit.Type.CHANGE_NAME){
            return false;
        }

        return nodes.contains(edit.getLeft()) && !NLPUtils.isUsingPersonalPronoun((Step) edit.getRight());
    }
}
