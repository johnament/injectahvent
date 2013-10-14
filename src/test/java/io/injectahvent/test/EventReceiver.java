package io.injectahvent.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class EventReceiver {
    private int receivedMessages;

    public void receiveEvent(@Observes Object object) {
        this.receivedMessages++;
    }

    public int getReceivedMessages() {
        return this.receivedMessages;
    }
}
