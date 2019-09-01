package com.deavensoft.jiraworklogs.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Predicate;

public interface WorkLogServicePort {
    /**
     * Finds all {@link WorkLog}s for a specified user in the give period.
     *
     * @param userWorkLogFilter Function for filtering out user worklogs.
     * @param dayOfWeekRegex Optional filter, weekday filer regex (e.g. Mon|Tue|Wed).
     * @param startDate Period start date in which work happened (inclusive).
     * @param endDate Period end date in which work happened (inclusive).
     *
     * @return Collection of work log entries.
     */
    Collection<WorkLog> findWorkLogsForUserInPeriod(Predicate<WorkLog> userWorkLogFilter, String dayOfWeekRegex,
                                                    LocalDate startDate, LocalDate endDate);
}
