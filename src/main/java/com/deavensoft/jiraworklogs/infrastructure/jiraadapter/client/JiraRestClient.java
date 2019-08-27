package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.client;

import org.springframework.http.HttpMethod;

public interface JiraRestClient {

    /**
     * Calls JIRA REST API.
     *
     * @param uri REST API URI.
     * @param method HTTP method to use.
     * @param responseType Response object.
     * @param <T> Parameter for response type.
     *
     * @return Response object.
     */
    <T> T exchange(String uri, HttpMethod method, Class<T> responseType);
}
