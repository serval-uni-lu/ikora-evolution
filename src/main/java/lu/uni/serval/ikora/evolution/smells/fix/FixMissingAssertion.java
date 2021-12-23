package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.smells.SmellConfiguration;

import java.util.Set;

public class FixMissingAssertion extends FixDetection{
    protected FixMissingAssertion(SmellConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isFix(Set<SourceNode> nodes, Edit edit) {
        if(edit.getRight() == null){
            return false;
        }

        if(edit.getType() == Edit.Type.ADD_STEP && isAddAssertion((Step)edit.getRight())){
            return true;
        }

        return edit.getType() == Edit.Type.ADD_USER_KEYWORD && isAddAssertion((UserKeyword) edit.getRight());
    }

    private static boolean isAddAssertion(Step step){
        return step.getKeywordCall()
                .flatMap(Step::getKeywordCall)
                .map(KeywordCall::getKeywordType)
                .map(t -> t == Keyword.Type.ASSERTION)
                .orElse(false);
    }

    private static boolean isAddAssertion(UserKeyword keyword){
        return keyword.getSteps().stream()
                .anyMatch(FixMissingAssertion::isAddAssertion);
    }
}
