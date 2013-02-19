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

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class StatisticsImpl implements Serializable, Statistics {

    private final Map<String, Long> browserViews;
    private final Map<String, Long> countryViews;
    private final Map<String, Long> languageViews;
    private final Map<String, Long> osViews;
    private final Map<String, Long> pageViews;
    private final long totalViews;
    private final Map<String, Long> userViews;

    public StatisticsImpl(long totalViews, Map<String, Long> pageViews, Map<String, Long> userViews,
            Map<String, Long> countryViews, Map<String, Long> languageViews, Map<String, Long> browserViews,
            Map<String, Long> osViews) {
        this.pageViews = pageViews;
        this.userViews = userViews;
        this.totalViews = totalViews;
        this.countryViews = countryViews;
        this.languageViews = languageViews;
        this.browserViews = browserViews;
        this.osViews = osViews;
    }

    @Override
    public List<Entry<String, Long>> getBrowserViews() {
        LinkedList<Entry<String, Long>> l = new LinkedList<Map.Entry<String, Long>>(browserViews.entrySet());
        Collections.sort(l, new SortByPopularityComparator());
        return l;
    }

    @Override
    public List<Entry<String, Long>> getCountryViews() {
        LinkedList<Entry<String, Long>> l = new LinkedList<Map.Entry<String, Long>>(countryViews.entrySet());
        Collections.sort(l, new SortByPopularityComparator());
        return l;
    }

    @Override
    public List<Entry<String, Long>> getLanguageViews() {
        LinkedList<Entry<String, Long>> l = new LinkedList<Map.Entry<String, Long>>(languageViews.entrySet());
        Collections.sort(l, new SortByPopularityComparator());
        return l;
    }

    @Override
    public List<Entry<String, Long>> getOsViews() {
        LinkedList<Entry<String, Long>> l = new LinkedList<Map.Entry<String, Long>>(osViews.entrySet());
        Collections.sort(l, new SortByPopularityComparator());
        return l;
    }

    @Override
    public List<Entry<String, Long>> getPageViews() {
        LinkedList<Entry<String, Long>> l = new LinkedList<Map.Entry<String, Long>>(pageViews.entrySet());
        Collections.sort(l, new SortByPopularityComparator());
        return l;
    }

    @Override
    public long getTotalViews() {
        return totalViews;
    }

    @Override
    public List<Entry<String, Long>> getUserViews() {
        LinkedList<Entry<String, Long>> l = new LinkedList<Map.Entry<String, Long>>(userViews.entrySet());
        Collections.sort(l, new SortByPopularityComparator());
        return l;
    }

}
