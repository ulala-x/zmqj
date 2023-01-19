package org.zeromq;

import java.util.concurrent.atomic.AtomicBoolean;

public class Context implements AutoCloseable {
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private long contextHandle;

    public void term() {
        if (this.closed.compareAndSet(false, true)) {
            this.destroy();
        }

    }

    public Context(int ioThreads) {
        this._construct(ioThreads);
    }

    private native void _construct(int ioThreads);

    private native void destroy();

    private long getContextHandle() {
        return this.contextHandle;
    }

    public void close() {
        this.term();
    }

    private native boolean _setMaxSockets(int maxSockets);

    public boolean setMaxSockets(int maxSockets) {
        return this._setMaxSockets(maxSockets);
    }

    private native int _getMaxSockets();

    public int getMaxSockets() {
        return this._getMaxSockets();
    }
}
