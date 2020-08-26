package tech.ikora.evolution;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jgit.diff.DiffEntry;
import tech.ikora.gitloader.git.GitCommit;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);

    public static List<GitCommit> removeCommitsWithNoFileChanged(List<GitCommit> commits, Set<String> projectFolders) {
        if(projectFolders == null || projectFolders.isEmpty()){
            return commits;
        }

        return commits.stream().filter(c -> isSubfolderChanged(c, projectFolders)).collect(Collectors.toList());
    }



    public static GitCommit lastCommitBeforeDate(List<GitCommit> commits, Date date){
        GitCommit commit = null;

        for(GitCommit current: commits){
            if(current.getDate().after(date)){
                break;
            }

            if(commit == null){
                commit = current;
                continue;
            }

            if(current.getDate().after(commit.getDate())){
                commit = current;
            }
        }

        return commit;
    }

    private static boolean isSubfolderChanged(GitCommit commit, Set<String> subFolders) {
        for(DiffEntry diffEntry: commit.getDiffEntries()){
            for(String subFolder: subFolders){
                try {
                    if(FilenameUtils.directoryContains(subFolder, diffEntry.getOldPath())){
                        return true;
                    }

                    if(FilenameUtils.directoryContains(subFolder, diffEntry.getNewPath())){
                        return true;
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }

        }

        return false;
    }
}
