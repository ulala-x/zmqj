package org.zeromq;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZFrame implements  AutoCloseable{

    /**
     * The buffer must be allocated using ByteBuffer.allocateDirect
     *
     * @param buffer
     * @return
     */
    private boolean isSent = false;
    public ZFrame(ByteBuffer buffer, boolean useZeroCopy){
        _construct(buffer,useZeroCopy);
    }

    public ZFrame(ByteBuffer buffer){
        _construct(buffer,false);
    }

    public ZFrame(byte[] msg, int offset, int len){
        _construct(msg,offset,len);
    }

    public ZFrame(byte[] msg){
        _construct(msg,0,msg.length);
    }

    @Override
    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            this._destroy();
        }
    }
    public int length() {return _length();}
    public byte[] data(){
        return _data();
    }
    private ZFrame(long msgHandle){
        this.msgHandle = msgHandle;
    }
    private long msgHandle;

    private static native void _nativeInit();
    private final AtomicBoolean closed = new AtomicBoolean(false);

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY){
            System.loadLibrary("libzmqj");
        }
        _nativeInit();
    }

    private long getMsgHandle() {
        return this.msgHandle;
    }
    private native void _construct(byte[] msg, int offset, int len);
    private native void _construct(ByteBuffer buffer,boolean useZeroCopy);

    private native byte[] _data();

    private native void _destroy();
    private native int _length();

    public boolean isSent() {return isSent;}

    public void markSent() {isSent = true;}
}
