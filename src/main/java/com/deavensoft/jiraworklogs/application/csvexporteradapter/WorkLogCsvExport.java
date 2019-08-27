package com.deavensoft.jiraworklogs.application.csvexporteradapter;

import java.io.File;
import java.time.LocalDate;

public interface WorkLogCsvExport {

    /**
     * Exports Work log entries into a CSV file format, based on the data
     * provided in the {@link com.deavensoft.jiraworklogs.domain.WorkLog}.
     *
     * @param userEmail Identifier of the user who performed the work.
     * @param startDate Period start date in which work happened (inclusive).
     * @param endDate Period end date in which work happened (inclusive).
     *
     * @return CSV File.
     */
    File exportWorkLogsForUserInPeriod(String userEmail, LocalDate startDate, LocalDate endDate);
}
