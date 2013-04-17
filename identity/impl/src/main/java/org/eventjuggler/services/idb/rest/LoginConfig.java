package org.eventjuggler.services.idb.rest;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginConfig {

    private String name;

    private List<ProviderLoginConfig> providerConfigs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProviderLoginConfig> getProviderConfigs() {
        return providerConfigs;
    }

    public void setProviderConfigs(List<ProviderLoginConfig> providerConfigs) {
        this.providerConfigs = providerConfigs;
    }

    public static class ProviderLoginConfig {

        private String name;

        private URI loginUri;

        private URI icon;

        public ProviderLoginConfig() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public URI getLoginUri() {
            return loginUri;
        }

        public void setLoginUri(URI loginUri) {
            this.loginUri = loginUri;
        }

        public URI getIcon() {
            return icon;
        }

        public void setIcon(URI icon) {
            this.icon = icon;
        }

    }

}
