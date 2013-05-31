package org.eventjuggler.services.idb.pl;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContext;

@ApplicationScoped
public class SimpleAuthConfig {

    private String url;

    private String appKey;

    private String appSecret;

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getUrl() {
        return url;
    }

    public void init(ServletContext context) {
        url = context.getInitParameter("ejs.url");
        appKey = context.getInitParameter("ejs.appKey");
        appSecret = context.getInitParameter("ejs.appSecret");
    }

}
