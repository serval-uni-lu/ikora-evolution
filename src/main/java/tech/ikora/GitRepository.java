package tech.ikora;

import tech.ikora.builder.Builder;
import tech.ikora.model.Project;
import tech.ikora.model.SourceFile;
import tech.ikora.model.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class GitRepository {
    private Git git;
    private String url;
    private String branch;
    private String username;
    private String password;
    private File localFolder;
    private String name;

    private Project project;

    public GitRepository(String url, String branch, String username, String password)  {
        this.url = url;
        this.branch = branch;
        this.username = username;
        this.password = password;

        this.name = FilenameUtils.getBaseName(url);

        File cache = Configuration.getInstance().getConfigurationFolder();
        this.localFolder = new File(cache, "git");

        if(!this.localFolder.exists() && !this.localFolder.mkdir()){
            this.localFolder = null;
        }
        else {
            this.localFolder = new File(this.localFolder.getAbsolutePath(), name);
        }

    }

    public String getName() {
        return name;
    }

    public Project getProject() {
        return project;
    }

    public Set<TestCase> findTestCase(String name) {
        SourceFile sourceFile = project.getSourceFile(name);

        if(sourceFile == null){
            return null;
        }

        return sourceFile.getTestCase(name);
    }

    public void checkout(Date date, boolean link) {
        GitCommit commit = getMostRecentCommit(date);
        checkout(commit.getId(), link);
    }

    public void checkout(String commitId, boolean link){
        try {
            if(git == null){
                cloneRepository();
            }

            Ref ref = git.checkout()
                    .setCreateBranch(true)
                    .setName(commitId)
                    .setStartPoint(commitId)
                    .call();

            Builder builder = new Builder();
            project = builder.build(localFolder, link);

            project.setGitUrl(url);
            project.setCommitId(commitId);
            project.setDate(getCommitDate(ref.getObjectId()));

            git.checkout().setName(branch).call();
            git.branchDelete().setBranchNames(commitId).call();

        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    private void cloneRepository() throws GitAPIException {
        if(localFolder.exists()){
            try {
                FileUtils.deleteDirectory(localFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        git = Git.cloneRepository()
                .setURI(url)
                .setCredentialsProvider( new UsernamePasswordCredentialsProvider(username, password))
                .setBranch(branch)
                .setDirectory(localFolder)
                .call();
    }

    public GitCommit getMostRecentCommit(Date date){
        GitCommit mostRecentCommit = null;

        try {
            if(git == null){
                cloneRepository();
            }

            List<GitCommit> commits =  getRevisions();

            for (GitCommit commit: commits) {
                if(commit.getDate().after(date)){
                    break;
                }

                mostRecentCommit = commit;
            }
        }
        catch (GitAPIException e){
            mostRecentCommit = null;
        }

        return mostRecentCommit;
    }

    public List<GitCommit> getRevisions() {
        List<GitCommit> commits = new ArrayList<>();

        try {
            if(git == null){
                cloneRepository();
            }

            Iterable<RevCommit> revCommits;
            ObjectId masterId = git.getRepository().resolve("remotes/origin/master");

            if(branch.equals("master") || masterId == null){
                revCommits = git.log().call();
            }
            else{
                ObjectId branchId = git.getRepository().resolve("remotes/origin/" + branch);

                revCommits = git.log().addRange(masterId, branchId).call();
            }

            Plugin analytics = Configuration.getInstance().getPlugin("project analytics");
            List<String> ignoreList = (List<String>)analytics.getAdditionalProperty("ignore releases", new ArrayList<>());

            Date startDate = analytics.getPropertyAsDate("start date");
            Date endDate = analytics.getPropertyAsDate("end date");

            Set<String> ignoreSet = new HashSet<>(ignoreList);

            for (RevCommit revCommit : revCommits) {
                if(ignoreSet.contains(revCommit.getName())){
                    continue;
                }

                Instant instant = Instant.ofEpochSecond(revCommit.getCommitTime());
                Date commitDate = Date.from(instant);

                if(isInInterval(commitDate, startDate, endDate)){
                    commits.add(new GitCommit(revCommit.getName(), commitDate));
                }
            }

            commits.sort(Comparator.comparing(GitCommit::getDate));

            int releaseNb = (int) analytics.getAdditionalProperty("number of releases", 0);

            if(releaseNb >= 2){
                commits = commits.subList(Math.max(0, commits.size() - releaseNb), commits.size());
            }

            return commits;
        }
        catch (GitAPIException | IOException e) {
            return new ArrayList<>();
        }
    }

    private Date getCommitDate(ObjectId commitId) throws GitAPIException, IOException {
        if(git == null){
            cloneRepository();
        }

        RevWalk revWalk = new RevWalk(git.getRepository());
        RevCommit revCommit = revWalk.parseCommit(commitId);

        return revCommit.getAuthorIdent().getWhen();
    }

    private boolean isInInterval(Date commitDate, Date startDate, Date endDate){
        if(startDate != null && startDate.after(commitDate)){
            return false;
        }

        if(endDate != null && endDate.before(commitDate)){
            return false;
        }

        return true;
    }
}
