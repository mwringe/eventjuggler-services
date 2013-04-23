package org.eventjuggler.services.simpleauth.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Attribute implements Serializable {

    private final String name;

    private final String value;

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
