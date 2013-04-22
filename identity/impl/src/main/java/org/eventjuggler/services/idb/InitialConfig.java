package org.eventjuggler.services.idb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.utils.KeyGenerator;
import org.jboss.logging.Logger;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.IdentityManagerFactory;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

@Singleton
@Startup
public class InitialConfig {

    private static final String ROOT_USERNAME = "root";

    public static final String APPLICATION_NAME = "system";

    public static final String APPLICATION_KEY = "system";

    public static final String APPLICATION_SECRET = "system";

    @PersistenceContext(unitName = "idb")
    private EntityManager em;

    @Resource(lookup = "java:/picketlink/ExampleIMF")
    private IdentityManagerFactory imf;

    private final Logger log = Logger.getLogger(InitialConfig.class);

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void init() {
        Application application = em.find(Application.class, APPLICATION_NAME);
        if (application == null) {
            application = new Application();
            application.setName(APPLICATION_NAME);
            application.setKey(APPLICATION_KEY);
            application.setSecret(KeyGenerator.createApplicationSecret());
            application.setOwner(ROOT_USERNAME);
            application.setCallbackUrl("/ejs-admin/#");

            em.persist(application);

            log.info("Created system application");
        }

        IdentityManager im = imf.createIdentityManager();

        User root = im.getUser(ROOT_USERNAME);
        if (root == null) {
            root = new SimpleUser(ROOT_USERNAME);
            im.add(root);
            im.updateCredential(root, new Password(ROOT_USERNAME));

            log.info("Created root user");
        }
    }

}
