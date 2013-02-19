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
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
class SubsystemAdd extends AbstractBoottimeAddStepHandler {

    static final SubsystemAdd INSTANCE = new SubsystemAdd();

    private final Logger log = Logger.getLogger(SubsystemAdd.class);

    private SubsystemAdd() {
    }

    /** {@inheritDoc} */
    @Override
    public void performBoottime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
                    throws OperationFailedException {
        String suffix = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        AnalyticsService analyticsService = new AnalyticsService();
        final ServiceName analyticsServiceName = AnalyticsService.createServiceName(suffix);
        ServiceController<Analytics> controller = context.getServiceTarget().addService(analyticsServiceName, analyticsService)
                .addListener(verificationHandler).setInitialMode(Mode.ACTIVE).install();
        newControllers.add(controller);

        context.addStep(new AbstractDeploymentChainStep() {
            @Override
            public void execute(DeploymentProcessorTarget processorTarget) {
                processorTarget.addDeploymentProcessor(SubsystemExtension.SUBSYSTEM_NAME, AnalyticsMarkerProcessor.PHASE,
                        AnalyticsMarkerProcessor.PRIORITY, new AnalyticsMarkerProcessor());

                processorTarget.addDeploymentProcessor(SubsystemExtension.SUBSYSTEM_NAME, AnalyticsWebFilterProcessor.PHASE,
                        AnalyticsWebFilterProcessor.PRIORITY, new AnalyticsWebFilterProcessor());

                processorTarget.addDeploymentProcessor(SubsystemExtension.SUBSYSTEM_NAME,
                        AnalyticsWeldExtensionProcessor.PHASE, AnalyticsWeldExtensionProcessor.PRIORITY,
                        new AnalyticsWeldExtensionProcessor(analyticsServiceName));

            }
        }, OperationContext.Stage.RUNTIME);

    }

    /** {@inheritDoc} */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        log.info("Populating the model");
        model.setEmptyObject();
    }
}
