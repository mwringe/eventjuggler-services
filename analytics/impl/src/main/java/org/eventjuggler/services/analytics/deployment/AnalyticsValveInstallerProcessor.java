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

import java.util.ArrayList;
import java.util.List;

import org.eventjuggler.services.analytics.extension.AnalyticsService;
import org.eventjuggler.services.analytics.web.AnalyticsValve;
import org.jboss.as.naming.ManagedReferenceFactory;
import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.web.deployment.WarMetaData;
import org.jboss.logging.Logger;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.jboss.ValveMetaData;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AnalyticsValveInstallerProcessor implements DeploymentUnitProcessor {

    private static final Logger log = Logger.getLogger("org.eventjuggler.services.analytics");

    public static final Phase PHASE = Phase.PARSE;

    public static final int PRIORITY = 0x4000;

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        final WarMetaData warMetaData = deploymentUnit.getAttachment(WarMetaData.ATTACHMENT_KEY);
        if (warMetaData == null) {
            return;
        }

        final JBossWebMetaData metaData = warMetaData.getMergedJBossWebMetaData();
        if (metaData == null) {
            return;
        }

        phaseContext.addDependency(AnalyticsService.JNDI_SERVICE_NAME, AttachmentKey.create(ManagedReferenceFactory.class));

        ValveMetaData valve = new ValveMetaData();
        valve.setId(AnalyticsValve.class.getSimpleName());
        valve.setValveClass(AnalyticsValve.class.getName());
        valve.setModule(AnalyticsMarkerProcessor.ANALYTICS_IDENTIFIER_NAME);

        List<ValveMetaData> valves = metaData.getValves();
        if (valves == null) {
            metaData.setValves(valves = new ArrayList<ValveMetaData>());
        }
        valves.add(valve);

        log.infov("Enabling analytics valve for {0}", deploymentUnit.getName());
    }

    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
