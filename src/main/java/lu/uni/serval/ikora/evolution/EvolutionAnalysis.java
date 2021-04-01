package lu.uni.serval.ikora.evolution;

import lu.uni.serval.commons.git.exception.InvalidGitRepositoryException;
import lu.uni.serval.ikora.evolution.configuration.EvolutionConfiguration;
import lu.uni.serval.ikora.evolution.export.EvolutionExport;
import lu.uni.serval.ikora.evolution.export.ExporterFactory;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lu.uni.serval.ikora.evolution.configuration.ConfigurationParser;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;

public class EvolutionAnalysis {
    private static final Logger logger = LogManager.getLogger(EvolutionAnalysis.class);

    private static final String CONFIG_OPTION = "config";

    public static void main(String[] args) {
        try{
            final EvolutionConfiguration configuration = getConfiguration(args);

            try(EvolutionExport exporter = ExporterFactory.fromConfiguration(configuration)){
                final EvolutionRunner runner = new EvolutionRunner(exporter, configuration);
                runner.execute();
            }

        } catch (ParseException | IOException | InvalidGitRepositoryException | GitAPIException e) {
            logger.error(String.format("Exit with error code 1: %s", e.getMessage()));
            System.exit(1);
        }

        logger.info("Finished with error code 0");
    }

    private static EvolutionConfiguration getConfiguration(String[] args) throws ParseException, IOException {
        Options options = new Options();

        options.addOption(CONFIG_OPTION, true, "path to the json configuration file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(!cmd.hasOption(CONFIG_OPTION)){
            throw new MissingArgumentException(CONFIG_OPTION);
        }

        return ConfigurationParser.parse(cmd.getOptionValue(CONFIG_OPTION));
    }
}
