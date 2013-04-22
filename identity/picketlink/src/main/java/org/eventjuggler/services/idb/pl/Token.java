package org.eventjuggler.services.idb.pl;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class Token implements Serializable {

    private String token;

    public String getValue() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
