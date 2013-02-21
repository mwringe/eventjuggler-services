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

import java.util.List;

import org.eventjuggler.analytics.Analytics;
import org.eventjuggler.analytics.AnalyticsService;
import org.eventjuggler.analytics.deployment.AnalyticsMarkerProcessor;
import org.eventjuggler.analytics.deployment.AnalyticsWebFilterProcessor;
import org.eventjuggler.analytics.deployment.AnalyticsWeldExtensionProcessor;
import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.naming.ManagedReferenceFactory;
import org.jboss.as.naming.ServiceBasedNamingStore;
import org.jboss.as.naming.ValueManagedReferenceFactory;
import org.jboss.as.naming.deployment.ContextNames;
import org.jboss.as.naming.service.BinderService;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.inject.InjectionException;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.value.ImmediateValue;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
class AnalyticsSubsystemAdd extends AbstractBoottimeAddStepHandler {

    static final AnalyticsSubsystemAdd INSTANCE = new AnalyticsSubsystemAdd();

    private AnalyticsSubsystemAdd() {
    }

    @Override
    public void performBoottime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> controllers)
            throws OperationFailedException {
        final ServiceTarget target = context.getServiceTarget();

        final ServiceController<Analytics> controller = AnalyticsService.addService(target, verificationHandler);
        controllers.add(controller);

        context.addStep(new AbstractDeploymentChainStep() {
            @SuppressWarnings("deprecation")
            @Override
            public void execute(DeploymentProcessorTarget processorTarget) {
                processorTarget.addDeploymentProcessor(AnalyticsMarkerProcessor.PHASE, AnalyticsMarkerProcessor.PRIORITY,
                        new AnalyticsMarkerProcessor());

                processorTarget.addDeploymentProcessor(AnalyticsWebFilterProcessor.PHASE, AnalyticsWebFilterProcessor.PRIORITY,
                        new AnalyticsWebFilterProcessor());

                processorTarget.addDeploymentProcessor(AnalyticsWeldExtensionProcessor.PHASE,
                        AnalyticsWeldExtensionProcessor.PRIORITY, new AnalyticsWeldExtensionProcessor());
            }
        }, OperationContext.Stage.RUNTIME);

        final BinderService binderService = new BinderService("AnalyticsService");
        final ServiceBuilder<ManagedReferenceFactory> builder = context.getServiceTarget().addService(
                ContextNames.JBOSS_CONTEXT_SERVICE_NAME.append("AnalyticsService"), binderService);
        builder.addDependency(ContextNames.JBOSS_CONTEXT_SERVICE_NAME, ServiceBasedNamingStore.class,
                binderService.getNamingStoreInjector());
        builder.addDependency(AnalyticsService.SERVICE_NAME, Analytics.class, new Injector<Analytics>() {
            @Override
            public void inject(final Analytics value) throws InjectionException {
                binderService.getManagedObjectInjector().inject(
                        new ValueManagedReferenceFactory(new ImmediateValue<Object>(value)));
            }

            @Override
            public void uninject() {
                binderService.getManagedObjectInjector().uninject();
            }
        });
        builder.addListener(verificationHandler);

        controllers.add(builder.install());
    }

    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        model.setEmptyObject();
    }
}
