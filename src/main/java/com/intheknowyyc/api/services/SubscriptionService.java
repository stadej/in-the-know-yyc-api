package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.SubscriptionRequest;
import com.intheknowyyc.api.data.exceptions.BadRequestException;
import com.intheknowyyc.api.data.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service
public class SubscriptionService {

    @Value("${mailchimp_url}")
    private String url;

    @Value("${mailchimp_api_key}")
    private String apiKey;

    @Value("${mailchimp_list_id}")
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

        subscriptionRequest.setStatus("pending");
        String link = url + "/lists/" + listId + "/members";
        // String link = "https://us8.api.mailchimp.com/3.0/lists/a8afc9b6a1/members";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "apikey " + apiKey);
        // headers.set("Authorization", "apikey 52d235aaac6c918d9e605f9bc22c4011-us8");
        HttpEntity<SubscriptionRequest> requestEntity = new HttpEntity<>(subscriptionRequest, headers);
        try {
            return restTemplate.exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BadRequestException("Failed to subscribe: " + e.getMessage());
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Resource not found: " + e.getMessage());
        } catch (RestClientException e) {
            throw new RestClientException("An error occurred: " + e.getMessage());
        }
    }
}