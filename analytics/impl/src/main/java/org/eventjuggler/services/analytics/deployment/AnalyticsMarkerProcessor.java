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
package org.eventjuggler.services.analytics.deployment;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.logging.Logger;
import org.jboss.modules.ModuleIdentifier;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AnalyticsMarkerProcessor implements DeploymentUnitProcessor {

    public static final AttachmentKey<Boolean> ENABLE_ANALYTICS_KEY = AttachmentKey.create(Boolean.class);

    public static final ModuleIdentifier ANALYTICS_IDENTIFIER = ModuleIdentifier.create("org.eventjuggler.services.analytics");

    private static final Logger log = Logger.getLogger("org.eventjuggler.services.analytics");

    public static final Phase PHASE = Phase.STRUCTURE;

    public static final int PRIORITY = 0x3000;

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        if (enabledAnalytics(deploymentUnit)) {

            if (deploymentUnit.getParent() != null) {
                deploymentUnit = deploymentUnit.getParent();
            }

            Boolean existingValue = deploymentUnit.putAttachment(ENABLE_ANALYTICS_KEY, true);

            if (existingValue == null) {
                log.infov("Enabling analytics for {0}", deploymentUnit.getName());
            }
        }
    }

    private boolean enabledAnalytics(DeploymentUnit deploymentUnit) {
        ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
        for (ModuleDependency d : moduleSpecification.getUserDependencies()) {
            if (d.getIdentifier().equals(ANALYTICS_IDENTIFIER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
