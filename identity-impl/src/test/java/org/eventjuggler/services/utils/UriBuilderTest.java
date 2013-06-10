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
package org.eventjuggler.services.utils;

import java.net.URI;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class UriBuilderTest {

    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;
    private MultivaluedMap<String, String> requestHeaders;

    @SuppressWarnings("unchecked")
    @Before
    public void before() throws Exception {
        uriInfo = Mockito.mock(UriInfo.class);
        httpHeaders = Mockito.mock(HttpHeaders.class);
        requestHeaders = Mockito.mock(MultivaluedMap.class);

        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(new URI("http://localhost:8080/ejs-identity/api/test"));
        Mockito.when(httpHeaders.getRequestHeaders()).thenReturn(requestHeaders);
        Mockito.when(requestHeaders.containsKey("x-forwarded-proto")).thenReturn(false);
    }

    @Test
    public void absolute() {
        URI uri = new UriBuilder(httpHeaders, uriInfo, "http://www.somedomain.com/test").setQueryParam("test", "value")
                .setQueryParam("test2", "value2").build();
        Assert.assertEquals("http://www.somedomain.com/test?test=value&test2=value2", uri.toString());
    }

    @Test
    public void xForwardedProto() {
        Mockito.when(requestHeaders.containsKey("x-forwarded-proto")).thenReturn(true);
        Mockito.when(requestHeaders.get("x-forwarded-proto")).thenReturn(Collections.singletonList("https"));

        URI uri = new UriBuilder(httpHeaders, uriInfo, "/test").build();
        Assert.assertEquals("https://localhost:8080/test", uri.toString());
    }

    @Test
    public void absoluteHttps() {
        URI uri = new UriBuilder(httpHeaders, uriInfo, "https://www.somedomain.com/test").setQueryParam("test", "value")
                .setQueryParam("test2", "value2").build();
        Assert.assertEquals("https://www.somedomain.com/test?test=value&test2=value2", uri.toString());
    }

    @Test
    public void absoluteHash() {
        URI uri = new UriBuilder(httpHeaders, uriInfo, "http://www.somedomain.com/test#/test").setQueryParam("test", "value")
                .setQueryParam("test2", "value2").build();
        Assert.assertEquals("http://www.somedomain.com/test?test=value&test2=value2#/test", uri.toString());
    }

    @Test
    public void relativeToHost() {
        URI uri = new UriBuilder(httpHeaders, uriInfo, "/test").setQueryParam("test", "value")
                .setQueryParam("test2", "value2").build();
        Assert.assertEquals("http://localhost:8080/test?test=value&test2=value2", uri.toString());
    }

    @Test
    public void relativeToApp() {
        URI uri = new UriBuilder(httpHeaders, uriInfo, "test").setQueryParam("test", "value").setQueryParam("test2", "value2")
                .build();
        Assert.assertEquals("http://localhost:8080/ejs-identity/test?test=value&test2=value2", uri.toString());
    }

}
