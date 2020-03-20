package tech.ikora.evolution.versions;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.BuildConfiguration;
import tech.ikora.builder.BuildResult;
import tech.ikora.builder.Builder;
import tech.ikora.evolution.Utils;
import tech.ikora.gitloader.git.GitCommit;
import tech.ikora.gitloader.git.GitUtils;
import tech.ikora.gitloader.git.LocalRepository;
import tech.ikora.model.Projects;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GitProvider implements VersionProvider {
    private static final Logger logger = LogManager.getLogger(GitProvider.class);

    private File rootFolder;

    private final Frequency frequency;
    private final Map<LocalRepository, List<GitCommit>> repositories;

    public GitProvider(Frequency frequency) {
        this.frequency = frequency;
        this.repositories = new HashMap<>();
        this.rootFolder = null;
    }

    public void addRepository(LocalRepository localRepository, List<GitCommit> commits) {
        this.repositories.put(localRepository, commits);
    }

    @Override
    public File getRootFolder() throws IOException {
        if(this.rootFolder == null){
            this.rootFolder = new File(System.getProperty("java.io.tmpdir"), "git-provider");

            if(this.rootFolder.exists()){
                FileUtils.deleteDirectory(this.rootFolder);
            }

            if(!this.rootFolder.mkdir()){
                throw new IOException(String.format("Failed to create directory: %s",
                        this.rootFolder.getAbsolutePath()));
            }
        }

        return this.rootFolder;
    }

    @Override
    public void clean() throws IOException {
        for(LocalRepository localRepository: repositories.keySet()){
            localRepository.getGit().getRepository().close();
        }

        FileUtils.forceDelete(this.rootFolder);
    }

    @Override
    public Iterator<Projects> iterator() {
        return new Iterator<Projects>() {
            private final List<Date> dates = getDates();
            private final Iterator<Date> dateIterator = dates.iterator();

            @Override
            public boolean hasNext() {
                return dateIterator.hasNext();
            }

            @Override
            public Projects next() {
                Projects projects = new Projects();

                try {
                    for(Map.Entry<LocalRepository, GitCommit> entry: getLastCommits(dateIterator.next()).entrySet()){
                        GitUtils.checkout(entry.getKey().getGit(), entry.getValue().getId());

                        final BuildResult build = Builder.build(entry.getKey().getLocation(), new BuildConfiguration(), true);
                        projects.addProjects(build.getProjects());
                    }
                } catch (GitAPIException e) {
                    logger.error(String.format("Git API error (this iteration will be ignored): %s", e.getMessage()));
                    projects = next();
                }

                return projects;
            }

            private Map<LocalRepository, GitCommit> getLastCommits(Date date){
                Map<LocalRepository, GitCommit> lastCommits = new HashMap<>(repositories.size());

                for(Map.Entry<LocalRepository, List<GitCommit>> entry: repositories.entrySet()){
                    GitCommit commit = Utils.lastCommitBeforeDate(entry.getValue(), date);
                    lastCommits.put(entry.getKey(), commit);
                }

                return lastCommits;
            }

            private List<Date> getDates(){
                List<GitCommit> allCommits = new ArrayList<>();

                for(List<GitCommit> commits: repositories.values()){
                    allCommits.addAll(commits);
                }

                allCommits = allCommits.stream().sorted(Comparator.comparing(GitCommit::getDate)).collect(Collectors.toList());
                allCommits = Utils.filterCommitsByFrequency(allCommits, frequency);

                return allCommits.stream()
                        .map(GitCommit::getDate)
                        .filter(this::isDateValid)
                        .collect(Collectors.toList());
            }

            private boolean isDateValid(Date date){
                for(Map.Entry<LocalRepository, List<GitCommit>> entry: repositories.entrySet()){
                    GitCommit commit = Utils.lastCommitBeforeDate(entry.getValue(), date);
                    if(commit == null){
                        return false;
                    }
                }

                return true;
            }
        };
    }
}
