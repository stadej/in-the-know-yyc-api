package com.intheknowyyc.api.controllers.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubscriptionRequest {

    @JsonProperty("email_address")
    private String emailAddress;

    private String status;

    @Data
    @ToString
    public class merge_fields {
        private String FNAME;
        private String LNAME;
    }

}
