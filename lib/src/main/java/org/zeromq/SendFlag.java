package org.zeromq;

import java.util.HashMap;
import java.util.Map;

public enum SendFlag {


    WAIT(0),
    DONT_WAIT(1),
    SEND_MORE(2),

    ;

    private final int value;
    private static Map map = new HashMap<>();

    static {
        for (SendFlag pageType : SendFlag.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static SendFlag valueOf(int value) {
        Object type = map.get(value);
        if(type == null){
            throw new RuntimeException("value is not matched: "+ value);
        }
        return (SendFlag)type;
    }

    public int value() {
        return this.value;
    }

    SendFlag(int type) {
        this.value = type;
    }
}
