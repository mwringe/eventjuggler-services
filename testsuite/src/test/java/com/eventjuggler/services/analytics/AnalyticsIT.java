package com.eventjuggler.services.analytics;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eventjuggler.services.analytics.Analytics;
import org.eventjuggler.services.analytics.Event;
import org.eventjuggler.services.analytics.EventImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.util.Base64;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AnalyticsIT {

    private static final String baseUrl = "http://localhost:8080";

    private static final String SAMPLE_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";

    public static void assertEvent(String contextPath, String page, Event event) {
        Assert.assertEquals(contextPath, event.getContextPath());
        Assert.assertEquals(contextPath, event.getPage());
    }

    public static void assertTestEvent(long start, long end, Event event) {
        Assert.assertEquals("/analytics-test", event.getContextPath());
        Assert.assertEquals("/analytics-test/hello", event.getPage());
        Assert.assertEquals("GBR", event.getCountry());
        Assert.assertEquals("eng", event.getLanguage());
        Assert.assertEquals(SAMPLE_USER_AGENT, event.getUserAgent());
        Assert.assertTrue(event.getTime() >= start);
        Assert.assertTrue(event.getTime() <= end);
        Assert.assertEquals("127.0.0.1", event.getRemoteAddr());
    }

    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "analytics-test.war").addClasses(AnalyticsIT.class,
                DataBaseTool.class, DataBaseToolServlet.class, HelloServlet.class);
        archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        archive.addAsManifestResource("analytics-jboss-deployment-structure.xml", "jboss-deployment-structure.xml");
        archive.addAsResource("analytics-persistence.xml", "META-INF/persistence.xml");
        return archive;
    }

    @Inject
    private Instance<Analytics> analyticsInstance;

    @Inject
    private DataBaseTool dataBaseTool;

    @Inject
    private UserTransaction tx;

    @After
    public void after() throws Exception {
        if (dataBaseTool != null) {
            dataBaseTool.clearEvents();
        } else {
            URL url = new URL(baseUrl + "/analytics-test/clear");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            connection.disconnect();
        }
    }

    @Test
    public void cdi() throws Exception {
        Assert.assertFalse(analyticsInstance.isUnsatisfied());

        final Analytics analytics = analyticsInstance.get();

        Assert.assertTrue(analytics.createQuery().getResults().isEmpty());

        long start = System.currentTimeMillis();

        invokeHelloServlet();

        retry(new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(1, analytics.createQuery().getResults().size());
            }
        }, 10000);

        long end = System.currentTimeMillis();

        List<Event> events = analytics.createQuery().getResults();
        Assert.assertEquals(1, events.size());
        assertTestEvent(start, end, events.get(0));
    }

    private List<Event> getRestEvents() throws Exception {
        URL url = new URL(baseUrl + "/analytics/rest/events");
        String credentials = Base64.encodeBytes("admin:password".getBytes());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestProperty("Authorization", "Basic " + credentials);

            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(IOUtils.toString(connection.getInputStream()), new TypeReference<List<EventImpl>>() {
            });
        } finally {
            connection.disconnect();
        }
    }

    private void invokeHelloServlet() throws Exception {
        URL url = new URL(baseUrl + "/analytics-test/hello");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestProperty("Accept-Language", "en-GB,en");
            connection.setRequestProperty("User-Agent", SAMPLE_USER_AGENT);

            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());

            String result = IOUtils.toString(connection.getInputStream());
            Assert.assertEquals("Hello World!", result);
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void lookupJndi() throws Exception {
        InitialContext ctx = new InitialContext();
        Object o = ctx.lookup("java:jboss/AnalyticsService");
        Assert.assertNotNull(o);
        Assert.assertTrue(o instanceof Analytics);

        final Analytics analytics = (Analytics) o;

        Assert.assertTrue(analytics.createQuery().getResults().isEmpty());

        long start = System.currentTimeMillis();

        invokeHelloServlet();

        retry(new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(1, analytics.createQuery().getResults().size());
            }
        }, 10000);

        long end = System.currentTimeMillis();

        List<Event> events = analytics.createQuery().getResults();
        Assert.assertEquals(1, events.size());
        assertTestEvent(start, end, events.get(0));
    }

    @Test
    @RunAsClient
    public void rest() throws Exception {
        Assert.assertTrue(getRestEvents().isEmpty());

        long start = System.currentTimeMillis();

        invokeHelloServlet();

        retry(new Runnable() {
            @Override
            public void run() {
                try {
                    Assert.assertEquals(1, getRestEvents().size());
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
            }
        }, 10000);

        long end = System.currentTimeMillis();

        List<Event> events = getRestEvents();
        Assert.assertEquals(1, events.size());
        assertTestEvent(start, end, events.get(0));
    }

    @Test
    @RunAsClient
    public void restUnauthenticated() throws Exception {
        URL url = new URL(baseUrl + "/analytics/rest/events");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            Assert.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    private void retry(Runnable runnable, long timeout) throws InterruptedException {
        long end = System.currentTimeMillis() + timeout;
        AssertionError error = null;
        while (error == null || System.currentTimeMillis() < end) {
            try {
                runnable.run();
                return;
            } catch (AssertionError e) {
                error = e;
                Thread.sleep(1000);
            }
        }
        throw error;
    }

    @Test
    @RunAsClient
    public void web() throws Exception {
        URL url = new URL(baseUrl + "/analytics");
        String credentials = Base64.encodeBytes("admin:password".getBytes());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());

            String result = IOUtils.toString(connection.getInputStream());
            result.contains("<h2>Total</h2>");
            result.contains("<h2>Popular</h2>");
            result.contains("<h2>Statistics</h2>");
        } finally {
            connection.disconnect();
        }
    }

    @Test
    @RunAsClient
    public void webUnauthenticated() throws Exception {
        URL url = new URL(baseUrl + "/analytics");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            Assert.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

}
