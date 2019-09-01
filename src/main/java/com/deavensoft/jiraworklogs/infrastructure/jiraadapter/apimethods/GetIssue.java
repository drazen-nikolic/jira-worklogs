package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods;

import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.client.JiraRestClient;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model.JiraIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

/**
 * Gets the required Issue data
 */
@RequiredArgsConstructor
public class GetIssue {
    private static final String FIELDS = "summary,issuetype,priority,worklog"; // needed fields to fetch

    private final JiraRestClient jiraRestClient;

    public JiraIssue get(String issueKey) {
        return jiraRestClient.exchange("/issue/" + issueKey + "?fields=" + FIELDS,
                HttpMethod.GET, JiraIssue.class);
    }
}
