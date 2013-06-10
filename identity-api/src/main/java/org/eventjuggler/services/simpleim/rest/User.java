package org.eventjuggler.services.simpleim.rest;

import javax.xml.bind.annotation.XmlRootElement;

import org.eventjuggler.services.simpleauth.rest.UserInfo;

@XmlRootElement
public class User extends UserInfo {

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}