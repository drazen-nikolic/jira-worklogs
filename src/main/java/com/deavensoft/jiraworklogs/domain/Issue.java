package com.deavensoft.jiraworklogs.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Issue {
    private String key;
    private String summary;
    private String type;
    private String priority;
}
