package com.example.messenger.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncodeOperations {

    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    public String encode(String str ){
        return encoder.encode(str);
    }
    public Boolean matches(String str1,String str2){
        return encoder.matches(str1,str2);
    }
}
