package com.emintolgahanpolat.api.configuration.security;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;


public class AuthenticationRequest implements Serializable {

    private static final long serialVersionUID = -8445943548965154778L;

    private String username;
    @Size(
            min = 5,
            max = 100,
            message = "error.invalidPassword"
    )
    private String password;


    public AuthenticationRequest() {
        super();
    }

    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
