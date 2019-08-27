package com.deavensoft.jiraworklogs.domain;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Main Domain Manager.
 */
@RequiredArgsConstructor
public class WorkLogManager implements WorkLogManagerPort {

    private final WorkLogServicePort workLogService;

    /**
     * Finds all the Works Logs for the specified user, in the given time period.
     *
     * @param userEmail Identifier of the user who performed the work.
     * @param startDate Period start date in which work happened (inclusive).
     * @param endDate Period end date in which work happened (inclusive).
     *
     * @return Collection of found {@link WorkLog} items.
     */
    @Override
    public Collection<WorkLog> findWorkLogsForUserInPeriod(String userEmail, LocalDate startDate, LocalDate endDate) {
        return workLogService.findWorkLogsForUserInPeriod(userEmail, startDate, endDate);
    }
}
