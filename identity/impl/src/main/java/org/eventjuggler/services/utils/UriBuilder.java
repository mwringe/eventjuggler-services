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

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class UriBuilder {

    private final javax.ws.rs.core.UriBuilder b;

    public UriBuilder(UriInfo uriInfo, String path) {
        if (path.contains("://")) {
            b = javax.ws.rs.core.UriBuilder.fromUri(path);
        } else if (path.startsWith("/")) {
            b = javax.ws.rs.core.UriBuilder.fromUri(uriInfo.getAbsolutePath().resolve(path));
        } else {
            URI uri = uriInfo.getAbsolutePath();
            String p = uri.getPath();
            p = p.substring(0, p.indexOf('/', 1) + 1);
            uri = uri.resolve(p + path);
            b = javax.ws.rs.core.UriBuilder.fromUri(uri);
        }
    }

    public UriBuilder setQueryParam(String name, String value) {
        b.replaceQueryParam(name, value);
        return this;
    }

    public URI build() {
        return b.build();
    }

}
