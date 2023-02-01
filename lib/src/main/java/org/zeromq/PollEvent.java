package org.zeromq;

import java.util.HashMap;
import java.util.Map;

public enum PollEvent {
    POLLIN(1),
    POLLOUT(2),
    POLLERR(4),

    ALL(1|2|3);

    ;
    private final int value;
    private static Map map = new HashMap<>();

    static {
        for (PollEvent pageType : PollEvent.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static PollEvent valueOf(int value) {
        Object type = map.get(value);
        if(type == null){
            throw new RuntimeException("value is not matched: "+ value);
        }
        return (PollEvent)type;
    }

    public int value() {
        return this.value;
    }

    PollEvent(int type) {
        this.value = type;
    }
}