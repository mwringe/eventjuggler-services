package com.eventjuggler.services.activities;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenResolverSystemBaseImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;

public class Deployments {

    /**
     * This is a hack to lookup the full canonical name for a dependency, as there seems to be a bug in shrinkwrap resolver
     */
    private static String expandCanonical(String canonical) {
        try {
            @SuppressWarnings("rawtypes")
            MavenResolverSystemBaseImpl resolver = (MavenResolverSystemBaseImpl) Maven.resolver();
            resolver.loadPomFromFile("../pom.xml");

            Method method = MavenResolverSystemBaseImpl.class.getDeclaredMethod("getSession", new Class<?>[0]);
            method.setAccessible(true);

            MavenWorkingSessionImpl mavenSession = (MavenWorkingSessionImpl) method.invoke(resolver);

            for (Iterator<MavenDependency> itr = mavenSession.getDependencyManagement().iterator(); itr.hasNext();) {
                MavenDependency dep = itr.next();
                if (dep.toCanonicalForm().startsWith(canonical)) {
                    return dep.toCanonicalForm();
                }
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static EnterpriseArchive getActivitiesDeployment() {
        EnterpriseArchive archive = Maven.resolver().offline().loadPomFromFile("../pom.xml")
                .resolve(expandCanonical("org.eventjuggler.services:ejs-activities-ear:ear")).withoutTransitivity()
                .as(EnterpriseArchive.class)[0];
        return archive;
    }

    public static WebArchive getTestArchive(Class<?>... test) {
        return ShrinkWrap.create(WebArchive.class, "ejs-test.war").addClasses(test)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public static File getDependency(String canonical) {
        return Maven.resolver().offline().loadPomFromFile("../pom.xml").resolve(expandCanonical(canonical))
                .withoutTransitivity().asSingleFile();
    }

}
