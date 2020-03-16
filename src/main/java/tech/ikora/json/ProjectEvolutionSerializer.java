package tech.ikora.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tech.ikora.EvolutionResults;
import tech.ikora.analytics.*;
import tech.ikora.exception.InvalidTypeException;
import tech.ikora.model.Project;
import tech.ikora.model.Sequence;
import tech.ikora.model.TestCase;
import tech.ikora.model.UserKeyword;

import java.io.IOException;
import java.util.*;

public class ProjectEvolutionSerializer extends JsonSerializer<EvolutionResults> {
    @Override
    public void serialize(EvolutionResults results, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();

        for(Project project: results.getProjects()){
            try{
                ProjectStatistics statistics = new ProjectStatistics(project);

                jsonGenerator.writeStartObject();

                // TODO: manage commit version
                //jsonGenerator.writeStringField("commit ID", project.getCommitId());
                jsonGenerator.writeStringField("time", project.getDate().toString());

                jsonGenerator.writeNumberField("number files", statistics.getNumberFiles());
                jsonGenerator.writeNumberField("number keywords", statistics.getNumberKeywords(UserKeyword.class));
                jsonGenerator.writeNumberField("number test cases", statistics.getNumberKeywords(TestCase.class));
                jsonGenerator.writeNumberField("documentation length", statistics.getDocumentationLength());
                jsonGenerator.writeNumberField("lines of code", statistics.getLoc());
                jsonGenerator.writeNumberField("sequence steps number", getSequenceStepNumber(results, project));

                jsonGenerator.writeNumberField("changes sequence steps", getSequenceDifferences(results, project));
                writeDifferences(results, jsonGenerator, project);

                jsonGenerator.writeEndObject();
            } catch (InvalidTypeException e) {
                jsonGenerator.writeStringField("ERROR", e.getMessage());
            }

        }

        jsonGenerator.writeEndArray();
    }

    private int getSequenceStepNumber(EvolutionResults results, Project project) {
        int size = 0;

        for(Sequence sequence: results.getSequence(project)){
            size += sequence.size();
        }

        return size;
    }

    private int getSequenceDifferences(EvolutionResults results, Project project) {
        int sequenceDifferences = 0;

        for(Difference difference: results.getSequenceDifferences(project)){
            if(difference == null){
                continue;
            }

            for (Action action: difference.getActions()){
                switch (action.getType()){
                    case ADD_SEQUENCE:
                    case REMOVE_SEQUENCE:
                        sequenceDifferences += ((Sequence)difference.getValue()).size();
                        break;
                    default:
                        sequenceDifferences++;
                }
            }
        }

        return sequenceDifferences;
    }

    private void writeDifferences(EvolutionResults results, JsonGenerator jsonGenerator, Project project) throws IOException {
        Map<Action.Type, Integer> changes = new HashMap<>();

        for(Action.Type changeType: Action.Type.values()){
            changes.put(changeType, 0);
        }

        for(Difference difference: results.getDifferences(project)){
            for(Action action: difference.getActions()){
                changes.put(action.getType(), changes.get(action.getType()) + 1);
            }
        }

        jsonGenerator.writeObjectFieldStart("changes");

        for(Map.Entry<Action.Type, Integer> change: changes.entrySet()){
            jsonGenerator.writeNumberField(DifferencesJson.cleanName(change.getKey().name()), change.getValue());
        }

        jsonGenerator.writeEndObject();
    }
}
