package tech.ikora.evolution;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import tech.ikora.evolution.configuration.EvolutionConfiguration;
import tech.ikora.evolution.configuration.FolderConfiguration;
import tech.ikora.evolution.configuration.GitConfiguration;
import tech.ikora.evolution.versions.GitProvider;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.GitUtils;
import tech.ikora.gitloader.git.LocalRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvolutionRunnerFactory {
    public static EvolutionRunner fromConfiguration(EvolutionConfiguration configuration) throws GitAPIException, IOException {
        EvolutionRunner runner;

        if(configuration.getFolderConfiguration() != null){
            runner = fromFolder(configuration.getFolderConfiguration());
        }
        else if(configuration.getGitConfiguration() != null){
            runner = fromGit(configuration.getGitConfiguration());
        }
        else{
            throw new InvalidConfigurationException("Configuration should have a folder or git section");
        }

        return runner;
    }

    private static EvolutionRunner fromFolder(FolderConfiguration configuration){
        throw new NotImplementedException("fromFolder method not implemented yet");
    }

    private static EvolutionRunner fromGit(GitConfiguration configuration) throws IOException, GitAPIException {
        final GitProvider provider = new GitProvider(configuration.getFrequency());

        for(String url: configuration.getUrls()){
            final File repositoryFolder = new File(provider.getRootFolder(), FilenameUtils.getBaseName(url));

            final LocalRepository localRepository = GitUtils.loadCurrentRepository(
                    url,
                    configuration.getToken(),
                    repositoryFolder,
                    configuration.getBranch()
            );

            List<GitCommit> commits = GitUtils.getCommits(localRepository.getGit(),
                    configuration.getStartDate(),
                    configuration.getEndDate(),
                    configuration.getBranch()
            );

            commits = Utils.removeIgnoredCommit(commits, configuration.getIgnoreCommits());
            commits = Utils.filterCommitsByFrequency(commits, configuration.getFrequency());
            commits = Utils.truncateCommits(commits, configuration.getMaximumCommitsNumber());

            provider.addRepository(localRepository, commits);
        }

        return  new EvolutionRunner(provider);
    }
}
