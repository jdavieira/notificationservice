package com.critical.notificationservice.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    @Value("${auth.service}")
    private String authServiceUrl;

    @Value("${springdoc.swagger-ui.oauth.client-id}")
    private String clientId;

    @Value("${springdoc.swagger-ui.oauth.client-secret}")
    private String clientSecret;

    public String getAccessToken() {

        String requestBody = "grant_type=client_credentials&client_id=" +
                clientId+"&client_secret="+clientSecret+"&scope=write";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response =  new RestTemplate().postForEntity(
                this.authServiceUrl,
                request,
                String.class);

        JsonObject data = new Gson().fromJson(response.getBody(), JsonObject.class);

        return data.get("access_token").getAsString();
    }
}