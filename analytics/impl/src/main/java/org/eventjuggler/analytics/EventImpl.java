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
package org.eventjuggler.analytics;

import java.io.Serializable;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class EventImpl implements Serializable, Event {

    private final String contextPath;
    private final String country;
    private final String language;
    private final String remoteAddr;
    private final long time;
    private final String uri;
    private final String userAgent;

    public EventImpl(long time, String contextPath, String uri, String remoteAddr, String country, String language,
            String userAgent) {
        this.time = time;
        this.contextPath = contextPath;
        this.uri = uri;
        this.remoteAddr = remoteAddr;
        this.country = country;
        this.language = language;
        this.userAgent = userAgent;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public String getPage() {
        return uri;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

}
