package org.zeromq;

import java.util.HashMap;
import java.util.Map;

public enum RouterNotifyOption {

    NOTIFY_NONE(0),
    NOTIFY_CONNECT(1),
    NOTIFY_DISCONNECT(2),
    NOTIFY_CONNECT_N_DISCONNECT(3),


    ;
    private final int value;

    public int value() {
        return this.value;
    }

    private static Map map = new HashMap<>();

    static {
        for (RouterNotifyOption pageType : RouterNotifyOption.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static RouterNotifyOption valueOf(int value) {
        Object type = map.get(value);
        if(type == null){
            throw new RuntimeException("value is not matched: "+ value);
        }
        return (RouterNotifyOption)type;
    }

    RouterNotifyOption(int value){
        this.value = value;
    }
}
