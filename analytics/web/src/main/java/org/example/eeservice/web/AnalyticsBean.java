package org.example.eeservice.web;

import java.util.List;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.eventjuggler.analytics.Analytics;
import org.eventjuggler.analytics.Statistics;

@ManagedBean
@SessionScoped
public class AnalyticsBean {

    @Inject
    private Analytics analytics;

    private String page;

    public String getPage() {
        if (page == null) {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest();
            page = request.getRequestURI();
        }
        return page;
    }

    public List<Entry<String, Long>> getPopular() {
        return analytics.getPopularPages();
    }

    public List<Entry<String, Long>> getRelated() {
        return analytics.getRelatedPages(page);
    }

    public Statistics getStatistics() {
        return analytics.getStatistics();
    }

    public void setPage(String page) {
        this.page = page;
    }

}
