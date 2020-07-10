package tech.ikora.evolution.results;

import tech.ikora.model.Projects;
import tech.ikora.model.TestCase;
import tech.ikora.model.UserKeyword;
import tech.ikora.model.VariableAssignment;

import java.time.Instant;
import java.util.Date;

public class VersionRecord implements Record {
    private final Date date;
    private final int projects;
    private final int testCases;
    private final int userKeywords;
    private final int variables;

    public VersionRecord(Projects version){
        this.date = version.getDate() == null ? Date.from(Instant.now()) : version.getDate();
        this.projects = version.size();
        this.testCases = version.getNodes(TestCase.class).size();
        this.userKeywords = version.getNodes(UserKeyword.class).size();
        this.variables = version.getNodes(VariableAssignment.class).size();
    }

    public Date getDate() {
        return date;
    }

    public int getProjects() {
        return projects;
    }

    public int getTestCases() {
        return testCases;
    }

    public int getUserKeywords() {
        return userKeywords;
    }

    public int getVariables() {
        return variables;
    }

    @Override
    public String[] getKeys() {
        return new String[]{
                "date",
                "number_projects",
                "number_test_cases",
                "number_keywords",
                "number_variables"
        };
    }

    @Override
    public Object[] getValues() {
        return new Object[]{
                this.getDate().toString(),
                this.getProjects(),
                this.getTestCases(),
                this.getUserKeywords(),
                this.getVariables()
        };
    }
}
