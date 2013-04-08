package org.eventjuggler.services.simpleauth.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AccountRegistrationResponse implements Serializable {

    private static final long serialVersionUID = -6286885353352367908L;

    private boolean registered;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}