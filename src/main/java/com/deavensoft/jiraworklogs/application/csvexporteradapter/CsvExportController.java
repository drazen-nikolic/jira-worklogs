package com.deavensoft.jiraworklogs.application.csvexporteradapter;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;

/**
 * WEB endpoint for exporting user work logs as CSV file.
 */
@Controller
@RequestMapping("work-logs")
@RequiredArgsConstructor
public class CsvExportController {

    private final WorkLogCsvExport workLogCsvExport;

    @GetMapping(value = "/csv-export", produces = "text/csv")
    public @ResponseBody byte[] getFile(@RequestParam String userDisplayName, @RequestParam(required = false) String dayOfWeekList,
        @RequestParam String startDate, @RequestParam String endDate, HttpServletResponse response) throws IOException {
        File csvFile = workLogCsvExport.exportWorkLogsForUserInPeriod(userDisplayName, dayOfWeekList,
                LocalDate.parse(startDate), LocalDate.parse(endDate));
        if (csvFile.exists()) {
            try (InputStream in = new FileInputStream(csvFile)) {
                response.addHeader("Content-Disposition",
                    "attachment; filename=\"" + csvFile.getName() + "\"");
                return IOUtils.toByteArray(in);
            } finally {
                Files.delete(csvFile.toPath());
            }
        } else {
            throw new IllegalArgumentException("CSV file could not be produced for the given parameters!");
        }
    }
}
