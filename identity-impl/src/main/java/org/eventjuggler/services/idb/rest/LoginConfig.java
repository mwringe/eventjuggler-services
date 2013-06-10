/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.eventjuggler.services.idb.rest;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@XmlRootElement
public class LoginConfig {

    private String name;

    private String callbackUrl;

    private List<ProviderLoginConfig> providerConfigs;

    public String getName() {
        return name;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
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
