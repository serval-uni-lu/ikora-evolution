package tech.ikora.evolution;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import tech.ikora.BuildConfiguration;
import tech.ikora.builder.BuildResult;
import tech.ikora.builder.Builder;
import tech.ikora.evolution.configuration.EvolutionConfiguration;
import tech.ikora.evolution.configuration.FolderConfiguration;
import tech.ikora.evolution.configuration.GitConfiguration;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.GitUtils;
import tech.ikora.gitloader.git.LocalRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        EvolutionRunner runner = new EvolutionRunner();

        final LocalRepository repo = GitUtils.loadCurrentRepository(configuration.getUrl(), configuration.getToken(), new File(System.getProperty("java.io.tmpdir")), configuration.getBranch());
        final List<GitCommit> commits = GitUtils.getCommits(repo.getGit(), configuration.getStartDate(), configuration.getEndDate(), configuration.getBranch());

        for(GitCommit commit: commits){
            GitUtils.checkout(repo.getGit(), commit.getId());
            final BuildResult build = Builder.build(repo.getLocation(), new BuildConfiguration(), true);

            runner.addProjects(build.getProjects());
        }

        return runner;
    }
}
