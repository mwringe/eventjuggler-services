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
package org.eventjuggler.analytics.rest;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.naming.NamingException;
import javax.ws.rs.core.Application;

import org.apache.catalina.Host;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.tomcat.InstanceManager;
import org.jboss.as.security.plugins.SecurityDomainContext;
import org.jboss.as.server.ServerEnvironment;
import org.jboss.as.web.deployment.WebCtxLoader;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class RestEasyPublisher {

    private final Host host;
    private final SecurityDomainContext securityDomainContext;

    public RestEasyPublisher(Host host, SecurityDomainContext securityDomainContext) {
        this.host = host;
        this.securityDomainContext = securityDomainContext;
    }

    public void publishRestServlet(String contextPath, Class<? extends Application> applicationClass) throws Exception {
        StandardContext context = new StandardContext();
        context.setPath(contextPath);

        File docBase = new File(System.getProperty(ServerEnvironment.SERVER_TEMP_DIR), "empty-dir");
        if (!docBase.isDirectory()) {
            docBase.mkdir();
        }

        context.setDocBase(docBase.getPath());
        context.addLifecycleListener(new ContextConfig());

        final Loader loader = new WebCtxLoader(Thread.currentThread().getContextClassLoader());

        loader.setContainer(host);
        context.setLoader(loader);
        context.setInstanceManager(new LocalInstanceManager());

        Wrapper wrapper = context.createWrapper();
        wrapper.setName("Resteasy");
        wrapper.setServletClass("org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher");

        context.addApplicationListener("org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap");
        wrapper.setLoadOnStartup(1);
        wrapper.addInitParameter("javax.ws.rs.Application", applicationClass.getName());

        context.addChild(wrapper);
        context.addServletMapping("/*", "Resteasy");

        context.addValve(new AuthenticationValve(securityDomainContext));

        host.addChild(context);
        context.create();
        context.start();
    }

    private static class LocalInstanceManager implements InstanceManager {
        LocalInstanceManager() {
        }

        @Override
        public Object newInstance(String className) throws IllegalAccessException, InvocationTargetException, NamingException,
                InstantiationException, ClassNotFoundException {
            return Class.forName(className).newInstance();
        }

        @Override
        public Object newInstance(String fqcn, ClassLoader classLoader) throws IllegalAccessException,
                InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException {
            return Class.forName(fqcn, false, classLoader).newInstance();
        }

        @Override
        public Object newInstance(Class<?> c) throws IllegalAccessException, InvocationTargetException, NamingException,
                InstantiationException {
            return c.newInstance();
        }

        @Override
        public void newInstance(Object o) throws IllegalAccessException, InvocationTargetException, NamingException {
            throw new IllegalStateException();
        }

        @Override
        public void destroyInstance(Object o) throws IllegalAccessException, InvocationTargetException {
        }
    }

}
