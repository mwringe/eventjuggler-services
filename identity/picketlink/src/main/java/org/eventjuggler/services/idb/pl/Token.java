package org.eventjuggler.services.idb.pl;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class Token {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
