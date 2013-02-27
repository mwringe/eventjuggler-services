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

import java.util.Collections;
import java.util.LinkedList;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.as.web.deployment.WarMetaData;
import org.jboss.logging.Logger;
import org.jboss.metadata.web.spec.FilterMappingMetaData;
import org.jboss.metadata.web.spec.FilterMetaData;
import org.jboss.metadata.web.spec.FiltersMetaData;
import org.jboss.metadata.web.spec.WebMetaData;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoader;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AnalyticsWebFilterProcessor implements DeploymentUnitProcessor {

    private static final Logger log = Logger.getLogger("org.eventjuggler.services.analytics");

    public static final Phase PHASE = Phase.PARSE;

    public static final int PRIORITY = Phase.PARSE_WEB_COMPONENTS - 1;

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        WarMetaData warMetaData = deploymentUnit.getAttachment(WarMetaData.ATTACHMENT_KEY);
        if (warMetaData == null) {
            return;
        }

        addDependency(deploymentUnit);

        FilterMetaData filterMetaData = new FilterMetaData();
        filterMetaData.setFilterClass(org.eventjuggler.services.analytics.web.AnalyticsFilter.class.getName());
        filterMetaData.setFilterName(org.eventjuggler.services.analytics.web.AnalyticsFilter.class.getSimpleName());

        FilterMappingMetaData filterMappingMetaData = new FilterMappingMetaData();
        filterMappingMetaData.setFilterName(org.eventjuggler.services.analytics.web.AnalyticsFilter.class.getSimpleName());
        filterMappingMetaData.setUrlPatterns(Collections.singletonList("/*"));

        if (warMetaData.getWebMetaData() == null) {
            warMetaData.setWebMetaData(new WebMetaData());
        }

        if (warMetaData.getWebMetaData().getFilters() == null) {
            warMetaData.getWebMetaData().setFilters(new FiltersMetaData());
        }
        warMetaData.getWebMetaData().getFilters().add(filterMetaData);

        if (warMetaData.getWebMetaData().getFilterMappings() == null) {
            warMetaData.getWebMetaData().setFilterMappings(new LinkedList<FilterMappingMetaData>());
        }
        warMetaData.getWebMetaData().getFilterMappings().add(filterMappingMetaData);

        log.infov("Enabling analytics filter for {0}", deploymentUnit.getName());
    }

    private void addDependency(DeploymentUnit deploymentUnit) {
        ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
        for (ModuleDependency d : moduleSpecification.getUserDependencies()) {
            if (d.getIdentifier().equals(AnalyticsMarkerProcessor.ANALYTICS_IDENTIFIER)) {
                return;
            }
        }

        ModuleLoader moduleLoader = Module.getBootModuleLoader();
        ModuleDependency moduleDependency = new ModuleDependency(moduleLoader, AnalyticsMarkerProcessor.ANALYTICS_IDENTIFIER,
                false, false, false, false);
        moduleSpecification.addSystemDependency(moduleDependency);
    }

    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
