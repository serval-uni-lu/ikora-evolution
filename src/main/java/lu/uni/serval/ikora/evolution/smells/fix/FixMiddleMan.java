package lu.uni.serval.ikora.evolution.smells.fix;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.Keyword;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.core.model.Step;
import lu.uni.serval.ikora.core.model.UserKeyword;
import lu.uni.serval.ikora.core.utils.Ast;
import lu.uni.serval.ikora.smells.SmellConfiguration;

import java.util.Optional;
import java.util.Set;

public class FixMiddleMan extends FixDetection{
    protected FixMiddleMan(SmellConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isFix(Set<SourceNode> nodes, Edit edit) {
        if(isDefaultFix(nodes, edit, Edit.Type.REMOVE_USER_KEYWORD)){
            return true;
        }

        if(edit.getType() == Edit.Type.CHANGE_STEP){
            final Optional<UserKeyword> parent = Ast.getParentByType(edit.getLeft(), UserKeyword.class);

            if(parent.isEmpty() || !nodes.contains(parent.get())){
                return false;
            }

            if(!Step.class.isAssignableFrom(edit.getRight().getClass())){
                return false;
            }

            return ((Step)edit.getRight()).getKeywordCall()
                    .filter(call -> call.getKeywordType() != Keyword.Type.USER).isPresent();
        }

        return false;
    }
}
