package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssue {
    private String key;
    private Fields fields;
}
