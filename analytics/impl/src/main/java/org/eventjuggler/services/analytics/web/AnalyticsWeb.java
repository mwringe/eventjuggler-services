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
package org.eventjuggler.services.analytics.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eventjuggler.services.analytics.Analytics;
import org.eventjuggler.services.analytics.Statistics;
import org.eventjuggler.services.analytics.Statistics.Entry;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AnalyticsWeb extends HttpServlet {

    private static final String H2 = "<h2>%s</h2>%n";

    private static final String H3 = "<h3>%s</h3>%n";

    private static final String TR_HEADER_2COL = "<tr><th>%s</th><th>%s</th></tr>%n";

    private static final String TR_2COL = "<tr><td>%s</td><td>%s</td></tr>%n";

    private static final String TR_HEADER_1COL = "<tr><th>%s</th></tr>%n";

    private static final String TR_1COL = "<tr><td>%s</td></tr>%n";

    private Analytics analytics;

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            analytics = (Analytics) new InitialContext().lookup("java:jboss/AnalyticsService");
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        Statistics statistics = analytics.createQuery().getStatistics();

        pw.println("<html>");
        pw.println("<body>");

        pw.printf(H2, "Total");
        pw.printf("<p>%s</p>", statistics.getTotalViews());

        pw.printf(H2, "Popular");
        pw.println("<table>");
        pw.printf(TR_HEADER_1COL, "Page");
        for (String p : analytics.createQuery().getPopularPages()) {
            pw.printf(TR_1COL, p);
        }
        pw.println("</table>");

        pw.printf(H2, "Statistics");

        printTable("Page", statistics.getPageViews(), pw);
        printTable("Country", statistics.getCountryViews(), pw);
        printTable("Language", statistics.getLanguageViews(), pw);
        printTable("User", statistics.getUserViews(), pw);
        printTable("Browser", statistics.getBrowserViews(), pw);
        printTable("OS", statistics.getOsViews(), pw);

        pw.println("</body>");
        pw.println("</html>");
    }

    private void printTable(String header, List<Entry> entries, PrintWriter pw) {
        pw.printf(H3, header);
        pw.println("<table>");
        pw.printf(TR_HEADER_2COL, "Page", "Views");
        for (Entry e : entries) {
            pw.printf(TR_2COL, e.getLabel(), e.getCount());
        }
        pw.println("</table>");
    }

}
