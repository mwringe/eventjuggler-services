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
package org.eventjuggler.services.idb.provider;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.imageio.spi.ServiceRegistry;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class IdentityProviderService {

    private final List<IdentityProvider> providers = new LinkedList<>();

    public IdentityProvider getProvider(String id) {
        for (IdentityProvider p : providers) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public List<IdentityProvider> getProviders() {
        return providers;
    }

    @PostConstruct
    public void loadProviders() {
        Iterator<IdentityProvider> itr = ServiceRegistry.lookupProviders(IdentityProvider.class);
        while (itr.hasNext()) {
            providers.add(itr.next());
        }
    }

}
