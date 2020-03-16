package tech.ikora.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.analytics.Action;
import tech.ikora.analytics.Difference;
import tech.ikora.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DifferenceSerializer extends JsonSerializer<Difference> {
    static final Logger logger = LogManager.getLogger(DifferenceSerializer.class);

    @Override
    public void serialize(Difference difference, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        writeActions(difference, jsonGenerator);
    }

    private void writeActions(Difference difference, JsonGenerator jsonGenerator) throws IOException {

        for(Action action: difference.getActions()){
            jsonGenerator.writeStartObject();

            switch (action.getType()){
                case ADD_TEST_CASE:
                {
                    jsonGenerator.writeStringField("type", "add test case");
                    writeKeywordInfo(jsonGenerator, difference.getRight(), "test case", true);
                }
                break;

                case REMOVE_TEST_CASE:
                {
                    jsonGenerator.writeStringField("type", "remove test case");
                    writeKeywordInfo(jsonGenerator, difference.getLeft(), "test case", true);
                }
                break;


                case ADD_USER_KEYWORD:
                {
                    jsonGenerator.writeStringField("type", "add user keyword");
                    writeKeywordInfo(jsonGenerator, difference.getRight(), "user keyword", true);
                }
                break;

                case REMOVE_USER_KEYWORD:
                {
                    jsonGenerator.writeStringField("type", "remove user keyword");
                    writeKeywordInfo(jsonGenerator, difference.getLeft(), "user keyword", true);
                }
                break;

                case ADD_STEP:
                {
                    jsonGenerator.writeStringField("type", "add step");
                    jsonGenerator.writeStringField("step", getStepName(action.getRight()));

                    writeKeywordInfo(jsonGenerator, difference.getLeft(), "keyword", false);
                }
                break;

                case REMOVE_STEP:
                {
                    jsonGenerator.writeStringField("type", "remove step");
                    jsonGenerator.writeStringField("step", getStepName(action.getLeft()));

                    writeKeywordInfo(jsonGenerator, difference.getLeft(), "keyword", false);
                }
                break;

                case CHANGE_STEP:
                {
                    jsonGenerator.writeStringField("type", "change step");

                    jsonGenerator.writeStringField("before", getStepName(action.getLeft()));
                    jsonGenerator.writeStringField("after", getStepName(action.getRight()));

                    writeKeywordInfo(jsonGenerator, difference.getLeft(), "keyword", false);
                }
                break;

                case CHANGE_STEP_ARGUMENTS:
                {
                    jsonGenerator.writeStringField("type", "change step arguments");

                    KeywordCall callBefore = (KeywordCall) action.getLeft();
                    jsonGenerator.writeStringField("before", listToString(callBefore.getParameters()));

                    KeywordCall callAfter = (KeywordCall) action.getRight();
                    jsonGenerator.writeStringField("after", listToString(callAfter.getParameters()));

                    writeKeywordInfo(jsonGenerator, difference.getLeft(), "keyword", false);
                }
                break;

                case CHANGE_STEP_EXPRESSION:
                {
                    Assignment assignmentBefore = (Assignment) action.getLeft();
                    Assignment assignmentAfter = (Assignment) action.getRight();

                    Optional<KeywordCall> before = assignmentBefore.getKeywordCall();
                    Optional<KeywordCall> after = assignmentAfter.getKeywordCall();

                    if(before.isPresent() && after.isPresent()){
                        jsonGenerator.writeStringField("type", "change step expression");
                        jsonGenerator.writeStringField("before", before.get().toString());
                        jsonGenerator.writeStringField("after", after.get().toString());
                        writeKeywordInfo(jsonGenerator, difference.getLeft(), "keyword", false);
                    }
                }
                break;

                case CHANGE_STEP_TYPE:
                {
                    jsonGenerator.writeStringField("type", "change step type");

                    Step stepBefore = (Step)action.getLeft();
                    jsonGenerator.writeStringField("before", getStepType(stepBefore));

                    Step stepAfter = (Step)action.getRight();
                    jsonGenerator.writeStringField("after", getStepType(stepAfter));

                    writeKeywordInfo(jsonGenerator, difference.getLeft(), "keyword", false);
                }
                break;

                case CHANGE_NAME:
                {
                    jsonGenerator.writeStringField("type", "change keyword name");

                    jsonGenerator.writeStringField("before", difference.getLeft().getName());
                    jsonGenerator.writeStringField("after", difference.getRight().getName());

                    jsonGenerator.writeStringField("file", ((Node)difference.getRight()).getFileName());
                }
                break;

                case ADD_VARIABLE:
                {
                    jsonGenerator.writeStringField("type", "add variable");
                    jsonGenerator.writeStringField("variable", difference.getRight().getName());

                    writeKeywordInfo(jsonGenerator, difference.getRight(), "keyword", false);
                }
                break;

                case REMOVE_VARIABLE:
                {
                    jsonGenerator.writeStringField("type", "remove variable");
                    jsonGenerator.writeStringField("variable", difference.getLeft().getName());

                    writeKeywordInfo(jsonGenerator, difference.getLeft(), "keyword", false);
                }
                break;

                case CHANGE_VARIABLE_DEFINITION:
                {
                    jsonGenerator.writeStringField("type", "change variable definition");

                    jsonGenerator.writeStringField("before", ((Variable)difference.getLeft()).getValueAsString());
                    jsonGenerator.writeStringField("after", ((Variable)difference.getRight()).getValueAsString());

                    jsonGenerator.writeStringField("file", ((Node)difference.getRight()).getFileName());
                }
                break;

                default:
                    logger.warn("Unhandled action type " + action.getType().name());
                    break;
            }

            jsonGenerator.writeEndObject();
        }
    }

    private void writeKeywordInfo(JsonGenerator jsonGenerator, Differentiable differentiable, String name, boolean showSteps) throws IOException {
        if(!(differentiable instanceof KeywordDefinition)){
            return;
        }

        KeywordDefinition keyword =(KeywordDefinition)differentiable;

        jsonGenerator.writeObjectFieldStart(name);

        jsonGenerator.writeStringField("file", keyword.getFileName());
        jsonGenerator.writeStringField("name", keyword.getName());

        if(showSteps){
            jsonGenerator.writeArrayFieldStart("steps");

            for(Step step: keyword){
                jsonGenerator.writeString(step.getName());
            }

            jsonGenerator.writeEndArray();
        }

        jsonGenerator.writeEndObject();
    }

    private String listToString(List<Value> values){
        StringBuilder builder = new StringBuilder();

        for(Value value : values){
            if(builder.length() != 0){
                builder.append("\t");
            }

            builder.append(value.toString());
        }

        return builder.toString();
    }

    private String getStepType(Keyword step){
        String type = "Unknown Type";

        if(step instanceof KeywordCall){
            type = "Keyword Call";
        }
        else if (step instanceof Assignment) {
            type = "Assignment";
        }
        else if (step instanceof ForLoop) {
            type = "For Loop";
        }

        return type;
    }

    private String getStepName(Differentiable step){
        if(!(step instanceof Step)){
            logger.warn("Expecting a Step got " + step.getClass().getSimpleName() + " instead");
            return "INVALID STEP";
        }

        return step.getName();
    }
}
