package tech.ikora.evolution;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import tech.ikora.evolution.configuration.ConfigurationParser;
import tech.ikora.evolution.configuration.EvolutionConfiguration;

import java.io.*;

public class EvolutionAnalysis {
    private static final Logger logger = LogManager.getLogger(EvolutionAnalysis.class);

    public static void main(String[] args) {
        try{
            Options options = new Options();

            options.addOption("config", true, "path to the json configuration file");

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if(!cmd.hasOption("config")){
                throw new MissingArgumentException("config");
            }

            EvolutionConfiguration configuration = ConfigurationParser.parse(cmd.getOptionValue("config"));

            EvolutionRunner runner = EvolutionRunnerFactory.fromConfiguration(configuration);
            runner.execute();

        } catch (ParseException | IOException | GitAPIException e) {
            logger.error(String.format("Exit with error: %s", e.getMessage()));
            System.exit(1);
        }

        logger.info("Finished without error");
    }
}
