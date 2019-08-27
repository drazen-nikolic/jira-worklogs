package com.deavensoft.jiraworklogs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jira")
class AppProperties {
    private String baseUrl;
    private String username;
    private String apiToken;
}
