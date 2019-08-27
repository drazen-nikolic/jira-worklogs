package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods;

import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.client.JiraRestClient;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model.JiraIssue;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model.SearchIssuesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Filters issues having work logs in the given period.
 */
@RequiredArgsConstructor
public class FilterIssuesInPeriodWithWorkLogs {
    private static final String FIELDS = "key"; // needed fields to fetch
    private static final String JQL_SEARCH_PATTERN = "created <= %1$s AND updated >= %2$s AND timespent > 0";

    private final JiraRestClient jiraRestClient;

    /**
     * Filters issues having work logs in the given period.
     *
     * @param startDate Period start date.
     * @param endDate Period end date.
     *
     * @return Collection of JIRA Issue keys.
     */
    public Collection<String> filter(LocalDate startDate, LocalDate endDate) {
        String jqlString = String.format(JQL_SEARCH_PATTERN,
                endDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        SearchIssuesResponse response = jiraRestClient.exchange("/search?startIndex=0&maxResults=1000&fields=" + FIELDS
                        + "&jql=" + jqlString,
                HttpMethod.GET, SearchIssuesResponse.class);

        return response.getIssues().stream()
                .map(JiraIssue::getKey)
                .collect(Collectors.toList());
    }
}
