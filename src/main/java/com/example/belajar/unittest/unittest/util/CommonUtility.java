package com.example.belajar.unittest.unittest.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommonUtility {

    public String getUuid(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
