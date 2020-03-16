package tech.ikora.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tech.ikora.analytics.EvolutionResults;
import tech.ikora.analytics.KeywordStatistics;
import tech.ikora.model.*;

import java.io.IOException;

public class KeywordsNamesSerializer extends JsonSerializer<EvolutionResults> {
    @Override
    public void serialize(EvolutionResults results, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();

        for(Project project: results.getProjects()){
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("commit ID", project.getCommitId());
            jsonGenerator.writeStringField("time", project.getDate().toString());

            jsonGenerator.writeArrayFieldStart("keywords");
            writeKeywords(jsonGenerator, project.getUserKeywords());
            writeKeywords(jsonGenerator, project.getTestCases());
            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }

    private void writeKeywords(JsonGenerator jsonGenerator, Iterable<? extends KeywordDefinition> keywords) throws IOException {
        for(KeywordDefinition keyword: keywords){
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("type", keyword.getClass().getSimpleName());
            jsonGenerator.writeNumberField("level", KeywordStatistics.getLevel(keyword));
            jsonGenerator.writeStringField("name", keyword.getName());

            jsonGenerator.writeEndObject();
        }
    }
}
