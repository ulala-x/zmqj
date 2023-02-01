package org.zeromq;

import java.util.HashMap;
import java.util.Map;

public enum RecvFlag {

    WAIT(0),
    DONT_WAIT(1),
    ;

    private final int value;
    private static Map map = new HashMap<>();

    static {
        for (RecvFlag pageType : RecvFlag.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static RecvFlag valueOf(int value) {
        Object type = map.get(value);
        if(type == null){
            throw new RuntimeException("value is not matched: "+ value);
        }
        return (RecvFlag)type;
    }

    public int value() {
        return this.value;
    }

    RecvFlag(int type) {
        this.value = type;
    }
}
