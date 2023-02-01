package org.zeromq;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    NONE(0),
    EVENT_CONNECTED(1),
    EVENT_CONNECT_DELAYED(2),
    EVENT_CONNECT_RETRIED(4),
    EVENT_LISTENING(8),
    EVENT_BIND_FAILED(16),
    EVENT_ACCEPTED(32),
    EVENT_ACCEPT_FAILED(64),
    EVENT_CLOSED(128),
    EVENT_CLOSE_FAILED(256),
    EVENT_DISCONNECTED(512),
    EVENT_MONITOR_STOPPED(1024),
    EVENT_ALL(EVENT_CONNECTED.value | EVENT_CONNECT_DELAYED.value | EVENT_CONNECT_RETRIED.value | EVENT_LISTENING.value | EVENT_BIND_FAILED.value | EVENT_ACCEPTED.value | EVENT_ACCEPT_FAILED.value | EVENT_CLOSED.value | EVENT_CLOSE_FAILED.value | EVENT_DISCONNECTED.value | EVENT_MONITOR_STOPPED.value);

    private final int value;

    public int value() {
        return this.value;
    }

    private static Map map = new HashMap<>();

    static {
        for (EventType pageType : EventType.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static EventType valueOf(int value) {
        Object type = map.get(value);
        if(type == null){
            throw new RuntimeException("value is not matched: "+ value);
        }
        return (EventType)type;
    }


    private EventType(int eventNumber) {
        this.value = eventNumber;
    }
}
