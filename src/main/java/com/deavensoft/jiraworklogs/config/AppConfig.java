package com.deavensoft.jiraworklogs.config;

import com.deavensoft.jiraworklogs.application.csvexporteradapter.WorkLogCsvExport;
import com.deavensoft.jiraworklogs.application.csvexporteradapter.WorkLogCsvExportAdapter;
import com.deavensoft.jiraworklogs.domain.WorkLogManager;
import com.deavensoft.jiraworklogs.domain.WorkLogManagerPort;
import com.deavensoft.jiraworklogs.domain.WorkLogServicePort;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods.FilterIssuesInPeriodWithWorkLogs;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.apimethods.GetIssue;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.client.JiraRestClient;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.client.JiraRestClientImpl;
import com.deavensoft.jiraworklogs.infrastructure.jiraadapter.JiraWorkLogServiceAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {

    // domain
    @Bean
    public WorkLogManagerPort workLogManager(WorkLogServicePort workLogService) {
        return new WorkLogManager(workLogService);
    }

    // csvexportadapter
    @Bean
    public WorkLogCsvExport workLogCsvExport(WorkLogManagerPort workLogManager) {
        return new WorkLogCsvExportAdapter(workLogManager);
    }

    // jiraadapter
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JiraRestClient jiraRestClient(RestTemplate restTemplate, AppProperties properties) {
        return new JiraRestClientImpl(restTemplate,
                properties.getBaseUrl(), properties.getUsername(), properties.getApiToken());
    }

    @Bean
    public FilterIssuesInPeriodWithWorkLogs filterIssuesInPeriodWithWorkLogs(JiraRestClient jiraRestClient) {
        return new FilterIssuesInPeriodWithWorkLogs(jiraRestClient);
    }

    @Bean
    public GetIssue getIssue(JiraRestClient jiraRestClient) {
        return new GetIssue(jiraRestClient);
    }

    @Bean
    public WorkLogServicePort workLogServicePort(FilterIssuesInPeriodWithWorkLogs filterIssuesInPeriodWithWorkLogs, GetIssue getIssue) {
        return new JiraWorkLogServiceAdapter(filterIssuesInPeriodWithWorkLogs, getIssue);
    }
}
