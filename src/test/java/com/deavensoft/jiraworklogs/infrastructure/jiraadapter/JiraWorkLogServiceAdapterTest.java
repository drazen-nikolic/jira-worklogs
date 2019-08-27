package com.deavensoft.jiraworklogs.infrastructure.jiraadapter;

import com.deavensoft.jiraworklogs.domain.WorkLog;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods.FilterIssuesInPeriodWithWorkLogs;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods.GetIssue;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model.JiraIssue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JiraWorkLogServiceAdapterTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private JiraWorkLogServiceAdapter workLogServiceAdapter;

    @Mock
    private FilterIssuesInPeriodWithWorkLogs filterIssuesInPeriodWithWorkLogs;

    @Mock
    private GetIssue getIssue;

    private JiraIssue loadIssueFromFile(String issueKey) throws IOException {
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream("/" + issueKey + ".json")) {
           return objectMapper.readValue(resourceAsStream, JiraIssue.class);
        }
    }

    @Before
    public void setUp() throws IOException {
        workLogServiceAdapter = new JiraWorkLogServiceAdapter(filterIssuesInPeriodWithWorkLogs, getIssue);
        when(filterIssuesInPeriodWithWorkLogs.filter(any(LocalDate.class), any(LocalDate.class))).thenReturn(
                Arrays.asList("JIRA-1", "JIRA-2", "JIRA-3")
        );

        when(getIssue.get("JIRA-1")).thenReturn(loadIssueFromFile("JIRA-1"));
        when(getIssue.get("JIRA-2")).thenReturn(loadIssueFromFile("JIRA-2"));
        when(getIssue.get("JIRA-3")).thenReturn(loadIssueFromFile("JIRA-3"));
    }

    @Test
    public void findWorkLogsForUserInPeriod_shouldFilterUserWorklogsOnly() {
        // when
        String userEmail = "john.doe@jira.com";
        Collection<WorkLog> workLogs = workLogServiceAdapter
                .findWorkLogsForUserInPeriod(userEmail, LocalDate.MIN, LocalDate.MAX);

        // then
        assertThat(workLogs, hasSize(3));
        assertThat(workLogs.stream()
                .map(WorkLog::getUserEmail)
                .distinct()
                .collect(Collectors.toList()), contains(userEmail));
    }

    @Test
    public void findWorkLogsForUserInPeriod_shouldReturnItemsOrdered() {
        // when
        String userEmail = "john.doe@jira.com";
        Collection<WorkLog> workLogs = workLogServiceAdapter
                .findWorkLogsForUserInPeriod(userEmail, LocalDate.MIN, LocalDate.MAX);

        // then
        assertThat(workLogs.stream()
                .map(WorkLog::getDate)
                .collect(Collectors.toList()),
                contains(LocalDate.of(2019, 7 , 15),
                        LocalDate.of(2019, 7 , 16),
                        LocalDate.of(2019, 8 , 5))
        );
    }

    @Test
    public void findWorkLogsForUserInPeriod_shouldFilterUserWorklogsInTimePeriod() {
        // when
        String userEmail = "john.doe@jira.com";
        Collection<WorkLog> workLogs = workLogServiceAdapter
                .findWorkLogsForUserInPeriod(userEmail,
                        LocalDate.of(2019, 7, 1),
                        LocalDate.of(2019, 7, 31));

        // then
        assertThat(workLogs, hasSize(2));
        assertThat(workLogs.stream()
                .map(WorkLog::getDescription)
                .collect(Collectors.toList()), contains("My first worklog!", "My 2nd worklog!"));
    }
}