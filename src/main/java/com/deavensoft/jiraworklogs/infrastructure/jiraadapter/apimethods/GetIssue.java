package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods;

import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.client.JiraRestClient;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model.JiraIssue;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model.JiraWorkLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

/**
 * Gets the required Issue data
 */
@RequiredArgsConstructor
public class GetIssue {
    private static final String FIELDS = "summary,issuetype,priority"; // needed fields to fetch

    private final JiraRestClient jiraRestClient;

    public JiraIssue get(String issueKey) {
        JiraIssue jiraIssue = getIssueDetails(issueKey);
        jiraIssue.getFields().setWorklog(getIssueWorkLogs(issueKey));
        return jiraIssue;
    }

    private JiraIssue getIssueDetails(String issueKey) {
        return jiraRestClient.exchange("/issue/" + issueKey + "?fields=" + FIELDS,
                HttpMethod.GET, JiraIssue.class);
    }

    private JiraWorkLog getIssueWorkLogs(String issueKey) {
        return jiraRestClient.exchange("/issue/" + issueKey + "/worklog",
                HttpMethod.GET, JiraWorkLog.class);
    }

}
