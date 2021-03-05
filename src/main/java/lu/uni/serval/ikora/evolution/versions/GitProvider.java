package lu.uni.serval.ikora.evolution.versions;

import lu.uni.serval.commons.git.utils.Frequency;
import lu.uni.serval.commons.git.utils.GitCommit;
import lu.uni.serval.commons.git.utils.GitUtils;
import lu.uni.serval.commons.git.utils.LocalRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lu.uni.serval.ikora.core.BuildConfiguration;
import lu.uni.serval.ikora.core.builder.BuildResult;
import lu.uni.serval.ikora.core.builder.Builder;
import lu.uni.serval.ikora.core.model.Projects;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class GitProvider implements VersionProvider {
    private static final Logger logger = LogManager.getLogger(GitProvider.class);

    private File rootFolder;

    private final Frequency frequency;
    private final Map<LocalRepository, List<GitCommit>> repositories;
    private final Map<LocalRepository, Set<String>> projectFolders;

    public GitProvider(Frequency frequency) {
        this.frequency = frequency;
        this.repositories = new HashMap<>();
        this.projectFolders = new HashMap<>();
        this.rootFolder = null;
    }

    public void addRepository(LocalRepository localRepository, List<GitCommit> commits, Set<String> projectFolders) {
        this.repositories.put(localRepository, commits);
        this.projectFolders.put(localRepository, projectFolders);
    }

    private Set<File> getProjectFolders(LocalRepository localRepository){
        Set<String> projectFolderNames = this.projectFolders.get(localRepository);

        if(projectFolderNames == null || projectFolderNames.isEmpty()){
            return Collections.singleton(localRepository.getLocation());
        }

        Set<File> projectFolders = new HashSet<>(projectFolderNames.size());

        File repositoryFolder = localRepository.getLocation();
        for(String projectFolderName: projectFolderNames){
            File projectFolder = new File(repositoryFolder, projectFolderName);

            if(projectFolder.exists()){
                projectFolders.add(projectFolder);
            }
            else {
                logger.warn(String.format("Folder %s does not exists in repository %s on %s",
                        projectFolderName,
                        localRepository.getRemoteUrl(),
                        localRepository.getGitCommit().getDate().toString()
                ));
            }
        }

        return projectFolders;
    }

    @Override
    public File getRootFolder() throws IOException {
        if(this.rootFolder == null){
            this.rootFolder = new File(System.getProperty("java.io.tmpdir"), "git-provider");

            if(!this.rootFolder.exists() && !this.rootFolder.mkdir()){
                throw new IOException(String.format("Failed to create directory: %s", this.rootFolder.getAbsolutePath()));
            }
        }

        return this.rootFolder;
    }

    @Override
    public void close() throws IOException {
        for(LocalRepository localRepository: repositories.keySet()){
            GitUtils.close(localRepository.getGit(), true);
        }
    }

    @Override
    public Iterator<Projects> iterator() {
        return new Iterator<Projects>() {
            private final List<Instant> dates = getDates();
            private final Iterator<Instant> dateIterator = dates.iterator();

            @Override
            public boolean hasNext() {
                return dateIterator.hasNext();
            }

            @Override
            public Projects next() {
                Projects projects = new Projects();

                try {
                    final Instant date = dateIterator.next();

                    for(Map.Entry<LocalRepository, GitCommit> entry: getLastCommits(date).entrySet()){
                        GitUtils.checkout(entry.getKey().getGit(), entry.getValue().getId());
                        final BuildResult build = Builder.build(getProjectFolders(entry.getKey()), new BuildConfiguration(), true);
                        projects.addProjects(build.getProjects());
                        projects.setDate(date);
                    }
                } catch (GitAPIException | IOException e) {
                    logger.error(String.format("Git API error (this iteration will be ignored): %s", e.getMessage()));
                    projects = next();
                }

                return projects;
            }

            private Map<LocalRepository, GitCommit> getLastCommits(Instant date){
                Map<LocalRepository, GitCommit> lastCommits = new HashMap<>(repositories.size());

                for(Map.Entry<LocalRepository, List<GitCommit>> entry: repositories.entrySet()){
                    GitCommit commit = lastCommitBeforeDate(entry.getValue(), date);
                    lastCommits.put(entry.getKey(), commit);
                }

                return lastCommits;
            }

            private List<Instant> getDates(){
                List<GitCommit> allCommits = new ArrayList<>();

                for(List<GitCommit> commits: repositories.values()){
                    allCommits.addAll(commits);
                }

                allCommits = allCommits.stream().sorted(Comparator.comparing(GitCommit::getDate)).collect(Collectors.toList());
                allCommits = GitUtils.filterCommitsByFrequency(allCommits, frequency);

                return allCommits.stream()
                        .map(GitCommit::getDate)
                        .collect(Collectors.toList());
            }

            private GitCommit lastCommitBeforeDate(List<GitCommit> commits, Instant date){
                GitCommit commit = GitCommit.none();

                for(GitCommit current: commits){
                    if(current.getDate().isAfter(date)){
                        break;
                    }

                    if(commit == GitCommit.none()){
                        commit = current;
                        continue;
                    }

                    if(current.getDate().isAfter(commit.getDate())){
                        commit = current;
                    }
                }

                return commit;
            }
        };
    }
}
