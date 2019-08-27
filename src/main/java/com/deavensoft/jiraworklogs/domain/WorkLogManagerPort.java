package com.deavensoft.jiraworklogs.domain;

import java.time.LocalDate;
import java.util.Collection;

public interface WorkLogManagerPort {

    /**
     * Finds all {@link WorkLog}s for a specified user in the give period.
     *
     * @param userEmail Identifier of the user who performed the work.
     * @param startDate Period start date in which work happened (inclusive).
     * @param endDate Period end date in which work happened (inclusive).
     *
     * @return Collection of work log entries.
     */
    Collection<WorkLog> findWorkLogsForUserInPeriod(String userEmail, LocalDate startDate, LocalDate endDate);
}
