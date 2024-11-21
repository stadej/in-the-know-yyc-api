package com.intheknowyyc.api.data.translators;

import com.intheknowyyc.api.controllers.requests.UserRequest;
import com.intheknowyyc.api.data.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserTranslator {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    private UserTranslator(){
    }

    public static User translateToUser(UserRequest userRequest, int userId){
        User user = new User();
        if (userId != 0) {
            user.setId(userId);
        }
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        return user;
    }

}
