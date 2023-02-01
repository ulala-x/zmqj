package org.zeromq;

public class SocketOption {
    public static final int ZMQ_AFFINITY = 4;
    public static final int ZMQ_ROUTING_ID = 5;
    public static final int ZMQ_SUBSCRIBE = 6;
    public static final int ZMQ_UNSUBSCRIBE = 7;
    public static final int ZMQ_RATE = 8;
    public static final int ZMQ_RECOVERY_IVL = 9;
    public static final int ZMQ_SNDBUF = 11;
    public static final int ZMQ_RCVBUF = 12;
    public static final int ZMQ_RCVMORE = 13;
    public static final int ZMQ_FD = 14;
    public static final int ZMQ_EVENTS = 15;
    public static final int ZMQ_TYPE = 16;
    public static final int ZMQ_LINGER = 17;
    public static final int ZMQ_RECONNECT_IVL = 18;
    public static final int ZMQ_BACKLOG = 19;
    public static final int ZMQ_RECONNECT_IVL_MAX = 21;
    public static final int ZMQ_MAXMSGSIZE = 22;
    public static final int ZMQ_SNDHWM = 23;
    public static final int ZMQ_RCVHWM = 24;
    public static final int ZMQ_MULTICAST_HOPS = 25;
    public static final int ZMQ_RCVTIMEO = 27;
    public static final int ZMQ_SNDTIMEO = 28;
    public static final int ZMQ_LAST_ENDPOINT = 32;
    public static final int ZMQ_ROUTER_MANDATORY = 33;
    public static final int ZMQ_TCP_KEEPALIVE = 34;
    public static final int ZMQ_TCP_KEEPALIVE_CNT = 35;
    public static final int ZMQ_TCP_KEEPALIVE_IDLE = 36;
    public static final int ZMQ_TCP_KEEPALIVE_INTVL = 37;
    public static final int ZMQ_IMMEDIATE = 39;
    public static final int ZMQ_XPUB_VERBOSE = 40;
    @Deprecated
    public static final int ZMQ_ROUTER_RAW = 41;
    public static final int ZMQ_IPV6 = 42;
    public static final int ZMQ_MECHANISM = 43;
    public static final int ZMQ_PLAIN_SERVER = 44;
    public static final int ZMQ_PLAIN_USERNAME = 45;
    public static final int ZMQ_PLAIN_PASSWORD = 46;
    public static final int ZMQ_CURVE_SERVER = 47;
    public static final int ZMQ_CURVE_PUBLICKEY = 48;
    public static final int ZMQ_CURVE_SECRETKEY = 49;
    public static final int ZMQ_CURVE_SERVERKEY = 50;
    public static final int ZMQ_PROBE_ROUTER = 51;
    public static final int ZMQ_REQ_CORRELATE = 52;
    public static final int ZMQ_REQ_RELAXED = 53;
    public static final int ZMQ_CONFLATE = 54;
    public static final int ZMQ_ZAP_DOMAIN = 55;
    public static final int ZMQ_ROUTER_HANDOVER = 56;
    public static final int ZMQ_TOS = 57;
    public static final int ZMQ_CONNECT_ROUTING_ID = 61;
    public static final int ZMQ_GSSAPI_SERVER = 62;
    public static final int ZMQ_GSSAPI_PRINCIPAL = 63;
    public static final int ZMQ_GSSAPI_SERVICE_PRINCIPAL = 64;
    public static final int ZMQ_GSSAPI_PLAINTEXT = 65;
    public static final int ZMQ_HANDSHAKE_IVL = 66;
    public static final int ZMQ_SOCKS_PROXY = 68;
    public static final int ZMQ_XPUB_NODROP = 69;
    public static final int ZMQ_BLOCKY = 70;
    public static final int ZMQ_XPUB_MANUAL = 71;
    public static final int ZMQ_XPUB_WELCOME_MSG = 72;
    public static final int ZMQ_STREAM_NOTIFY = 73;
    public static final int ZMQ_INVERT_MATCHING = 74;
    public static final int ZMQ_HEARTBEAT_IVL = 75;
    public static final int ZMQ_HEARTBEAT_TTL = 76;
    public static final int ZMQ_HEARTBEAT_TIMEOUT = 77;
    public static final int ZMQ_XPUB_VERBOSER = 78;
    public static final int ZMQ_CONNECT_TIMEOUT = 79;
    public static final int ZMQ_TCP_MAXRT = 80;
    public static final int ZMQ_THREAD_SAFE = 81;
    public static final int ZMQ_MULTICAST_MAXTPDU = 84;
    public static final int ZMQ_VMCI_BUFFER_SIZE = 85;
    public static final int ZMQ_VMCI_BUFFER_MIN_SIZE = 86;
    public static final int ZMQ_VMCI_BUFFER_MAX_SIZE = 87;
    public static final int ZMQ_VMCI_CONNECT_TIMEOUT = 88;
    public static final int ZMQ_USE_FD = 89;
    public static final int ZMQ_GSSAPI_PRINCIPAL_NAMETYPE = 90;
    public static final int ZMQ_GSSAPI_SERVICE_PRINCIPAL_NAMETYPE = 91;
    public static final int ZMQ_BINDTODEVICE = 92;

    /*
     DRAFT Socket options.
    */

    public static final int ZMQ_ZAP_ENFORCE_DOMAIN = 93;
    public static final int ZMQ_LOOPBACK_FASTPATH = 94;
    public static final int ZMQ_METADATA = 95;
    public static final int ZMQ_MULTICAST_LOOP = 96;
    public static final int ZMQ_ROUTER_NOTIFY = 97;
    public static final int ZMQ_XPUB_MANUAL_LAST_VALUE = 98;
    public static final int ZMQ_SOCKS_USERNAME = 99;
    public static final int ZMQ_SOCKS_PASSWORD = 100;
    public static final int ZMQ_IN_BATCH_SIZE = 101;
    public static final int ZMQ_OUT_BATCH_SIZE = 102;
    public static final int ZMQ_WSS_KEY_PEM = 103;
    public static final int ZMQ_WSS_CERT_PEM = 104;
    public static final int ZMQ_WSS_TRUST_PEM = 105;
    public static final int ZMQ_WSS_HOSTNAME = 106;
    public static final int ZMQ_WSS_TRUST_SYSTEM = 107;
    public static final int ZMQ_ONLY_FIRST_SUBSCRIBE = 108;
    public static final int ZMQ_RECONNECT_STOP = 109;
    public static final int ZMQ_HELLO_MSG = 110;
    public static final int ZMQ_DISCONNECT_MSG = 111;
    public static final int ZMQ_PRIORITY = 112;


    public SocketOption() {
    }
}
