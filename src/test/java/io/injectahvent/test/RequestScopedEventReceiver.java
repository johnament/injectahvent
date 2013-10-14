package io.injectahvent.test;

import org.junit.Assert;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@RequestScoped
public class RequestScopedEventReceiver {
    @Inject
    private AppScopedEventReceiver aser;

    public void receiveString(@Observes @Sendable String str) {
        Assert.assertEquals("ralph", str);
        aser.incrementReqScoped();
    }
}
