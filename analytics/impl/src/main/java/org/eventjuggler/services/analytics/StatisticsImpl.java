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

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.sf.uadetector.UADetectorServiceFactory;
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class StatisticsImpl implements Serializable, Statistics {

    class EntryImpl implements Entry {

        private long count;
        private String label;

        @Override
        public long getCount() {
            return count;
        }

        @Override
        public String getLabel() {
            return label;
        }

    }

    class SortByPopularityComparator implements Comparator<Entry> {

        @Override
        public int compare(Entry o1, Entry o2) {
            long c1 = o1.getCount();
            long c2 = o2.getCount();
            if (c1 == c2) {
                return 0;
            }
            return c1 < c2 ? 1 : -1;
        }

    }

    private final List<Statistics.Entry> browserViews;
    private final List<Statistics.Entry> countryViews;
    private final List<Statistics.Entry> languageViews;
    private final List<Statistics.Entry> osViews;
    private final List<Statistics.Entry> pageViews;

    private long totalViews;

    private final List<Statistics.Entry> userViews;

    public StatisticsImpl(List<Event> events) {
        this.pageViews = new LinkedList<>();
        this.userViews = new LinkedList<>();
        this.totalViews = 0;
        this.countryViews = new LinkedList<>();
        this.languageViews = new LinkedList<>();
        this.browserViews = new LinkedList<>();
        this.osViews = new LinkedList<>();

        UserAgentStringParser userAgentParser = UADetectorServiceFactory.getResourceModuleParser();

        for (Event e : events) {
            UserAgent agent = userAgentParser.parse(e.getUserAgent());

            String browser = agent.getName();
            String os = agent.getOperatingSystem().getName();

            totalViews++;

            increment(e.getPage(), pageViews);
            increment(e.getRemoteAddr(), userViews);
            increment(e.getCountry(), countryViews);
            increment(e.getLanguage(), languageViews);
            increment(browser, browserViews);
            increment(os, osViews);
        }

        SortByPopularityComparator comparator = new SortByPopularityComparator();
        Collections.sort(pageViews, comparator);
        Collections.sort(userViews, comparator);
        Collections.sort(countryViews, comparator);
        Collections.sort(languageViews, comparator);
        Collections.sort(browserViews, comparator);
        Collections.sort(osViews, comparator);
    }

    @Override
    public List<Entry> getBrowserViews() {
        return browserViews;
    }

    @Override
    public List<Entry> getCountryViews() {
        return countryViews;
    }

    @Override
    public List<Entry> getLanguageViews() {
        return languageViews;
    }

    @Override
    public List<Entry> getOsViews() {
        return osViews;
    }

    @Override
    public List<Entry> getPageViews() {
        return pageViews;
    }

    @Override
    public long getTotalViews() {
        return totalViews;
    }

    @Override
    public List<Entry> getUserViews() {
        return userViews;
    }

    private void increment(String label, List<Statistics.Entry> list) {
        if (label == null) {
            label = "unknown";
        }

        for (Statistics.Entry e : list) {
            if (e.getLabel().equals(label)) {
                ((EntryImpl) e).count++;
                return;
            }
        }

        EntryImpl e = new EntryImpl();
        e.label = label;
        e.count = 1;
        list.add(e);
    }

}
