package org.eventjuggler.analytics;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

public class AnalyticsProducer {

    @ApplicationScoped
    @Default
    @Produces
    public Analytics createAnalytics() {
        return new AnalyticsImpl();
    }

}
