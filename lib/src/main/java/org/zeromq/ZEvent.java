package org.zeromq;

public class ZEvent {

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY){
            System.loadLibrary("libzmqj");
        }
        _nativeInit();
    }

    private static native void _nativeInit();


    private final int event;
    private final Object value;
    private final String address;

    private ZEvent(int event, int value, String address) {
        this(event, Integer.valueOf(value), address != null ? address : "");
    }

    public ZEvent(int event, Object value, String address) {
        this.event = event;
        this.value = value;
        this.address = address;
    }

    public EventType getEvent() {
        return EventType.valueOf(event);
    }

    public Object getValue() {
        return value;
    }

    /**
     * Get the address.
     * For libzmq versions 3.2.x the address will be an empty string.
     * @return
     */
    public String getAddress() {
        return address;
    }

    private static native ZEvent _recv(long socket, int flags) throws ZMQException;

    /**
     * Receive an event from a monitor socket.
     * @param socket the socket
     * @param flags the flags to apply to the receive operation.
     * @return the received event or null if no message was received.
     * @throws ZMQException
     */
    public static ZEvent recv(ZSocket socket, RecvFlag flags) throws ZMQException {
        return ZEvent._recv(socket.getSocketHandle(), flags.value());
    }

    /**
     * Receive an event from a monitor socket.
     * Does a blocking recv.
     * @param socket the socket
     * @return the received event.
     * @throws ZMQException
     */
    public static ZEvent recv(ZSocket socket) throws ZMQException {
        return ZEvent.recv(socket, RecvFlag.WAIT);
    }
}
