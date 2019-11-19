package com.deavensoft.jiraworklogs.application.csvexporteradapter;

import com.deavensoft.jiraworklogs.domain.Issue;
import com.deavensoft.jiraworklogs.domain.WorkLog;
import com.deavensoft.jiraworklogs.domain.WorkLogManagerPort;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkLogCsvExportAdapterTest {
    private static final String USER_DISPLAY_NAME = "John Doe";
    private static final float LOG_HOURS = 1.5f;
    private static final String WORK_DESCRIPTION = "work description";
    private static final LocalDate WORK_LOG_DATE = LocalDate.of(2019, 7, 24);
    private static final String ISSUE_KEY = "TEST-123";
    private static final String ISSUE_TYPE = "Story";
    private static final String ISSUE_PRIORITY = "major";
    private static final String ISSUE_SUMMARY = "Test issue summary";
    private static final String COMMA_DELIMITER = ",";
    private static final LocalDate START_DATE = LocalDate.of(2019, 7, 1);
    private static final LocalDate END_DATE = LocalDate.of(2019, 7, 31);


    private WorkLogCsvExportAdapter workLogCsvExportAdapter;

    @Mock
    private WorkLogManagerPort workLogManagerPort;

    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    private String readFirstLineFromFile(File csvFile) throws IOException {
        try(InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream(csvFile), StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(inputStreamReader)) {
            return in.readLine();
        }
    }

    private List<List<String>> readCsvFileRecords(File csvFile) throws FileNotFoundException {
        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(csvFile)) {
            while (scanner.hasNextLine()) {
                records.add(getRecordFromLine(scanner.nextLine()));
            }
        }
        return records;
    }

    @Before
    public void setUp() {
        Collection<WorkLog> workLogCollection = Collections.singletonList(
                WorkLog.builder()
                        .userDisplayName(USER_DISPLAY_NAME)
                        .logHours(LOG_HOURS)
                        .description(WORK_DESCRIPTION)
                        .date(WORK_LOG_DATE)
                        .details(Issue.builder()
                                .key(ISSUE_KEY)
                                .type(ISSUE_TYPE)
                                .priority(ISSUE_PRIORITY)
                                .summary(ISSUE_SUMMARY)
                                .build())
                        .build()
        );
        when(workLogManagerPort.findWorkLogsForUserInPeriod(any(), any(), any(), any())).thenReturn(workLogCollection);

        workLogCsvExportAdapter = new WorkLogCsvExportAdapter(workLogManagerPort);
    }

    @Test
    public void exportWorkLogsForUserInPeriod_ShouldReturnNull_WhenNoWorkLogEntriesExist() {
        // given
        WorkLogManagerPort workLogManagerPortWithNoResults = Mockito.mock(WorkLogManagerPort.class);
        when(workLogManagerPortWithNoResults.findWorkLogsForUserInPeriod(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        workLogCsvExportAdapter = new WorkLogCsvExportAdapter(workLogManagerPortWithNoResults);

        // when
        File csvFile = workLogCsvExportAdapter.exportWorkLogsForUserInPeriod(USER_DISPLAY_NAME, null, START_DATE, END_DATE);

        // then
        assertThat(csvFile, is(nullValue()));
    }

    @Test
    public void exportWorkLogsForUserInPeriod_ShouldReturnCSVFileHeaderRecord_WhenWorkLogEntriesExist() throws Exception {
        // when
        File csvFile = workLogCsvExportAdapter.exportWorkLogsForUserInPeriod(USER_DISPLAY_NAME, null, START_DATE, END_DATE);

        // then
        assertThat(csvFile, is(notNullValue()));

        String firstLine = readFirstLineFromFile(csvFile);
        assertThat(firstLine, is(WorkLogCsvExportAdapter.HEADER_LINE));
    }


    @Test
    public void exportWorkLogsForUserInPeriod_ShouldReturnCSVFile_WhenWorkLogEntriesExist() throws Exception {
        // when
        File csvFile = workLogCsvExportAdapter.exportWorkLogsForUserInPeriod(USER_DISPLAY_NAME, null, START_DATE, END_DATE);

        // then
        assertThat(csvFile, is(notNullValue()));
        List<List<String>> records = readCsvFileRecords(csvFile);
        assertThat(records, hasSize(2));

        Iterator<String> workLogRecord = records.get(1).iterator();
        assertThat(workLogRecord.next(), is(workLogCsvExportAdapter.formatDate(WORK_LOG_DATE)));
        assertThat(workLogRecord.next(), is(workLogCsvExportAdapter.formatDayOfWeek(WORK_LOG_DATE)));
        assertThat(workLogRecord.next(), is(workLogCsvExportAdapter.formatFloat(LOG_HOURS)));
        assertThat(workLogRecord.next(), is(USER_DISPLAY_NAME));
        assertThat(workLogRecord.next(), is(ISSUE_KEY));
        assertThat(workLogRecord.next(), is(ISSUE_SUMMARY));
        assertThat(workLogRecord.next(), is(ISSUE_TYPE));
        assertThat(workLogRecord.next(), is(ISSUE_PRIORITY));
        assertThat(workLogRecord.next(), is(WORK_DESCRIPTION));
    }

    @Test
    public void exportWorkLogsForUserInPeriod_ShouldReturnCSVFileWithEmptyDescription_WhenWorkLogDescriptionIsNull() throws Exception {
        // given
        Collection<WorkLog> workLogCollection = Collections.singletonList(
                WorkLog.builder()
                        .userDisplayName(USER_DISPLAY_NAME)
                        .logHours(LOG_HOURS)
                        .description(null) // null description
                        .date(WORK_LOG_DATE)
                        .details(Issue.builder()
                                .key(ISSUE_KEY)
                                .type(ISSUE_TYPE)
                                .priority(ISSUE_PRIORITY)
                                .summary(ISSUE_SUMMARY)
                                .build())
                        .build()
        );
        when(workLogManagerPort.findWorkLogsForUserInPeriod(any(), any(), any(), any())).thenReturn(workLogCollection);


        // when
        File csvFile = workLogCsvExportAdapter.exportWorkLogsForUserInPeriod(USER_DISPLAY_NAME, null, START_DATE, END_DATE);

        // then
        assertThat(csvFile, is(notNullValue()));
        List<List<String>> records = readCsvFileRecords(csvFile);
        assertThat(records, hasSize(2));

        Iterator<String> workLogRecord = records.get(1).iterator();
        assertThat(workLogRecord.next(), is(workLogCsvExportAdapter.formatDate(WORK_LOG_DATE)));
        assertThat(workLogRecord.next(), is(workLogCsvExportAdapter.formatDayOfWeek(WORK_LOG_DATE)));
        assertThat(workLogRecord.next(), is(workLogCsvExportAdapter.formatFloat(LOG_HOURS)));
        assertThat(workLogRecord.next(), is(USER_DISPLAY_NAME));
        assertThat(workLogRecord.next(), is(ISSUE_KEY));
        assertThat(workLogRecord.next(), is(ISSUE_SUMMARY));
        assertThat(workLogRecord.next(), is(ISSUE_TYPE));
        assertThat(workLogRecord.next(), is(ISSUE_PRIORITY));
        assertThat(workLogRecord.hasNext(), is(false));
    }

    @Test
    public void convertToRegex_ShouldConvertProperly_WhenNotNull() {
        // when
        String regexString = workLogCsvExportAdapter.convertToRegex("Mon, Tue,Wed");

        // then
        assertThat(regexString, is("Mon|Tue|Wed"));
    }

    @Test
    public void convertToRegex_ShouldReturnNull_WhenNull() {
        // when
        String regexString = workLogCsvExportAdapter.convertToRegex(null);

        // then
        assertThat(regexString, is(nullValue()));
    }

    @Test
    public void convertToRegex_ShouldReturnNull_WhenEmptyString() {
        // when
        String regexString = workLogCsvExportAdapter.convertToRegex("   ");

        // then
        assertThat(regexString, is(nullValue()));
    }
}