package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.SubscriptionRequest;
import io.swagger.v3.core.util.Json;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SubscriptionService {

    @Value("${app.mailchimp.api.url}")
    private String url;

    @Value("${app.mailchimp.api.key}")
    private String apiKey;

    @Value("${app.mailchimp.list.id}")
    private String listId;

    public String subscribe(@RequestBody @Valid SubscriptionRequest subscriptionRequest) throws Exception {

        String link = url + "/lists/" + listId + "/members";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(link))
                .header("Authorization","apikey " + apiKey)
                .header("Content-Type", "application/json")
                .header("accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Json.pretty(subscriptionRequest)))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }
}