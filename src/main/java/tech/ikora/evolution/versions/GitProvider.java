package tech.ikora.evolution.versions;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.BuildConfiguration;
import tech.ikora.builder.BuildResult;
import tech.ikora.builder.Builder;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.GitUtils;
import tech.ikora.gitloader.git.LocalRepository;
import tech.ikora.model.Projects;

import java.util.Iterator;
import java.util.List;

public class GitProvider implements VersionProvider {
    private static final Logger logger = LogManager.getLogger(GitProvider.class);

    private final LocalRepository localRepository;
    private final List<GitCommit> commits;

    public GitProvider(LocalRepository localRepository, List<GitCommit> commits) {
        this.localRepository = localRepository;
        this.commits = commits;
    }

    @Override
    public Iterator<Projects> iterator() {
        return new Iterator<Projects>() {
            private Iterator<GitCommit> commitIterator = commits.iterator();

            @Override
            public boolean hasNext() {
                return commitIterator.hasNext();
            }

            @Override
            public Projects next() {
                Projects projects;

                try {
                    GitCommit commit = commitIterator.next();
                    GitUtils.checkout(localRepository.getGit(), commit.getId());

                    final BuildResult build = Builder.build(localRepository.getLocation(), new BuildConfiguration(), true);
                    projects = build.getProjects();

                } catch (GitAPIException e) {
                    logger.error(String.format("Git API error: %s", e.getMessage()));
                    projects = new Projects();
                }

                return projects;
            }
        };
    }
}
