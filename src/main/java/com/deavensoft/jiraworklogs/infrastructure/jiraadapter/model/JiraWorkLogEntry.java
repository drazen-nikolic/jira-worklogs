package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraWorkLogEntry {
    private Author author;
    private String comment;
    private Integer timeSpentSeconds;
    private Date started;
}
