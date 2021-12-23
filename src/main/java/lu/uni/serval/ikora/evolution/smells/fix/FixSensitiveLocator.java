package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.builder.resolver.ValueResolver;
import lu.uni.serval.ikora.core.model.Argument;
import lu.uni.serval.ikora.core.model.Literal;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.core.model.VariableAssignment;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.utils.LocatorUtils;

import java.util.Set;
import java.util.stream.Stream;

public class FixSensitiveLocator extends FixDetection{
    protected FixSensitiveLocator(SmellConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isFix(Set<SourceNode> nodes, Edit edit) {
        return isContaining(nodes, edit)
                && Literal.class.isAssignableFrom(edit.getRight().getClass())
                && !LocatorUtils.isComplex(edit.getRight().getName(), configuration.getMaximumLocatorSize());
    }

    private boolean isContaining(Set<SourceNode> nodes, Edit edit){
        return nodes.contains(edit.getLeft()) ||
        nodes.stream().filter(Argument.class::isInstance)
                .map(Argument.class::cast)
                .flatMap(n -> ValueResolver.getValueNodes(n).stream())
                .flatMap(n -> n instanceof VariableAssignment ? ((VariableAssignment)n).getValues().stream() : Stream.of(n))
                .anyMatch(n -> n == edit.getLeft() || n == edit.getLeft().getAstParent(false));
    }
}
