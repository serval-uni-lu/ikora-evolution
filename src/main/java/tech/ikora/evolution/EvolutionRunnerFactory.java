package tech.ikora.evolution;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import tech.ikora.evolution.configuration.*;
import tech.ikora.evolution.export.EvolutionExport;
import tech.ikora.evolution.versions.FolderProvider;
import tech.ikora.evolution.versions.GitProvider;
import tech.ikora.evolution.versions.VersionProvider;
import tech.ikora.gitloader.Api;
import tech.ikora.gitloader.GitEngine;
import tech.ikora.gitloader.GitEngineFactory;
import tech.ikora.gitloader.exception.InvalidGitRepositoryException;
import tech.ikora.gitloader.git.CommitCollector;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.GitUtils;
import tech.ikora.gitloader.git.LocalRepository;
import tech.ikora.smells.SmellConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvolutionRunnerFactory {
    public static EvolutionRunner fromConfiguration(EvolutionConfiguration configuration) throws GitAPIException, IOException, InvalidGitRepositoryException {
        final EvolutionExport exporter = createExporter(configuration.getOutputConfiguration());
        final VersionProvider provider = createVersionProvider(configuration);
        final SmellConfiguration smellConfiguration = new SmellConfiguration();


        return new EvolutionRunner(provider, exporter, smellConfiguration);
    }

    private static VersionProvider createVersionProvider(EvolutionConfiguration configuration) throws GitAPIException, IOException, InvalidGitRepositoryException {
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

    private static EvolutionExport createExporter(OutputConfiguration configuration){
        Map<EvolutionExport.Statistics, File> outputFiles = new HashMap<>();

        File smellsCsvFile = configuration.getSmellsCsvFile();
        if(smellsCsvFile != null){
            outputFiles.put(EvolutionExport.Statistics.SMELL, smellsCsvFile);
        }

        File projectsCsvFile = configuration.getProjectsCsvFile();
        if(projectsCsvFile != null){
            outputFiles.put(EvolutionExport.Statistics.PROJECT, projectsCsvFile);
        }

        return new EvolutionExport(configuration.getStrategy(), outputFiles);
    }

    private static VersionProvider createFolderProvider(FolderConfiguration configuration){
        return new FolderProvider(configuration.getRootFolder(), configuration.getNameFormat(), configuration.getDateFormat());
    }

    private static VersionProvider createGitProvider(GitConfiguration configuration) throws IOException, InvalidGitRepositoryException {
        final GitProvider provider = new GitProvider(configuration.getFrequency());

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
