package tech.ikora.evolution;

import org.apache.commons.collections4.iterators.ReverseListIterator;
import tech.ikora.evolution.versions.Frequency;
import tech.ikora.gitloader.git.GitCommit;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    public static List<GitCommit> removeIgnoredCommit(List<GitCommit> commits, Set<String> ignoreCommits) {
        if(ignoreCommits == null){
            return commits;
        }

        return commits.stream()
                .filter(commit -> ignoreCommits.contains(commit.getId()))
                .collect(Collectors.toList());
    }

    public static List<GitCommit> filterCommitsByFrequency(List<GitCommit> commits, Frequency frequency) {
        if(frequency == Frequency.UNIQUE){
            return commits;
        }

        List<GitCommit> filtered = new ArrayList<>(commits.size());
        ReverseListIterator<GitCommit> iterator = new ReverseListIterator<>(commits);

        Date previousDate = null;
        while (iterator.hasNext()){
            GitCommit commit = iterator.next();
            Date commitDate = commit.getDate();

            if(!Utils.isSameFrequencyBucket(previousDate, commitDate, frequency)){
                filtered.add(commit);
            }

            previousDate = commitDate;
        }

        Collections.reverse(filtered);

        return filtered;
    }

    public static List<GitCommit> truncateCommits(List<GitCommit> commits, int maximumCommitsNumber) {
        if(maximumCommitsNumber <= 0){
            return commits;
        }

        return commits.subList(0, Math.min(commits.size(), maximumCommitsNumber));
    }

    public static boolean isSameFrequencyBucket(Date date1, Date date2, Frequency frequency) {
        if(date1 == null || date2 == null){
            return false;
        }

        switch (frequency) {
            case DAILY: return isSameDay(date1, date2);
            case WEEKLY: return isSameWeek(date1, date2);
            case MONTHLY: return isSameMonth(date1, date2);
            case YEARLY: return isSameYear(date1, date2);
        }

        return false;
    }

    public static boolean isSameDay(Date date1, Date date2){
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date1);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        int year1 = calendar.get(Calendar.YEAR);

        calendar.setTime(date2);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);
        int year2 = calendar.get(Calendar.YEAR);

        return day1 == day2 && year1 == year2;
    }

    public static boolean isSameWeek(Date date1, Date date2){
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date1);
        int week1 = calendar.get(Calendar.WEEK_OF_YEAR);
        int year1 = calendar.get(Calendar.YEAR);

        calendar.setTime(date2);
        int week2 = calendar.get(Calendar.WEEK_OF_YEAR);
        int year2 = calendar.get(Calendar.YEAR);

        return week1 == week2 && year1 == year2;
    }

    public static boolean isSameMonth(Date date1, Date date2){
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date1);
        int month1 = calendar.get(Calendar.MONTH);
        int year1 = calendar.get(Calendar.YEAR);

        calendar.setTime(date2);
        int month2 = calendar.get(Calendar.MONTH);
        int year2 = calendar.get(Calendar.YEAR);

        return month1 == month2 && year1 == year2;
    }

    public static boolean isSameYear(Date date1, Date date2){
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);

        calendar.setTime(date2);
        int year2 = calendar.get(Calendar.YEAR);

        return year1 == year2;
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
}
