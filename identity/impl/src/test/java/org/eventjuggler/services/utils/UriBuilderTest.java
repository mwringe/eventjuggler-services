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

    @Before
    public void before() throws Exception {
        uriInfo = Mockito.mock(UriInfo.class);

        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(new URI("http://localhost:8080/ejs-identity/api/test"));
    }

    @Test
    public void absolute() {
        URI uri = new UriBuilder(uriInfo, "http://www.somedomain.com/test").setQueryParam("test", "value")
                .setQueryParam("test2", "value2").build();
        Assert.assertEquals("http://www.somedomain.com/test?test=value&test2=value2", uri.toString());
    }

    @Test
    public void relativeToHost() {
        URI uri = new UriBuilder(uriInfo, "/test").setQueryParam("test", "value")
                .setQueryParam("test2", "value2").build();
        Assert.assertEquals("http://localhost:8080/test?test=value&test2=value2", uri.toString());
    }

    @Test
    public void relativeToApp() {
        URI uri = new UriBuilder(uriInfo, "test").setQueryParam("test", "value").setQueryParam("test2", "value2").build();
        Assert.assertEquals("http://localhost:8080/ejs-identity/test?test=value&test2=value2", uri.toString());
    }

}
