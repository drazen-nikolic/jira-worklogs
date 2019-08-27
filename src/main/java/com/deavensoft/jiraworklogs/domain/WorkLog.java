package com.deavensoft.jiraworklogs.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Main domain entity, representing work log.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkLog {
    private String userDisplayName;
    private String userEmail;
    private String description;
    private Float logHours;
    private LocalDate date;
    private Issue details;
}
