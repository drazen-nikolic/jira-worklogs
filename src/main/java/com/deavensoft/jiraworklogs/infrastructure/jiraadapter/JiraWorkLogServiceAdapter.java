package com.deavensoft.jiraworklogs.infrastructure.jiraadapter;

import com.deavensoft.jiraworklogs.domain.Issue;
import com.deavensoft.jiraworklogs.domain.WorkLog;
import com.deavensoft.jiraworklogs.domain.WorkLogServicePort;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods.FilterIssuesInPeriodWithWorkLogs;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods.GetIssue;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model.JiraIssue;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * JIRA Work logs adapter.
 */
@RequiredArgsConstructor
public class JiraWorkLogServiceAdapter implements WorkLogServicePort {

    private final FilterIssuesInPeriodWithWorkLogs filterIssuesInPeriodWithWorkLogs;
    private final GetIssue getIssue;

    @Override
    public Collection<WorkLog> findWorkLogsForUserInPeriod(Predicate<WorkLog> userWorkLogFilter, String dayOfWeekRegex,
                                                           LocalDate startDate, LocalDate endDate) {
        Collection<String> issueKeys = filterIssuesInPeriodWithWorkLogs.filter(startDate, endDate);
        return issueKeys.stream()
                .map(getIssue::get)
                .flatMap(jiraIssue -> convertToWorkLogs(jiraIssue).stream())
                .filter(userWorkLogFilter)
                .filter(workLog -> isInPeriod(workLog, startDate, endDate))
                .filter(worklog -> filterByDayOfWeek(worklog.getDate(), dayOfWeekRegex))
                .sorted(Comparator.comparing(WorkLog::getDate))
                .collect(Collectors.toList());
    }

    private Collection<WorkLog> convertToWorkLogs(JiraIssue jiraIssue) {
        return jiraIssue.getFields().getWorklog().getWorklogs().stream()
                .map(jiraWorkLogEntry -> WorkLog.builder()
                        .userDisplayName(jiraWorkLogEntry.getAuthor().getDisplayName())
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

    private boolean isInPeriod(WorkLog workLog, LocalDate startDate, LocalDate endDate) {
        return (workLog.getDate().isEqual(startDate) || workLog.getDate().isAfter(startDate))
                && (workLog.getDate().isEqual(endDate) || workLog.getDate().isBefore(endDate));
    }

    private boolean filterByDayOfWeek(LocalDate date, String dayOfWeekRegex) {
        if (StringUtils.isNotBlank(dayOfWeekRegex)) {
            return formatDayOfWeek(date).matches(dayOfWeekRegex);
        } else {
            return true;
        }
    }

    private String formatDayOfWeek(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("E"));
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
