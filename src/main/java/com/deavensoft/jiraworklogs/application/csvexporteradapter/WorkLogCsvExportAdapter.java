package com.deavensoft.jiraworklogs.application.csvexporteradapter;

import com.deavensoft.jiraworklogs.domain.WorkLog;
import com.deavensoft.jiraworklogs.domain.WorkLogManagerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class WorkLogCsvExportAdapter implements WorkLogCsvExport {
    private static final String NEW_LINE = "\r\n";
    private static final String DELIMITER = ",";
    private static final String UNDERSCORE = "_";
    private static final String PIPE = "|";
    static final String HEADER_LINE = "Work Date,DOW,Hours,User,Issue Key,Issue Summary,Issue Type,Issue Priority,Work Description";
    private final WorkLogManagerPort workLogManager;

    @Override
    public File exportWorkLogsForUserInPeriod(String userDisplayName, String dayOfWeekList,
                                              LocalDate startDate, LocalDate endDate) {
        log.info("Exporting Work Logs for {}, in the period: {} - {}", userDisplayName, startDate, endDate);
        Collection<WorkLog> workLogsForUserInPeriod = workLogManager.findWorkLogsForUserInPeriod(
                workLog -> userDisplayName.equals(workLog.getUserDisplayName()), convertToRegex(dayOfWeekList), startDate, endDate);
        if (workLogsForUserInPeriod.isEmpty()) {
            return null;
        } else {
            try {
                String fileNamePrefix = String.format("worklogs_%1$s_%2$s_%3$s_",
                        userDisplayName.replaceAll(StringUtils.SPACE, UNDERSCORE),
                        formatDate(startDate),
                        formatDate(endDate));
                return exportToFile(workLogsForUserInPeriod, fileNamePrefix);
            } catch (IOException e) {
                throw new RuntimeException("Exception working with file!", e);
            }
        }
    }

    private File exportToFile(Collection<WorkLog> workLogs, String fileNamePrefix) throws IOException {
        File csvFile = Files.createTempFile(fileNamePrefix, ".csv").toFile();

        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8)) {
            writeLine(writer, HEADER_LINE);

            workLogs.stream()
                    .map(this::exportWorkLogLine)
                    .forEach(line -> writeLine(writer, line));
        }
        return csvFile;
    }

    private void writeLine(OutputStreamWriter writer, String line) {
        try {
            writer.write(line);
            writer.write(NEW_LINE);
        } catch (IOException e) {
            throw new RuntimeException("Problem writing to a file!", e);
        }
    }

    private String exportWorkLogLine(WorkLog workLog) {
        return appendStringsForCsv(formatDate(workLog.getDate()),
                formatDayOfWeek(workLog.getDate()),
                formatFloat(workLog.getLogHours()),
                workLog.getUserDisplayName(),
                workLog.getDetails().getKey(),
                workLog.getDetails().getSummary(),
                workLog.getDetails().getType(),
                workLog.getDetails().getPriority(),
                workLog.getDescription()
        );
    }

    private String appendStringsForCsv(String... values) {
        StringBuilder sb = new StringBuilder();
        for (String string : values) {
            if (sb.length() > 0) sb.append(DELIMITER);
            sb.append(StringEscapeUtils.escapeCsv(string));
        }
        return sb.toString();
    }

    String formatFloat(Float number) {
        return String.format("%.2f", number);
    }

    String formatDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    String formatDayOfWeek(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("E"));
    }

    String convertToRegex(String dayOfWeekList) {
        if (StringUtils.isNotBlank(dayOfWeekList)) {
            return dayOfWeekList.replaceAll(StringUtils.SPACE, StringUtils.EMPTY)
                    .replaceAll(DELIMITER, PIPE);
        } else {
            return null;
        }
    }

}
