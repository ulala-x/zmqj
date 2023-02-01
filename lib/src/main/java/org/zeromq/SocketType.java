package org.zeromq;

import java.util.HashMap;
import java.util.Map;

public enum SocketType {
    PAIR(0),
    PUB(1),
    SUB(2),
    REQ(3),
    REP(4),
    DEALER(5),
    ROUTER(6),
    PULL(7),
    PUSH(8),
    XPUB(9),
    XSUB(10),
    STREAM(11),

    /*  DRAFT Socket types. */
    SERVER(12),
    CLIENT(13),
    RADIO(14),
    DISH(15),
    GATHER(16),
    SCATTER(17),
    DGRAM(18),
    PEER(19),
    CHANNEL(20),

    ;

    private final int value;
    private static Map map = new HashMap<>();

    static {
        for (SocketType pageType : SocketType.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static SocketType valueOf(int value) {
        Object type = map.get(value);
        if(type == null){
            throw new RuntimeException("value is not matched: "+ value);
        }
        return (SocketType)type;
    }

    public int value() {
        return this.value;
    }

    SocketType(int type) {
        this.value = type;
    }
}
