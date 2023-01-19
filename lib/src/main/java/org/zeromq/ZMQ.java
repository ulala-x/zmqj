package org.zeromq;

public class ZMQ {
    public static final int NOBLOCK = 1;
    public static final int DONTWAIT = 1;
    public static final int SNDMORE = 2;

    public ZMQ() {
    }

    public static native int zmq_errno();

    public static native String zmq_error_msg(int var0);

    public static int getMajorVersion() {
        return version_major();
    }

    public static int getMinorVersion() {
        return version_minor();
    }

    public static int getPatchVersion() {
        return version_patch();
    }

    public static int getFullVersion() {
        return version_full();
    }

    public static int makeVersion(int var0, int var1, int var2) {
        return make_version(var0, var1, var2);
    }

    public static String getVersionString() {
        return String.format("%d.%d.%d", version_major(), version_minor(), version_patch());
    }

    protected static native int version_full();

    protected static native int version_major();

    protected static native int version_minor();

    protected static native int version_patch();

    protected static native int make_version(int major, int miner, int patch);

    protected static native long ENOTSUP();

    protected static native long EPROTONOSUPPORT();

    protected static native long ENOBUFS();

    protected static native long ENETDOWN();

    protected static native long EADDRINUSE();

    protected static native long EADDRNOTAVAIL();

    protected static native long ECONNREFUSED();

    protected static native long EINPROGRESS();

    protected static native long EHOSTUNREACH();

    protected static native long EMTHREAD();

    protected static native long EFSM();

    protected static native long ENOCOMPATPROTO();

    protected static native long ETERM();

    protected static native long ENOTSOCK();

    protected static native long EAGAIN();

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY) {
            System.loadLibrary("jzmq");
        }

    }

    public static enum Error {
        ENOTSUP(ZMQ.ENOTSUP()),
        EPROTONOSUPPORT(ZMQ.EPROTONOSUPPORT()),
        ENOBUFS(ZMQ.ENOBUFS()),
        ENETDOWN(ZMQ.ENETDOWN()),
        EADDRINUSE(ZMQ.EADDRINUSE()),
        EADDRNOTAVAIL(ZMQ.EADDRNOTAVAIL()),
        ECONNREFUSED(ZMQ.ECONNREFUSED()),
        EINPROGRESS(ZMQ.EINPROGRESS()),
        EHOSTUNREACH(ZMQ.EHOSTUNREACH()),
        EMTHREAD(ZMQ.EMTHREAD()),
        EFSM(ZMQ.EFSM()),
        ENOCOMPATPROTO(ZMQ.ENOCOMPATPROTO()),
        ETERM(ZMQ.ETERM()),
        ENOTSOCK(ZMQ.ENOTSOCK()),
        EAGAIN(ZMQ.EAGAIN());

        private final long code;

        private Error(long code) {
            this.code = code;
        }

        public long getCode() {
            return this.code;
        }

        public static Error findByCode(int code) {
            for (Error e : Error.class.getEnumConstants()) {
                if (e.getCode() == code) {
                    return e;
                }
            }
            throw new IllegalArgumentException("Unknown " + Error.class.getName() + " enum code:" + code);
        }
    }
}
