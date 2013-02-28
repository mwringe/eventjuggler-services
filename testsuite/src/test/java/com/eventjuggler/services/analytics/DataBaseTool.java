package com.eventjuggler.services.analytics;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

@Stateless
public class DataBaseTool {

    private static final Logger log = Logger.getLogger(DataBaseTool.class);

    @PersistenceContext(unitName = "analytics")
    private EntityManager em;

    public void clearEvents() {
        int update = em.createQuery("delete from EventImpl").executeUpdate();
        log.infof("Deleted %s event(s) from database", update);
    }

}
