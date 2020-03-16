package tech.ikora.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tech.ikora.analytics.Clone;
import tech.ikora.analytics.EvolutionResults;
import tech.ikora.model.Project;

import java.io.IOException;

public class ProjectClonesEvolutionSerializer extends JsonSerializer<EvolutionResults> {
    @Override
    public void serialize(EvolutionResults results, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();

        for(Project project: results.getProjects()){
            writeClones(jsonGenerator, project, results);
        }

        jsonGenerator.writeEndArray();
    }

    private void writeClones(JsonGenerator jsonGenerator, Project project, EvolutionResults results) throws IOException {
        int numberKeyword = project.getUserKeywords().size();
        int numberTestCase = project.getTestCases().size();

        int numberKeywordClonesTypeI = results.getKeywordClones(project).size(Clone.Type.TypeI);
        int numberTestCaseClonesTypeI = results.getTestCaseClones(project).size(Clone.Type.TypeI);

        int numberKeywordClonesTypeII = results.getKeywordClones(project).size(Clone.Type.TypeII);
        int numberTestCaseClonesTypeII = results.getTestCaseClones(project).size(Clone.Type.TypeII);

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("commit id", project.getCommitId());
        jsonGenerator.writeNumberField("epoch", project.getEpoch());

        jsonGenerator.writeNumberField("number keywords", numberKeyword);
        jsonGenerator.writeNumberField("number keywords clones Type I", numberKeywordClonesTypeI);
        jsonGenerator.writeNumberField("number keyword clones Type II", numberKeywordClonesTypeII);

        jsonGenerator.writeNumberField("number test cases", numberTestCase);
        jsonGenerator.writeNumberField("number test cases clones Type I", numberTestCaseClonesTypeI);
        jsonGenerator.writeNumberField("number test cases clones Type II", numberTestCaseClonesTypeII);

        jsonGenerator.writeEndObject();
    }
}
