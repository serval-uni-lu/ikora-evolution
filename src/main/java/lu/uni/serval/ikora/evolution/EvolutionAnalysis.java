package lu.uni.serval.ikora.evolution;

import lu.uni.serval.commons.git.exception.InvalidGitRepositoryException;
import lu.uni.serval.ikora.evolution.configuration.EvolutionConfiguration;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import lu.uni.serval.ikora.evolution.configuration.ConfigurationParser;

import java.io.*;

public class EvolutionAnalysis {
    private static final Logger logger = LogManager.getLogger(EvolutionAnalysis.class);

    public static void main(String[] args) {
        try{
            final EvolutionConfiguration configuration = getConfiguration(args);
            final EvolutionRunner runner = EvolutionRunnerFactory.fromConfiguration(configuration);

            runner.execute();

        } catch (ParseException | IOException | GitAPIException | InvalidGitRepositoryException e) {
            logger.error(String.format("Exit with error code 1: %s", e.getMessage()));
            System.exit(1);
        }

        logger.info("Finished with error code 0");
    }

    private static EvolutionConfiguration getConfiguration(String[] args) throws ParseException, IOException {
        Options options = new Options();

        options.addOption("config", true, "path to the json configuration file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(!cmd.hasOption("config")){
            throw new MissingArgumentException("config");
        }

        return ConfigurationParser.parse(cmd.getOptionValue("config"));
    }
}
