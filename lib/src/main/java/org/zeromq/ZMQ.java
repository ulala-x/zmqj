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
        return _version_major();
    }

    public static int getMinorVersion() {
        return _version_minor();
    }

    public static int getPatchVersion() {
        return _version_patch();
    }

    public static int getFullVersion() {
        return _version_full();
    }

    public static int makeVersion(int var0, int var1, int var2) {
        return _make_version(var0, var1, var2);
    }

    public static String getVersionString() {
        return String.format("%d.%d.%d", _version_major(), _version_minor(), _version_patch());
    }

    public static native int _version_full();

    public static native int _version_major();

    public static native int _version_minor();

    public static native int _version_patch();

    public static native int _make_version(int major, int miner, int patch);

    public static native long _ENOTSUP();

    public static native long _EPROTONOSUPPORT();

    public static native long _ENOBUFS();

    public static native long _ENETDOWN();

    public static native long _EADDRINUSE();

    public static native long _EADDRNOTAVAIL();

    public static native long _ECONNREFUSED();

    public static native long _EINPROGRESS();

    public static native long _EHOSTUNREACH();

    public static native long _EMTHREAD();

    public static native long _EFSM();

    public static native long _ENOCOMPATPROTO();

    public static native long _ETERM();

    public static native long _ENOTSOCK();

    public static native long _EAGAIN();

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY) {
            System.loadLibrary("jzmq");
        }

    }

    public static enum Error {
        ENOTSUP(ZMQ._ENOTSUP()),
        EPROTONOSUPPORT(ZMQ._EPROTONOSUPPORT()),
        ENOBUFS(ZMQ._ENOBUFS()),
        ENETDOWN(ZMQ._ENETDOWN()),
        EADDRINUSE(ZMQ._EADDRINUSE()),
        EADDRNOTAVAIL(ZMQ._EADDRNOTAVAIL()),
        ECONNREFUSED(ZMQ._ECONNREFUSED()),
        EINPROGRESS(ZMQ._EINPROGRESS()),
        EHOSTUNREACH(ZMQ._EHOSTUNREACH()),
        EMTHREAD(ZMQ._EMTHREAD()),
        EFSM(ZMQ._EFSM()),
        ENOCOMPATPROTO(ZMQ._ENOCOMPATPROTO()),
        ETERM(ZMQ._ETERM()),
        ENOTSOCK(ZMQ._ENOTSOCK()),
        EAGAIN(ZMQ._EAGAIN());

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
