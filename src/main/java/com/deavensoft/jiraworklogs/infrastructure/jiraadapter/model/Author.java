package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {
    private String accountId;
    private String emailAddress;
    private String displayName;
}
