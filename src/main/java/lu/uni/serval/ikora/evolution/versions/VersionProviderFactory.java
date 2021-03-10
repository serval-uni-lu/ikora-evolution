package lu.uni.serval.ikora.evolution.versions;

import lu.uni.serval.commons.git.api.Api;
import lu.uni.serval.commons.git.api.GitEngine;
import lu.uni.serval.commons.git.api.GitEngineFactory;
import lu.uni.serval.commons.git.exception.InvalidGitRepositoryException;
import lu.uni.serval.commons.git.utils.CommitCollector;
import lu.uni.serval.commons.git.utils.GitCommit;
import lu.uni.serval.commons.git.utils.GitUtils;
import lu.uni.serval.commons.git.utils.LocalRepository;
import lu.uni.serval.ikora.evolution.configuration.EvolutionConfiguration;
import lu.uni.serval.ikora.evolution.configuration.FolderConfiguration;
import lu.uni.serval.ikora.evolution.configuration.GitConfiguration;
import lu.uni.serval.ikora.evolution.configuration.GitLocation;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class VersionProviderFactory {
    private static final Logger logger = LogManager.getLogger(VersionProviderFactory.class);

    private VersionProviderFactory() {}

    public static VersionProvider fromConfiguration(EvolutionConfiguration configuration) throws GitAPIException, IOException, InvalidGitRepositoryException {
        VersionProvider provider;

        if(configuration.getFolderConfiguration() != null){
            provider = createFolderProvider(configuration.getFolderConfiguration());
        }
        else if(configuration.getGitConfiguration() != null){
            provider = createGitProvider(configuration.getGitConfiguration());
        }
        else{
            throw new InvalidConfigurationException("Configuration should have a folder or git section");
        }

        return provider;
    }

    private static VersionProvider createFolderProvider(FolderConfiguration configuration){
        return new FolderProvider(configuration.getRootFolder(), configuration.getNameFormat(), configuration.getDateFormat());
    }

    private static VersionProvider createGitProvider(GitConfiguration configuration) throws IOException, InvalidGitRepositoryException, GitAPIException {
        final GitProvider provider = new GitProvider(configuration.getFrequency());

        logger.info("Initializing repositories...");
        for(LocalRepository localRepository: getLocalRepositories(provider.getRootFolder(), configuration)){
            final String branch = configuration.getBranchExceptions().getOrDefault(localRepository.getRemoteUrl(), configuration.getDefaultBranch());

            final Set<String> projectFolders = configuration.getLocations().stream()
                    .filter(l -> l.getUrl().equalsIgnoreCase(localRepository.getRemoteUrl()))
                    .findFirst()
                    .map(GitLocation::getProjectFolders)
                    .orElse(Collections.emptySet());

            final List<GitCommit> commits = new CommitCollector()
                    .forGit(localRepository.getGit())
                    .onBranch(branch)
                    .from(configuration.getStartDate())
                    .to(configuration.getEndDate())
                    .ignoring(configuration.getIgnoreCommits())
                    .filterNoChangeIn(projectFolders)
                    .every(configuration.getFrequency())
                    .limit(configuration.getMaximumCommitsNumber())
                    .collect();

            provider.addRepository(localRepository, commits, projectFolders);
        }
        logger.info("Repositories initialized.");

        return provider;
    }

    private static Set<LocalRepository> getLocalRepositories(File rootFolder, GitConfiguration configuration) throws IOException, InvalidGitRepositoryException {
        Set<LocalRepository> localRepositories = new HashSet<>();

        if(configuration.getGroup() != null && !configuration.getGroup().isEmpty()){
            final GitEngine git = GitEngineFactory.create(Api.Gitlab);

            git.setToken(configuration.getToken());
            git.setUrl(configuration.getUrl());
            git.setCloneFolder(rootFolder.getAbsolutePath());

            if(configuration.getDefaultBranch() != null){
                git.setDefaultBranch(configuration.getDefaultBranch());
            }

            if(configuration.getBranchExceptions() != null){
                for (Map.Entry<String, String> entry: configuration.getBranchExceptions().entrySet()){
                    git.setBranchForProject(entry.getKey(), entry.getValue());
                }
            }

            localRepositories.addAll(git.cloneProjectsFromGroup(configuration.getGroup()));
        }
        else{
            for(GitLocation location: configuration.getLocations()) {
                final File repositoryFolder = new File(rootFolder, FilenameUtils.getBaseName(location.getUrl()));

                final LocalRepository localRepository = GitUtils.loadCurrentRepository(
                        location.getUrl(),
                        configuration.getToken(),
                        repositoryFolder,
                        configuration.getDefaultBranch()
                );

                localRepositories.add(localRepository);
            }
        }

        return localRepositories;
    }
}
