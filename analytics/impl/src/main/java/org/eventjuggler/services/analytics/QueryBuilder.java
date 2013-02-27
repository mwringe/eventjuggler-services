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
package org.eventjuggler.services.analytics;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class QueryBuilder {

    private final CriteriaBuilder builder;
    private final CriteriaQuery<?> criteria;
    private final EntityManager em;
    private int firstResult;
    private int maxResult;
    private final List<Predicate> predicates;
    private final Root<?> root;

    public QueryBuilder(EntityManager em, Class<?> rootClass) {
        this.em = em;
        builder = em.getCriteriaBuilder();
        criteria = builder.createQuery(EventImpl.class);
        root = criteria.from(rootClass);
        predicates = new LinkedList<Predicate>();
    }

    public void addPredicate(Predicate predicate) {
        predicates.add(predicate);
    }

    public CriteriaBuilder getBuilder() {
        return builder;
    }

    @SuppressWarnings("unchecked")
    public <T> CriteriaQuery<T> getCriteria() {
        return (CriteriaQuery<T>) criteria;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getResults() {
        criteria.where(predicates.toArray(new Predicate[predicates.size()]));

        TypedQuery<T> q = (TypedQuery<T>) em.createQuery(criteria);

        if (firstResult != -1) {
            q.setFirstResult(firstResult);
        }

        if (maxResult != -1) {
            q.setMaxResults(maxResult);
        }

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> Root<T> getRoot() {
        return (Root<T>) root;
    }

    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

}
