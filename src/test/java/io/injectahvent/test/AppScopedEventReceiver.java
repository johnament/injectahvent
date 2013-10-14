package io.injectahvent.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class AppScopedEventReceiver {
    private int receivedMessages;
    private int receivedReqScoped;

    public void receiveEvent(@Observes Object object) {
        this.receivedMessages++;
    }

    public int getReceivedMessages() {
        return this.receivedMessages;
    }

    public void incrementReqScoped() {
        this.receivedReqScoped++;
    }

    public int getReceivedReqScoped() {
        return receivedReqScoped;
    }
}
