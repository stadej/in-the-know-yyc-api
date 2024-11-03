package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.SubscriptionRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;


@Service
public class SubscriptionService {

    @Value("${app.mailchimp.api.url}")
    private String url;

    @Value("${app.mailchimp.api.key}")
    private String apiKey;

    @Value("${app.mailchimp.list.id}")
    private String listId;

    private final RestTemplate restTemplate;

    /**
     * Constructs a SubscriptionService with the provided RestTemplate.
     *
     * @param restTemplate the RestTemplate to use for HTTP requests
     */
    @Autowired
    public SubscriptionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Subscribes a user to the Mailchimp list.
     *
     * @param subscriptionRequest the subscription request containing user details
     * @return the response body as a String
     */
    public String subscribe(@RequestBody @Valid SubscriptionRequest subscriptionRequest) {

        String link = url + "/lists/" + listId + "/members";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "apikey " + apiKey);
        HttpEntity<SubscriptionRequest> requestEntity = new HttpEntity<>(subscriptionRequest, headers);

        return restTemplate.exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();

    }
}