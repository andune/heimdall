/**
 *
 */
package com.andune.heimdall.event;


/**
 * Class for debugging an issue with missed events. Temporary only.
 *
 * @author andune
 */
public class EventDebug {
    private static int totalEventNumber = 0;

    private Event event;
    private int eventNumber;

    public EventDebug(Event event) {
        this.event = event;
        this.eventNumber = incrementEventNumber();
    }

    public static synchronized int incrementEventNumber() {
        return totalEventNumber++;
    }

    public Event getEvent() {
        return event;
    }

    public int getEventNumber() {
        return eventNumber;
    }

}
