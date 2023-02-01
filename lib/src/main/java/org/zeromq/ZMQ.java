package org.zeromq;

public class ZMQ {

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY) {
            System.loadLibrary("libzmqj");
        }
    }

    public static final int NOBLOCK = 1;
    public static final int DONTWAIT = 1;
    public static final int SNDMORE = 2;



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

    public static int version_full(){
        return _version_full();
    }

    public static int version_major(){
        return _version_major();
    }

    public static   int version_minor(){
        return _version_minor();
    }

    public static  int version_patch(){
        return _version_patch();
    }

    public static  int make_version(int major, int miner, int patch){
        return makeVersion(major,miner,patch);
    }

    public static  long ENOTSUP(){
        return _ENOTSUP();
    }

    public static  long EPROTONOSUPPORT(){
        return _EPROTONOSUPPORT();
    }

    public static  long ENOBUFS(){
        return _ENOBUFS();
    }

    public static  long ENETDOWN(){
        return _ENETDOWN();
    }

    public static  long EADDRINUSE(){
        return _EADDRINUSE();
    }

    public static  long EADDRNOTAVAIL(){
        return _EADDRNOTAVAIL();
    }

    public static  long ECONNREFUSED(){
        return _ECONNREFUSED();
    }

    public static  long EINPROGRESS(){
        return _EINPROGRESS();
    }

    public static  long EHOSTUNREACH(){
        return _EHOSTUNREACH();
    }

    public static  long EMTHREAD(){
        return _EMTHREAD();
    }

    public static  long EFSM(){
        return _EFSM();
    }

    public static  long ENOCOMPATPROTO(){
        return _ENOCOMPATPROTO();
    }

    public static  long ETERM(){
        return _ETERM();
    }

    public static  long ENOTSOCK(){
        return  _ENOTSOCK();
    }

    public static  long EAGAIN(){
        return _EAGAIN();
    }

    private static native int _version_full();

    private static native int _version_major();

    private static native int _version_minor();

    private static native int _version_patch();

    private static native int _make_version(int major, int miner, int patch);

    private static native long _ENOTSUP();

    private static native long _EPROTONOSUPPORT();

    private static native long _ENOBUFS();

    private static native long _ENETDOWN();

    private static native long _EADDRINUSE();

    private static native long _EADDRNOTAVAIL();

    private static native long _ECONNREFUSED();

    private static native long _EINPROGRESS();

    private static native long _EHOSTUNREACH();

    private static native long _EMTHREAD();

    private static native long _EFSM();

    private static native long _ENOCOMPATPROTO();

    private static native long _ETERM();

    private static native long _ENOTSOCK();

    private static native long _EAGAIN();



    public enum Error {
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

    public static void proxy(ZMQSocket frontend, ZMQSocket backend, ZMQSocket capture) {
        _proxy(frontend, backend, capture);
    }
    private static native void _proxy(ZMQSocket frontend, ZMQSocket backend, ZMQSocket capture);
}
