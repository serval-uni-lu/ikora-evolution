package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.Keyword;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.NodeUtils;
import lu.uni.serval.ikora.smells.SmellConfiguration;

import java.util.Set;

public class FixConditionalAssertion extends FixDetection{
    protected FixConditionalAssertion(SmellConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isFix(Set<SourceNode> nodes, Edit edit) {
        return nodes.contains(edit.getLeft()) && NodeUtils.isCallType(edit.getRight(), Keyword.Type.ASSERTION, true);
    }
}
