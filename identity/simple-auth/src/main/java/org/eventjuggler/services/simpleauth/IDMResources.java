package org.eventjuggler.services.simpleauth;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.picketlink.annotations.PicketLink;

public class IDMResources {

    @PicketLink
    @Produces
    @PersistenceContext(unitName = "identity")
    public EntityManager entityManager;

}
