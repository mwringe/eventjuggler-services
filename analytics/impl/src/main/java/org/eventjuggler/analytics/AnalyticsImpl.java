package org.eventjuggler.analytics;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.inject.Alternative;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.uadetector.UADetectorServiceFactory;
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;

@Alternative
public class AnalyticsImpl implements Analytics {

    private final List<String> acceptedContentTypes = Arrays.asList("text/html", "application/json", "application/xml");

    private final List<Event> events = new LinkedList<Event>();

    @Override
    public synchronized void addEvent(ServletRequest request, ServletResponse response) {
        if (!(request instanceof HttpServletRequest)) {
            return;
        }

        if (response.getContentType() == null || !acceptedContentTypes.contains(response.getContentType().split(";")[0])) {
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getContextPath().startsWith("/analytics-web")) {
            return;
        }

        long time = System.currentTimeMillis();
        String contextPath = httpRequest.getContextPath();
        String page = httpRequest.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        String country = request.getLocale().getISO3Country();
        String language = request.getLocale().getISO3Language();
        String userAgent = httpRequest.getHeader("user-agent");

        Event event = new EventImpl(time, contextPath, page, remoteAddr, country, language, userAgent);
        events.add(event);
    }

    private Statistics generateStatistics(List<Event> events) {
        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

        long totalViews = 0;
        Map<String, Long> pageViews = new HashMap<String, Long>();
        Map<String, Long> userViews = new HashMap<String, Long>();
        Map<String, Long> countryViews = new HashMap<String, Long>();
        Map<String, Long> languageViews = new HashMap<String, Long>();
        Map<String, Long> browserViews = new HashMap<String, Long>();
        Map<String, Long> osViews = new HashMap<String, Long>();

        for (Event e : events) {
            UserAgent agent = parser.parse(e.getUserAgent());

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

        return new StatisticsImpl(totalViews, pageViews, userViews, countryViews, languageViews, browserViews, osViews);
    }

    @Override
    public synchronized List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    public synchronized List<Event> getEvents(String contextPath) {
        List<Event> l = new LinkedList<Event>();
        for (Event e : events) {
            if (e.getContextPath().equals(contextPath)) {
                l.add(e);
            }
        }
        return l;
    }

    private List<Entry<String, Long>> getPopular(List<Entry<String, Long>> l) {
        Collections.sort(l, new SortByPopularityComparator());
        return l.size() < 10 ? l : l.subList(0, 10);
    }

    @Override
    public synchronized List<Entry<String, Long>> getPopularPages() {
        return getPopular(getStatistics().getPageViews());
    }

    @Override
    public synchronized List<Entry<String, Long>> getPopularPages(String contextPath) {
        return getPopular(getStatistics(contextPath).getPageViews());
    }

    private List<Entry<String, Long>> getRelatedPages(List<Event> events, String page) {
        Set<String> usersWithVisit = new HashSet<String>();
        for (Event e : events) {
            if (e.getPage().equals(page)) {
                usersWithVisit.add(e.getRemoteAddr());
            }
        }

        List<Event> eventsForUsersWithVisit = new LinkedList<Event>();
        for (Event e : events) {
            if (usersWithVisit.contains(e.getRemoteAddr())) {
                eventsForUsersWithVisit.add(e);
            }
        }

        return getPopular(generateStatistics(eventsForUsersWithVisit).getPageViews());
    }

    @Override
    public synchronized List<Entry<String, Long>> getRelatedPages(String page) {
        return getRelatedPages(getEvents(), page);
    }

    @Override
    public synchronized List<Entry<String, Long>> getRelatedPages(String contextPath, String page) {
        return getRelatedPages(getEvents(contextPath), page);
    }

    @Override
    public synchronized Statistics getStatistics() {
        return generateStatistics(getEvents());
    }

    @Override
    public synchronized Statistics getStatistics(String contextPath) {
        return generateStatistics(getEvents(contextPath));
    }

    private void increment(String label, Map<String, Long> map) {
        if (label == null) {
            label = "unknown";
        }

        if (map.containsKey(label)) {
            map.put(label, map.get(label) + 1);
        } else {
            map.put(label, 1L);
        }
    }

}
