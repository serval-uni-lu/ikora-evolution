package tech.ikora.json;

import com.fasterxml.jackson.core.JsonGenerator;
import tech.ikora.analytics.Action;
import tech.ikora.analytics.Difference;
import tech.ikora.exception.InvalidArgumentException;
import tech.ikora.model.Differentiable;
import tech.ikora.model.Node;
import tech.ikora.model.Keyword;
import tech.ikora.model.KeywordDefinition;
import tech.ikora.utils.DifferentiableStringList;
import tech.ikora.utils.LevenshteinDistance;
import tech.ikora.utils.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DifferencesJson {
    private enum Type{
        CHANGE_NAME,

        CHANGE_SYNC_STEP,
        CHANGE_USER_STEP,
        CHANGE_ASSERT_STEP,
        CHANGE_LOG_STEP,
        CHANGE_CONTROLFLOW_STEP,
        CHANGE_ACTION_STEP,
        CHANGE_ERROR_STEP,
        CHANGE_GET_STEP,
        CHANGE_UNKNOWN_STEP,

        CHANGE_STEP_TYPE,
        CHANGE_STEP_RETURN_VALUES,

        CHANGE_FOR_LOOP_CONDITION,
        CHANGE_FOR_LOOP_BODY,

        CHANGE_DOCUMENTATION,

        INVALID,

        TOTAL
    }

    private Map<Type, Integer> actions;

    public DifferencesJson() {
        this.actions = new HashMap<>();

        for(Type changeType: Type.values()){
            actions.put(changeType, 0);
        }
    }

    public void add(Difference difference) throws InvalidArgumentException {
        if(difference == null){
            return;
        }

        for(Action action: difference.getActions()){

            Differentiable differentiable = action.getValue();

            if(differentiable == null){
                return;
            }

            if(!(differentiable instanceof Node)){
                throw new InvalidArgumentException("Expected a DifferentiableString got " + differentiable.getClass() + " instead!");
            }

            Node node = (Node)differentiable;

            switch (action.getType()){
                case CHANGE_NAME:
                    actions.put(Type.CHANGE_NAME, actions.get(Type.CHANGE_NAME) + 1);
                    break;
                case CHANGE_STEP_TYPE:
                    actions.put(Type.CHANGE_STEP_TYPE, actions.get(Type.CHANGE_STEP_TYPE) + node.getLoc());
                    break;
                case CHANGE_STEP_RETURN_VALUES:
                    actions.put(Type.CHANGE_STEP_RETURN_VALUES, actions.get(Type.CHANGE_STEP_RETURN_VALUES) + 1);
                    break;
                case CHANGE_FOR_LOOP_CONDITION:
                    actions.put(Type.CHANGE_FOR_LOOP_CONDITION, actions.get(Type.CHANGE_FOR_LOOP_CONDITION) + 1);
                    break;
                case CHANGE_FOR_LOOP_BODY:
                    actions.put(Type.CHANGE_FOR_LOOP_BODY, actions.get(Type.CHANGE_FOR_LOOP_BODY) + node.getLoc());
                    break;
                case ADD_DOCUMENTATION:
                case REMOVE_DOCUMENTATION:
                case CHANGE_DOCUMENTATION:
                    int changes_documentation = getDocumentationChanges(action);
                    actions.put(Type.CHANGE_DOCUMENTATION, actions.get(Type.CHANGE_DOCUMENTATION) + changes_documentation);

                case INVALID:
                    actions.put(Type.INVALID, actions.get(Type.INVALID) + node.getLoc());
                    break;

                case ADD_STEP:
                case REMOVE_STEP:
                case CHANGE_STEP_EXPRESSION:
                case CHANGE_STEP:
                case CHANGE_STEP_ARGUMENTS:
                    changeStep(node);
                    break;
            }

            actions.put(Type.TOTAL, actions.get(Type.TOTAL) + 1);
        }
    }

    private int getDocumentationChanges(Action action) throws InvalidArgumentException {
        if(!KeywordDefinition.class.isAssignableFrom(action.getLeft().getClass())){
            throw new InvalidArgumentException("Expected a Keyword got " + action.getLeft().getClass() + " instead!");
        }

        if(!KeywordDefinition.class.isAssignableFrom(action.getRight().getClass())){
            throw new InvalidArgumentException("Expected a Keyword got " + action.getRight().getClass() + " instead!");
        }

        KeywordDefinition keyword1 = (KeywordDefinition)action.getLeft();
        KeywordDefinition keyword2 = (KeywordDefinition)action.getRight();

        int lines = 0;

        if(action.getType() == Action.Type.ADD_DOCUMENTATION){
            lines = StringUtils.countLines(keyword2.getDocumentation());
        }
        else if(action.getType() == Action.Type.REMOVE_DOCUMENTATION){
            lines = StringUtils.countLines(keyword1.getDocumentation());
        }
        else if(action.getType() == Action.Type.CHANGE_DOCUMENTATION){
            DifferentiableStringList doc1 = DifferentiableStringList.fromTextBlock(keyword1.getDocumentation());
            DifferentiableStringList doc2 = DifferentiableStringList.fromTextBlock(keyword2.getDocumentation());

            lines = LevenshteinDistance.getDifferences(doc1, doc2).size();
        }

        return lines;
    }

    private void changeStep(Node node) throws InvalidArgumentException {
        if(!(node instanceof Keyword)){
            throw new InvalidArgumentException("Expected a Keyword got " + node.getClass() + " instead!");
        }

        Keyword keyword = (Keyword) node;


        switch (keyword.getType()){

            case User:
                actions.put(Type.CHANGE_USER_STEP, actions.get(Type.CHANGE_USER_STEP) + keyword.getLoc());
                break;
            case ControlFlow:
                actions.put(Type.CHANGE_CONTROLFLOW_STEP, actions.get(Type.CHANGE_CONTROLFLOW_STEP) + keyword.getLoc());
                break;
            case Assertion:
                actions.put(Type.CHANGE_ASSERT_STEP, actions.get(Type.CHANGE_ASSERT_STEP) + keyword.getLoc());
                break;
            case Action:
                actions.put(Type.CHANGE_ACTION_STEP, actions.get(Type.CHANGE_ACTION_STEP) + keyword.getLoc());
                break;
            case Log:
                actions.put(Type.CHANGE_LOG_STEP, actions.get(Type.CHANGE_LOG_STEP) + keyword.getLoc());
                break;
            case Error:
                actions.put(Type.CHANGE_ERROR_STEP, actions.get(Type.CHANGE_ERROR_STEP) + keyword.getLoc());
                break;
            case Synchronisation:
                actions.put(Type.CHANGE_SYNC_STEP, actions.get(Type.CHANGE_SYNC_STEP) + keyword.getLoc());
                break;
            case Get:
                actions.put(Type.CHANGE_GET_STEP, actions.get(Type.CHANGE_GET_STEP) + keyword.getLoc());
                break;
            case Unknown:
                actions.put(Type.CHANGE_UNKNOWN_STEP, actions.get(Type.CHANGE_UNKNOWN_STEP) + keyword.getLoc());
                break;
        }
    }

    public void writeJson(JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeObjectFieldStart("changes");

        for(Type type: actions.keySet()){
            jsonGenerator.writeNumberField(cleanName(type.name()), actions.get(type));
        }

        jsonGenerator.writeEndObject();
    }

    public static String cleanName(String raw){
        String clean = raw.replace('_', ' ');
        return clean.toLowerCase();
    }
}
