package com.deavensoft.jiraworklogs.infrastructure.jiraadapter;

import com.deavensoft.jiraworklogs.domain.Issue;
import com.deavensoft.jiraworklogs.domain.WorkLog;
import com.deavensoft.jiraworklogs.domain.WorkLogServicePort;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods.FilterIssuesInPeriodWithWorkLogs;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods.GetIssue;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model.JiraIssue;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JIRA Work logs adapter.
 */
@RequiredArgsConstructor
public class JiraWorkLogServiceAdapter implements WorkLogServicePort {

    private final FilterIssuesInPeriodWithWorkLogs filterIssuesInPeriodWithWorkLogs;
    private final GetIssue getIssue;

    /**
     * Finds changed issues in the given period, having logger work,
     * extracts work log items from them, for the given user and time period.
     *
     * @param userEmail Identifier of the user who performed the work.
     * @param startDate Period start date in which work happened (inclusive).
     * @param endDate Period end date in which work happened (inclusive).
     *
     * @return Ordered collection of Work Logs (ordered by work log date).
     */
    @Override
    public Collection<WorkLog> findWorkLogsForUserInPeriod(String userEmail, LocalDate startDate, LocalDate endDate) {
        Collection<String> issueKeys = filterIssuesInPeriodWithWorkLogs.filter(startDate, endDate);
        return issueKeys.stream()
                .map(getIssue::get)
                .flatMap(jiraIssue -> convertToWorkLogs(jiraIssue).stream())
                .filter(workLog -> isUserWorkLog(workLog, userEmail))
                .filter(workLog -> isInPeriod(workLog, startDate, endDate))
                .sorted(Comparator.comparing(WorkLog::getDate))
                .collect(Collectors.toList());
    }

    private Collection<WorkLog> convertToWorkLogs(JiraIssue jiraIssue) {
        return jiraIssue.getFields().getWorklog().getWorklogs().stream()
                .map(jiraWorkLogEntry -> WorkLog.builder()
                        .userDisplayName(jiraWorkLogEntry.getAuthor().getDisplayName())
                        .userEmail(jiraWorkLogEntry.getAuthor().getEmailAddress())
                        .logHours(convertToHours(jiraWorkLogEntry.getTimeSpentSeconds()))
                        .description(jiraWorkLogEntry.getComment())
                        .date(convertToLocalDate(jiraWorkLogEntry.getStarted()))
                        .details(Issue.builder()
                                .key(jiraIssue.getKey())
                                .type(jiraIssue.getFields().getIssuetype().getName())
                                .priority(jiraIssue.getFields().getPriority().getName())
                                .summary(jiraIssue.getFields().getSummary())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    private boolean isUserWorkLog(WorkLog workLog, String userEmail) {
        return workLog.getUserEmail().equals(userEmail);
    }

    private boolean isInPeriod(WorkLog workLog, LocalDate startDate, LocalDate endDate) {
        return (workLog.getDate().equals(startDate) || workLog.getDate().isAfter(startDate))
                && (workLog.getDate().equals(endDate) || workLog.getDate().isBefore(endDate));
    }

    private Float convertToHours(Integer timeSpentSeconds) {
        return timeSpentSeconds / 3600f;
    }

    private LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
