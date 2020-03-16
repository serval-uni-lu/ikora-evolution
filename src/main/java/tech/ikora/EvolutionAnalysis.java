package tech.ikora;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.export.json.*;
import tech.ikora.utils.Configuration;
import tech.ikora.utils.Plugin;

import java.io.*;

public class EvolutionAnalysis {
    private static final Logger logger = LogManager.getLogger(EvolutionAnalysis.class);

    public void run() {
        Configuration config = Configuration.getInstance();
        Plugin analytics = config.getPlugin("evolution analytics");

        String gitUrl = analytics.getPropertyAsString("git url", "");
        String branch = analytics.getPropertyAsString("git branch", "master");
        String username = analytics.getPropertyAsString("git user", "");
        String password = analytics.getPropertyAsString("git password", "");

        EvolutionAnalyzer projects = EvolutionAnalyzer.fromGit(gitUrl, branch, username, password);
        EvolutionResults results = projects.findDifferences();

        exportReportEvolution(analytics, results);
        exportKeywordEvolution(analytics, results);
        exportKeywordNames(analytics, results);
        exportKeywordChangeSequence(analytics, results);
        exportKeywordSequenceDifference(analytics, results);
        exportClones(analytics, results);
    }

    private void exportReportEvolution(Plugin analytics, EvolutionResults results) {
        if(analytics.getPropertyAsString("output report differences file").isEmpty()){
            logger.warn("no output  report differences file provided");
            return;
        }

        String fileName = analytics.getPropertyAsString("output report differences file", "./report-differences.json");
        export(new ProjectEvolutionSerializer(), fileName, results);
    }

    private void exportKeywordEvolution(Plugin analytics, EvolutionResults results){
        if(analytics.getPropertyAsString("output keyword differences file").isEmpty()){
            logger.warn("no output keyword differences file provided");
            return;
        }

        String fileName = analytics.getPropertyAsString("output keyword differences file");
        export(new KeywordsEvolutionSerializer(), fileName, results);
    }

    private void exportKeywordNames(Plugin analytics, EvolutionResults results){
        if(analytics.getPropertyAsString("output keyword names file").isEmpty()){
            logger.warn("no output keyword names file provided");
            return;
        }

        String fileName = analytics.getPropertyAsString("output keyword names file");
        export(new KeywordsNamesSerializer(), fileName, results);
    }

    private void exportKeywordChangeSequence(Plugin analytics, EvolutionResults results) {
        if(analytics.getPropertyAsString("output keyword changes sequences file").isEmpty()){
            logger.warn("no output keyword changes sequences file provided");
            return;
        }

        String fileName = analytics.getPropertyAsString("output keyword changes sequences file");
        export(new KeywordsChangeSequenceSerializer(), fileName, results);
    }

    private void exportKeywordSequenceDifference(Plugin analytics, EvolutionResults results) {
        if(analytics.getPropertyAsString("output keyword sequence difference file").isEmpty()){
            logger.warn("no output keyword sequence difference file provided");
            return;
        }

        String fileName = analytics.getPropertyAsString("output keyword sequence difference file");
        export(new SequenceComparisonSerializer(), fileName, results);
    }

    private void exportClones(Plugin analytics, EvolutionResults results) {
        if(analytics.getPropertyAsString("output clones file").isEmpty()){
            logger.warn("no output clones file provided");
            return;
        }

        String fileName = analytics.getPropertyAsString("output clones file");
        export(new ProjectClonesEvolutionSerializer(), fileName, results);
    }

    private void export(JsonSerializer<EvolutionResults> serializer, String fileName, EvolutionResults results){
        try {
            ObjectMapper mapper = new ObjectMapper();

            SimpleModule module = new SimpleModule();
            module.addSerializer(EvolutionResults.class, serializer);
            mapper.registerModule(module);

            File file = new File(fileName);
            mapper.writeValue(file, results);

            logger.info("results written to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
