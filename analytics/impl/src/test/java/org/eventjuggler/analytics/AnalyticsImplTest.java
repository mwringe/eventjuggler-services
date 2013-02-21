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

import java.util.List;
import java.util.Locale;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * TODO Fix test - it doesn't work at the moment due to em.joinTransaction obviously failing when we don't have a transaction
 * manager
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Ignore
public class AnalyticsImplTest {

    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.70 Safari/537.17";

    private AnalyticsImpl analytics;

    @Rule
    public DatabaseTools db = new DatabaseTools("analytics-test");

    private HttpServletRequest request;

    private ServletResponse response;

    @Test
    public void addEvent() {
        setupRequest("text/html", "/contextPath", "/contextPath/page", "127.0.0.1", Locale.ENGLISH, USER_AGENT);
        addEventInTx();
    }

    private void addEventInTx() {
        db.inTx(new Runnable() {
            @Override
            public void run() {
                analytics.addEvent(request, response);
            }
        });
    }

    @Before
    public void before() {
        analytics = new AnalyticsImpl(db.getEmf(), db.getUserTransaction());
    }

    @Test
    public void list() throws InterruptedException {
        addEvent();

        Thread.sleep(1000);

        List<? extends Event> results = analytics.createQuery().getResults();
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void popular() {
        setupRequest("text/html", "/contextPath", "/contextPath/page", "127.0.0.1", Locale.ENGLISH, USER_AGENT);
        addEventInTx();

        setupRequest("text/html", "/contextPath", "/contextPath/page", "127.0.0.1", Locale.ENGLISH, USER_AGENT);
        addEventInTx();

        setupRequest("text/html", "/contextPath", "/contextPath/page2", "127.0.0.1", Locale.ENGLISH, USER_AGENT);
        addEventInTx();

        List<String> popularPages = analytics.createQuery().maxResult(1).getPopularPages();
        Assert.assertEquals(1, popularPages.size());
        Assert.assertEquals("/contextPath/page", popularPages.get(0));
    }

    @Test
    public void related() {
        setupRequest("text/html", "/contextPath", "/contextPath/page", "127.0.0.1", Locale.ENGLISH, USER_AGENT);
        addEventInTx();

        setupRequest("text/html", "/contextPath", "/contextPath/page2", "127.0.0.1", Locale.ENGLISH, USER_AGENT);
        addEventInTx();

        setupRequest("text/html", "/contextPath", "/contextPath/page3", "127.0.0.2", Locale.ENGLISH, USER_AGENT);
        addEventInTx();

        setupRequest("text/html", "/contextPath2", "/contextPath2/page", "127.0.0.1", Locale.ENGLISH, USER_AGENT);
        addEventInTx();

        List<String> popularPages = analytics.createQuery().contextPath("/contextPath").getRelatedPages("/contextPath/page");

        Assert.assertEquals(1, popularPages.size());
        Assert.assertEquals("/contextPath/page2", popularPages.get(0));
    }

    private void setupRequest(String contextType, String contextPath, String page, String remoteAddr, Locale locale,
            String userAgent) {
        request = EasyMock.createMock(HttpServletRequest.class);
        response = EasyMock.createMock(ServletResponse.class);

        EasyMock.expect(response.getContentType()).andReturn(contextType);
        EasyMock.expect(request.getContextPath()).andReturn(contextPath);
        EasyMock.expect(request.getRequestURI()).andReturn(page);
        EasyMock.expect(request.getLocale()).andReturn(locale).times(2);
        EasyMock.expect(request.getRemoteAddr()).andReturn(remoteAddr);
        EasyMock.expect(request.getHeader("user-agent")).andReturn(userAgent);
        EasyMock.expect(request.getHeader("x-forwarded-for")).andReturn(null);

        EasyMock.replay(request, response);
    }

}
