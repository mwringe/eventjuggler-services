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
package org.eventjuggler.services.idb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.eventjuggler.services.idb.model.Realm;
import org.eventjuggler.services.idb.utils.KeyGenerator;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.config.IdentityConfiguration;
import org.picketlink.idm.config.IdentityConfigurationBuilder;
import org.picketlink.idm.config.JPAStoreConfigurationBuilder;
import org.picketlink.idm.internal.IdentityManagerFactory;
import org.picketlink.idm.jpa.internal.JPAContextInitializer;
import org.picketlink.idm.jpa.schema.CredentialObject;
import org.picketlink.idm.jpa.schema.CredentialObjectAttribute;
import org.picketlink.idm.jpa.schema.IdentityObject;
import org.picketlink.idm.jpa.schema.IdentityObjectAttribute;
import org.picketlink.idm.jpa.schema.PartitionObject;
import org.picketlink.idm.jpa.schema.RelationshipIdentityObject;
import org.picketlink.idm.jpa.schema.RelationshipObject;
import org.picketlink.idm.jpa.schema.RelationshipObjectAttribute;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@EJB(name = "java:global/ejs/IdentityManagerRegistry", beanInterface = IdentityManagerRegistry.class)
public class IdentityManagerRegistryBean implements IdentityManagerRegistry {

    @PersistenceContext(unitName = "idb")
    private EntityManager em;

    @PersistenceUnit(unitName = "idb")
    private EntityManagerFactory emf;

    private IdentityManagerFactory imf;

    @Override
    public boolean containsRealm(String name) {
        return em.find(Realm.class, name) != null;
    }

    @Override
    @Lock(LockType.READ)
    public IdentityManager createIdentityManager(String realm) {
        return imf.createIdentityManager(imf.getRealm(realm));
    }

    @Override
    @Lock(LockType.WRITE)
    public void createRealm(Realm realm) {
        if (realm.getKey() == null) {
            realm.setKey(KeyGenerator.createRealmKey());
        }

        em.persist(realm);

        recreateIdentityManagerFactory();
    }

    @Override
    public Realm updateRealm(Realm realm) {
        return em.merge(realm);
    }

    @Override
    @Lock(LockType.WRITE)
    public void deleteRealm(String name) {
        Realm realm = em.find(Realm.class, name);
        em.remove(realm);

        recreateIdentityManagerFactory();
    }

    @Override
    public Realm getRealm(String name) {
        return em.find(Realm.class, name);
    }

    @Override
    public List<Realm> getRealms() {
        return em.createQuery("from Realm", Realm.class).getResultList();
    }

    @Override
    public List<Realm> getRealms(String username) {
        return em.createQuery("from Realm r where r.owner = :owner", Realm.class).setParameter("owner", username)
                .getResultList();
    }

    @PostConstruct
    public void init() {
        recreateIdentityManagerFactory();
    }

    private void recreateIdentityManagerFactory() {
        List<Realm> realms = em.createQuery("from Realm", Realm.class).getResultList();

        IdentityConfigurationBuilder builder = new IdentityConfigurationBuilder();

        JPAStoreConfigurationBuilder jpaBuilder = builder.stores().jpa();
        jpaBuilder.addContextInitializer(new JPAContextInitializer(emf));

        jpaBuilder.identityClass(IdentityObject.class);
        jpaBuilder.partitionClass(PartitionObject.class);
        jpaBuilder.relationshipClass(RelationshipObject.class);
        jpaBuilder.relationshipIdentityClass(RelationshipIdentityObject.class);
        jpaBuilder.relationshipAttributeClass(RelationshipObjectAttribute.class);
        jpaBuilder.attributeClass(IdentityObjectAttribute.class);
        jpaBuilder.credentialClass(CredentialObject.class);
        jpaBuilder.credentialAttributeClass(CredentialObjectAttribute.class);

        jpaBuilder.supportAllFeatures();
        for (Realm r : realms) {
            jpaBuilder.addRealm(r.getKey());
        }

        IdentityConfiguration configuration = builder.build();
        imf = new IdentityManagerFactory(configuration);
    }

}
