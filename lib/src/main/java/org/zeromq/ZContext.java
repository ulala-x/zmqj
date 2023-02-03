package org.zeromq;

import java.util.concurrent.atomic.AtomicBoolean;

public class ZContext implements AutoCloseable {
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private long contextHandle;

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY){
            System.loadLibrary("libzmqj");
        }
    }

    public void term() {
        if (this.closed.compareAndSet(false, true)) {
            this._destroy();
        }

    }

    public ZContext(int ioThreads) {
        this._construct(ioThreads);
    }

    private native void _construct(int ioThreads);

    private native void _destroy();


    /**
     * Get the underlying context handle. This is private because it is only accessed from JNI, where Java access
     * controls are ignored.
     *
     * @return the internal 0MQ context handle.
     */
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
