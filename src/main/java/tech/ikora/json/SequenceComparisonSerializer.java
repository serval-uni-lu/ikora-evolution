package tech.ikora.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tech.ikora.analytics.*;
import tech.ikora.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SequenceComparisonSerializer extends JsonSerializer<EvolutionResults> {

    @Override
    public void serialize(EvolutionResults results, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        writeCoEvolution(jsonGenerator, results.getTimeLinesMatches());
        writeNotChanged(jsonGenerator, results.getNotChanged());
        writeProportions(jsonGenerator, results);

        jsonGenerator.writeEndObject();
    }

    private void writeNotChanged(JsonGenerator jsonGenerator, List<TimeLine> notChanged) throws IOException {
        jsonGenerator.writeArrayFieldStart("not changed");

        for(TimeLine timeLine: notChanged){
            if(!timeLine.isKeywordDefinition()){
                continue;
            }

            jsonGenerator.writeStartObject();
            writeTimeLineInfo(jsonGenerator, timeLine);
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }

    private void writeCoEvolution(JsonGenerator jsonGenerator, DifferentiableMatcher timeLinesMatches) throws IOException {
        jsonGenerator.writeArrayFieldStart("coevolution");

        Map<Differentiable, Set<Differentiable>> matched = timeLinesMatches.getMatched();

        for(Differentiable differentiable1: matched.keySet()){
            TimeLine timeLine = (TimeLine)differentiable1;

            if(!timeLine.isKeywordDefinition()){
                continue;
            }

            jsonGenerator.writeStartObject();
            writeTimeLineInfo(jsonGenerator, timeLine);

            jsonGenerator.writeArrayFieldStart("similar");
            for(Differentiable differentiable2: matched.get(timeLine)){
                TimeLine similar = (TimeLine)differentiable2;

                jsonGenerator.writeStartObject();
                writeTimeLineInfo(jsonGenerator, similar);
                jsonGenerator.writeNumberField("similarity", 1 - timeLine.distance(similar));
                jsonGenerator.writeStringField("clone type", timeLine.getCloneType(similar).name());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }

    private void writeTimeLineInfo(JsonGenerator jsonGenerator, TimeLine timeLine) throws IOException {
        jsonGenerator.writeStringField("type", timeLine.getType().getSimpleName());
        jsonGenerator.writeStringField("file", getFileName(timeLine));
        jsonGenerator.writeStringField("name", timeLine.getName());
        jsonGenerator.writeStringField("depth", getDepth(timeLine));
        //writeActions(jsonGenerator, timeLine);
    }

    private void writeActions(JsonGenerator jsonGenerator, TimeLine timeLine) throws IOException {
        jsonGenerator.writeArrayFieldStart("actions");

        for(Action action: timeLine.getActions()){
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("type", action.getType().name());
            jsonGenerator.writeStringField("value", action.getValue().getName());
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }

    private void writeProportions(JsonGenerator jsonGenerator, EvolutionResults results) throws IOException {
        jsonGenerator.writeObjectFieldStart("proportions");

        List<Class<? extends Node>> nodeTypes = new ArrayList<>();
        nodeTypes.add(TestCase.class);
        nodeTypes.add(UserKeyword.class);

        Clone.Type[] cloneTypes = {Clone.Type.TypeI, Clone.Type.TypeII, Clone.Type.None};
        EvolutionResults.CoEvolutionType[] coEvolutionTypes = {EvolutionResults.CoEvolutionType.CoEvolution, EvolutionResults.CoEvolutionType.NoCoEvolution, EvolutionResults.CoEvolutionType.NoChange};

        for(Class<? extends Node> nodeType: nodeTypes){
            jsonGenerator.writeObjectFieldStart(nodeType.getSimpleName());

            for(Clone.Type cloneType: cloneTypes){
                jsonGenerator.writeObjectFieldStart(cloneType.name());

                for(EvolutionResults.CoEvolutionType coEvolutionType: coEvolutionTypes){
                    int total = results.getTotalElement(nodeType, cloneType, coEvolutionType);

                    jsonGenerator.writeNumberField(coEvolutionType.name(), total);
                }

                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndObject();
    }

    private String getFileName(TimeLine timeLine){
        Differentiable last = timeLine.getLastValid();

        if(last == null){
            return "";
        }

        if(!Node.class.isAssignableFrom(last.getClass())){
            return "";
        }

        return ((Node)last).getFile().getName();
    }

    private String getDepth(TimeLine timeLine){
        Differentiable last = timeLine.getLastValid();

        if(last == null){
            return "0";
        }

        if(!KeywordDefinition.class.isAssignableFrom(last.getClass())){
            return "0";
        }

        int depth = KeywordStatistics.getLevel((KeywordDefinition)last);

        return String.valueOf(depth);
    }
}
