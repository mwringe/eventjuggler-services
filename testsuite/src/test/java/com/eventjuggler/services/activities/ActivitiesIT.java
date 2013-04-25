package com.eventjuggler.services.activities;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.annotation.Resource;
import javax.naming.InitialContext;

import org.eventjuggler.services.activities.Activities;
import org.eventjuggler.services.activities.Event;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ActivitiesIT {

    private static final String baseUrl = "http://localhost:8080";

    private static final String SAMPLE_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";

    private static void assertTestEvent(long start, long end, Event event) {
        Assert.assertEquals("/ejs-test", event.getContextPath());
        Assert.assertEquals("/ejs-test/hello", event.getPage());
        Assert.assertEquals("GBR", event.getCountry());
        Assert.assertEquals("eng", event.getLanguage());
        Assert.assertEquals(SAMPLE_USER_AGENT, event.getUserAgent());
        Assert.assertTrue(event.getTime() >= start);
        Assert.assertTrue(event.getTime() <= end);
        Assert.assertEquals("127.0.0.1", event.getRemoteAddr());
    }

    @Deployment(name = "ejs-test", order = 2, testable = true)
    public static WebArchive getTestArchive() throws IllegalArgumentException, Exception {
        return Deployments.getTestArchive(ActivitiesIT.class, HelloServlet.class)
                .addAsWebInfResource("activities-web.xml", "web.xml")
                .addAsManifestResource(new StringAsset("Dependencies: deployment.ejs-activities.ear \n"), "MANIFEST.MF")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Resource(lookup = "java:global/ejs/Activities")
    private Activities activities;

    @Test
    @OperateOnDeployment("ejs-test")
    public void cdi() throws Exception {
        final Activities activities = (Activities) new InitialContext().lookup("java:global/ejs/Activities");

        List<Event> existing = activities.createQuery().getResults();

        long start = System.currentTimeMillis();

        invokeHelloServlet();

        long end = System.currentTimeMillis();

        List<Event> events = activities.createQuery().getResults();
        Assert.assertEquals(existing.size() + 1, events.size());

        Event newEvent = null;
        for (Event e : events) {
            if (!existing.contains(e)) {
                newEvent = e;
            }
        }

        assertTestEvent(start, end, newEvent);
    }

    private void invokeHelloServlet() throws Exception {
        URL url = new URL(baseUrl + "/ejs-test/hello");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestProperty("Accept-Language", "en-GB,en");
            connection.setRequestProperty("User-Agent", SAMPLE_USER_AGENT);

            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

}
