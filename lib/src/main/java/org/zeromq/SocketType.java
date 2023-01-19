package org.zeromq;

public enum SocketType {
    Pair(0),
    Pub(1),
    Sub(2),
    Req(3),
    Rep(4),
    Dealer(5),
    Router(6),
    Pull(7),
    Push(8),
    XPub(9),
    XSub(10),
    Stream(11);

    private final int number;

    public int number() {
        return this.number;
    }

    private SocketType(int type) {
        this.number = type;
    }
}
