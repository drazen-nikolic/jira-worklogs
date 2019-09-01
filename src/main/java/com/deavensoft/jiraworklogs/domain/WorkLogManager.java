package com.deavensoft.jiraworklogs.domain;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Main Domain Manager.
 */
@RequiredArgsConstructor
public class WorkLogManager implements WorkLogManagerPort {

    private final WorkLogServicePort workLogService;

    @Override
    public Collection<WorkLog> findWorkLogsForUserInPeriod(Predicate<WorkLog> userWorkLogFilter, String dayOfWeekRegex,
                                                           LocalDate startDate, LocalDate endDate) {
        return workLogService.findWorkLogsForUserInPeriod(userWorkLogFilter, dayOfWeekRegex, startDate, endDate);
    }
}
