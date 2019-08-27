package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fields {
    private String summary;
    private Name issuetype;
    private Name priority;
    private JiraWorkLog worklog;
}
