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
package org.eventjuggler.analytics.deployment;

import javax.enterprise.inject.spi.Extension;

import org.eventjuggler.analytics.cdi.AnalyticsCdiExtension;
import org.jboss.as.server.deployment.AttachmentList;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.weld.deployment.WeldAttachments;
import org.jboss.logging.Logger;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.metadata.MetadataImpl;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AnalyticsWeldExtensionProcessor implements DeploymentUnitProcessor {

    private static final Logger log = Logger.getLogger("org.eventjuggler.analytics");

    public static final Phase PHASE = Phase.STRUCTURE;

    public static final int PRIORITY = 0x4000;

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        if (deploymentUnit.getAttachment(AnalyticsMarkerProcessor.ENABLE_ANALYTICS_KEY) == null) {
            return;
        }

        if (deploymentUnit.getParent() != null) {
            deploymentUnit = deploymentUnit.getParent();
        }

        AttachmentList<Metadata<Extension>> extensions = deploymentUnit.getAttachment(WeldAttachments.PORTABLE_EXTENSIONS);
        if (extensions != null) {
            for (Metadata<Extension> e : extensions) {
                if (e.getValue() instanceof AnalyticsCdiExtension) {
                    return;
                }
            }
        }

        Extension extension = new AnalyticsCdiExtension();
        Metadata<Extension> metadata = new MetadataImpl<Extension>(extension, deploymentUnit.getName());
        deploymentUnit.addToAttachmentList(WeldAttachments.PORTABLE_EXTENSIONS, metadata);

        log.infov("Enabling analytics extension for {0}", deploymentUnit.getName());
    }

    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
