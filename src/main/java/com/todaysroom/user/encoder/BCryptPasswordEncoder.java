package com.todaysroom.user.encoder;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptPasswordEncoder {

    public String encrypt(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean isMatch(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }
}
