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
package org.eventjuggler.services.activities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ActivitiesQueryImpl implements ActivitiesQuery {

    private final QueryBuilder qb;

    public ActivitiesQueryImpl(EntityManager em) {
        qb = new QueryBuilder(em, EventImpl.class);
    }

    @Override
    public ActivitiesQuery after(long after) {
        Root<EventImpl> root = qb.getRoot();
        qb.addPredicate(qb.getBuilder().ge(root.get(EventImpl_.time), after));
        return this;
    }

    @Override
    public ActivitiesQuery before(long before) {
        Root<EventImpl> root = qb.getRoot();
        qb.addPredicate(qb.getBuilder().le(root.get(EventImpl_.time), before));
        return this;
    }

    @Override
    public ActivitiesQuery contextPath(String contextPath) {
        Root<EventImpl> root = qb.getRoot();
        qb.addPredicate(qb.getBuilder().equal(root.get(EventImpl_.contextPath), contextPath));
        return this;
    }

    @Override
    public ActivitiesQuery firstResult(int firstResult) {
        qb.setFirstResult(firstResult);
        return this;
    }

    @Override
    public List<String> getPopularPages() {
        CriteriaBuilder builder = qb.getBuilder();
        CriteriaQuery<String> criteria = qb.getCriteria();
        Root<EventImpl> root = qb.getRoot();

        criteria.groupBy(root.get(EventImpl_.contextPath), root.get(EventImpl_.page));
        criteria.orderBy(builder.desc(builder.count(root.get(EventImpl_.page))));
        criteria.select(root.get(EventImpl_.page));

        return qb.getResults();
    }

    @Override
    public List<String> getRelatedPages(String page) {
        CriteriaBuilder builder = qb.getBuilder();
        CriteriaQuery<String> criteria = qb.getCriteria();
        Root<EventImpl> root = qb.getRoot();

        Subquery<String> subquery = criteria.subquery(String.class);
        Root<EventImpl> subEventRoot = subquery.from(EventImpl.class);
        subquery.where(builder.equal(subEventRoot.get(EventImpl_.page), page));
        subquery.select(subEventRoot.get(EventImpl_.remoteAddr));
        subquery.distinct(true);

        qb.addPredicate(builder.and(builder.notEqual(root.get(EventImpl_.page), page),
                root.get(EventImpl_.remoteAddr).in(subquery)));

        criteria.groupBy(root.get(EventImpl_.contextPath), root.get(EventImpl_.page));
        criteria.orderBy(builder.desc(builder.count(root.get(EventImpl_.page))));
        criteria.select(root.get(EventImpl_.page));

        return qb.getResults();
    }

    @Override
    public List<Event> getResults() {
        CriteriaBuilder builder = qb.getBuilder();
        CriteriaQuery<String> criteria = qb.getCriteria();
        Root<EventImpl> root = qb.getRoot();

        criteria.orderBy(builder.desc(root.get(EventImpl_.time)));

        return qb.getResults();
    }

    @Override
    public Statistics getStatistics() {
        List<Event> events = qb.getResults();
        return new StatisticsImpl(events);
    }

    @Override
    public ActivitiesQuery maxResult(int maxResult) {
        qb.setMaxResult(maxResult);
        return this;
    }

    @Override
    public ActivitiesQuery page(String page) {
        Root<EventImpl> root = qb.getRoot();
        qb.addPredicate(qb.getBuilder().like(root.get(EventImpl_.page), page));
        return this;
    }

}
