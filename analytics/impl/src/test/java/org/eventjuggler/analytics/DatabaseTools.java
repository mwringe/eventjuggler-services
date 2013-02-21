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
package org.eventjuggler.analytics;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.rules.ExternalResource;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class DatabaseTools extends ExternalResource {

    class ResourceLocaleUserTransaction implements UserTransaction {

        private EntityTransaction tx;

        public ResourceLocaleUserTransaction(EntityManager em) {
            tx = em.getTransaction();
        }

        @Override
        public void begin() throws NotSupportedException, SystemException {
            tx.begin();
        }

        @Override
        public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException,
                IllegalStateException, SystemException {
            tx.commit();
        }

        @Override
        public int getStatus() throws SystemException {
            if (tx.getRollbackOnly()) {
                return Status.STATUS_MARKED_ROLLBACK;
            } else if (tx.isActive()) {
                return Status.STATUS_ACTIVE;
            } else {
                return Status.STATUS_NO_TRANSACTION;
            }
        }

        @Override
        public void rollback() throws IllegalStateException, SecurityException, SystemException {
            tx.rollback();
        }

        @Override
        public void setRollbackOnly() throws IllegalStateException, SystemException {
            tx.setRollbackOnly();
        }

        @Override
        public void setTransactionTimeout(int arg0) throws SystemException {
            throw new UnsupportedOperationException();
        }

    }
    private final EntityManager em;

    private final EntityManagerFactory emf;

    public DatabaseTools(String unitName) {
        emf = Persistence.createEntityManagerFactory(unitName);
        em = emf.createEntityManager();
    }

    @Override
    protected void after() {
        super.after();

        cleanDatabase();
        emf.close();
    }

    public void beginTx() {
        em.getTransaction().begin();
    }

    public void cleanDatabase() {
        inTx(new Runnable() {
            @Override
            public void run() {
                for (EntityType<?> e : em.getMetamodel().getEntities()) {
                    for (Object o : em.createQuery("from " + e.getName()).getResultList()) {
                        em.remove(o);
                    }
                }
            }
        });
    }

    public void commitTx() {
        em.getTransaction().commit();
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }
    
    public EntityManager getEm() {
        return em;
    }

    public UserTransaction getUserTransaction() {
        return new ResourceLocaleUserTransaction(em);
    }

    public void inTx(Runnable runnable) {
        beginTx();
        try {
            runnable.run();
        } finally {
            commitTx();
        }
    }

}
