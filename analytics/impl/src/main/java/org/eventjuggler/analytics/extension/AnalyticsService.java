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
package org.eventjuggler.analytics.extension;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.eventjuggler.analytics.Analytics;
import org.eventjuggler.analytics.AnalyticsImpl;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.naming.deployment.ContextNames;
import org.jboss.as.txn.service.UserTransactionService;
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

/**
 * TODO Currently all applications (WARs) and all urls are logged. This should be configurable through standalone.xml (AS
 * management)
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AnalyticsService implements Service<Analytics> {

    private static final Logger log = Logger.getLogger("org.eventjuggler.analytics");

    public static ServiceName SERVICE_NAME = ServiceName.JBOSS.append("analytics");

    public static ServiceName JNDI_SERVICE_NAME = ContextNames.JBOSS_CONTEXT_SERVICE_NAME.append("AnalyticsService");

    public static ServiceController<Analytics> addService(final ServiceTarget target,
            final ServiceVerificationHandler verificationHandler) {
        AnalyticsService service = new AnalyticsService();
        ServiceBuilder<Analytics> serviceBuilder = target.addService(SERVICE_NAME, service);

        serviceBuilder.addDependency(UserTransactionService.SERVICE_NAME, UserTransaction.class, service.userTransaction);

        serviceBuilder.addListener(verificationHandler);
        return serviceBuilder.install();
    }

    private EntityManagerFactory emf;

    private final InjectedValue<UserTransaction> userTransaction = new InjectedValue<UserTransaction>();

    @Override
    public Analytics getValue() throws IllegalStateException, IllegalArgumentException {
        return new AnalyticsImpl(emf, userTransaction.getValue());
    }

    @Override
    public void start(StartContext context) throws StartException {
        log.info("Starting Analytics Service");

        log.info("Creating entity manager factory");
        emf = Persistence.createEntityManagerFactory("analytics");
    }

    @Override
    public void stop(StopContext context) {
        log.info("Stopping Analytics Service");

        log.info("Closing entity manager factory");
        emf.close();
    }

}
