package org.zeromq;

public enum SocketEvent {
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
    EVENT_ALL(EVENT_CONNECTED.number | EVENT_CONNECT_DELAYED.number | EVENT_CONNECT_RETRIED.number | EVENT_LISTENING.number | EVENT_BIND_FAILED.number | EVENT_ACCEPTED.number | EVENT_ACCEPT_FAILED.number | EVENT_CLOSED.number | EVENT_CLOSE_FAILED.number | EVENT_DISCONNECTED.number | EVENT_MONITOR_STOPPED.number);

    private final int number;

    public int number() {
        return this.number;
    }

    private SocketEvent(int eventNumber) {
        this.number = eventNumber;
    }
}
