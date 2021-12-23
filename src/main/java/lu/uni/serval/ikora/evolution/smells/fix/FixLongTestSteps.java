package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.analytics.KeywordStatistics;
import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.KeywordDefinition;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.core.model.Step;
import lu.uni.serval.ikora.core.utils.Cfg;
import lu.uni.serval.ikora.smells.SmellConfiguration;

import java.util.Optional;
import java.util.Set;

public class FixLongTestSteps extends FixDetection{
    protected FixLongTestSteps(SmellConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isFix(Set<SourceNode> nodes, Edit edit) {
        final KeywordDefinition previousStep = getPreviousStep(edit, nodes);

        if(previousStep == null){
            return false;
        }

        return KeywordStatistics.getSequenceSize(previousStep) < configuration.getMaximumStepSize();
    }

    private static KeywordDefinition getPreviousStep(Edit edit, Set<SourceNode> nodes){
        if(edit.getType() != Edit.Type.REMOVE_STEP){
            return null;
        }

        if(!Step.class.isAssignableFrom(edit.getLeft().getClass())){
            return null;
        }

        return getRelevantStep((Step) edit.getLeft(), nodes);
    }

    private static KeywordDefinition getRelevantStep(Step step, Set<SourceNode> nodes){
        for(SourceNode node: nodes){
            final Optional<KeywordDefinition> parent = Cfg.getCallerByName(step, node.getDefinitionToken());

            if(parent.isPresent() ){
                return parent.get();
            }
        }

        return null;
    }
}
