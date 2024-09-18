package com.intheknowyyc.api.data.models;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class UserId implements Serializable {
    private int Id;
    private String username;
    private String email;
}
