package org.eventjuggler.services.analytics.extension;

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
import org.eventjuggler.services.analytics.rest.AnalyticsApplication;
import org.eventjuggler.services.analytics.rest.AuthenticationValve;
import org.eventjuggler.services.analytics.web.AnalyticsWeb;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.security.plugins.SecurityDomainContext;
import org.jboss.as.security.service.SecurityDomainService;
import org.jboss.as.server.ServerEnvironment;
import org.jboss.as.web.VirtualHost;
import org.jboss.as.web.WebSubsystemServices;
import org.jboss.as.web.deployment.WebCtxLoader;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

public class AnalyticsWebService implements Service<AnalyticsWebService> {

    private static class LocalInstanceManager implements InstanceManager {
        LocalInstanceManager() {
        }

        @Override
        public void destroyInstance(Object o) throws IllegalAccessException, InvocationTargetException {
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
        public Object newInstance(String className) throws IllegalAccessException, InvocationTargetException, NamingException,
                InstantiationException, ClassNotFoundException {
            return Class.forName(className).newInstance();
        }

        @Override
        public Object newInstance(String fqcn, ClassLoader classLoader) throws IllegalAccessException,
                InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException {
            return Class.forName(fqcn, false, classLoader).newInstance();
        }
    }

    private static final String CONTEXT_PATH = "/analytics";

    private static final Logger log = Logger.getLogger("org.eventjuggler.services.analytics");

    public static ServiceName SERVICE_NAME = AnalyticsService.SERVICE_NAME.append("web");

    public static ServiceController<AnalyticsWebService> addService(final ServiceTarget target,
            final ServiceVerificationHandler verificationHandler) {
        AnalyticsWebService service = new AnalyticsWebService();
        ServiceBuilder<AnalyticsWebService> serviceBuilder = target.addService(SERVICE_NAME, service);

        serviceBuilder.addDependency(WebSubsystemServices.JBOSS_WEB_HOST.append("default-host"), VirtualHost.class,
                service.virtualHostValue);

        serviceBuilder.addDependency(SecurityDomainService.SERVICE_NAME.append("other"), SecurityDomainContext.class,
                service.securityDomainContextValue);

        serviceBuilder.addDependency(AnalyticsService.JNDI_SERVICE_NAME);

        serviceBuilder.addListener(verificationHandler);
        return serviceBuilder.install();
    }

    private StandardContext context;

    private Host host;

    private final InjectedValue<SecurityDomainContext> securityDomainContextValue = new InjectedValue<>();

    private final InjectedValue<VirtualHost> virtualHostValue = new InjectedValue<>();

    @Override
    public AnalyticsWebService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    public void publishServlets(String contextPath, Class<? extends Application> applicationClass) throws Exception {
        host = virtualHostValue.getValue().getHost();
        SecurityDomainContext securityDomainContext = securityDomainContextValue.getValue();

        context = new StandardContext();
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
        context.addApplicationListener("org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap");

        Wrapper webWrapper = context.createWrapper();
        webWrapper.setName(AnalyticsWeb.class.getSimpleName());
        webWrapper.setServletClass(AnalyticsWeb.class.getName());
        webWrapper.setLoadOnStartup(0);
        context.addChild(webWrapper);
        context.addServletMapping("/", AnalyticsWeb.class.getSimpleName());

        Wrapper restWrapper = context.createWrapper();
        restWrapper.setName("Resteasy");
        restWrapper.setServletClass("org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher");
        restWrapper.setLoadOnStartup(0);
        restWrapper.addInitParameter("javax.ws.rs.Application", applicationClass.getName());
        context.addChild(restWrapper);
        context.addServletMapping("/rest/*", "Resteasy");

        context.addValve(new AuthenticationValve(securityDomainContext));

        host.addChild(context);
        context.create();
        context.start();
    }

    @Override
    public void start(StartContext startContext) throws StartException {
        log.info("Registering web context: " + CONTEXT_PATH);

        try {
            publishServlets(CONTEXT_PATH, AnalyticsApplication.class);
        } catch (Exception e) {
            throw new StartException("Failed to register web context", e);
        }
    }

    @Override
    public void stop(StopContext stopContext) {
        host.removeChild(context);
    }

}
