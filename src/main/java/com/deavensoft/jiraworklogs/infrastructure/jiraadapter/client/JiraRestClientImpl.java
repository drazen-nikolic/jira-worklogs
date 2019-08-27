package com.deavensoft.jiraworklogs.infrastructure.jiraadapter.client;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * Executes calls to JIRA REST API (wrapper around {@link RestTemplate}).
 */
@RequiredArgsConstructor
public class JiraRestClientImpl implements JiraRestClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String username;
    private final String apiToken;

    @Override
    public <T> T exchange(String uri, HttpMethod method, Class<T> responseType) {
        ResponseEntity<T> result = restTemplate.exchange
                (baseUrl + uri, method,
                        new HttpEntity<>(createHeaders(username, apiToken)), responseType);
        return result.getBody();
    }

    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

}
