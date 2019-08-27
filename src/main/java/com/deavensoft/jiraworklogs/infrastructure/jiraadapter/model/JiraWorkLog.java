package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraWorkLog {
    private Integer startAt;
    private Integer maxResults;
    private Integer total;
    private List<JiraWorkLogEntry> worklogs;
}
