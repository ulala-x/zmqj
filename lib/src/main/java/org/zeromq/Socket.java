package org.zeromq;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Socket implements AutoCloseable {
    private long socketHandle;
    private final Context context;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private static native void _nativeInit();

    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            this.destroy();
        }

    }

    private long getSocketHandle() {
        return this.socketHandle;
    }

    private native void _construct(Context context, int socketType);

    private native void destroy();

    public Socket(Context context, SocketType socketType) {
        this.context = context;
        this._construct(this.context, socketType.number());
    }

    public Socket(SocketType socketType) {
        this.context = new Context(1);
        this._construct(this.context, socketType.number());
    }

    private native int _bind(String endpoint);

    public void bind(String endpoint) {
        int var2 = this._bind(endpoint);
        if (var2 != 0) {
            throw new ZMQException.InternalError();
        }
    }

    private native int _unbind(String endpoint);

    public void unbind(String endpoint) {
        int var2 = this._unbind(endpoint);
        if (var2 != 0) {
            throw new ZMQException.InternalError();
        }
    }

    private native int _connect(String endpoint);

    public void connect(String endpoint) {
        int var2 = this._connect(endpoint);
        if (var2 != 0) {
            throw new ZMQException.InternalError();
        }
    }

    private native int _disconnect(String endpoint);

    public void disconnect(String endpoint) {
        int var2 = this._disconnect(endpoint);
        if (var2 != 0) {
            throw new ZMQException.InternalError();
        }
    }

    private native int _monitor(String endpoint, int socketEvent);

    public void monitor(String endpoint, SocketEvent socketEvent) {
        int var3 = this._monitor(endpoint, socketEvent.number());
        if (var3 != 0) {
            throw new ZMQException.InternalError();
        }
    }

    private native int _getBooleanSockopt(int option, Boolean value);

    private native int _setBooleanSockopt(int option, Boolean value);

    private native int _getIntSockopt(int option, Integer value);

    private native int _setIntSockopt(int option, Integer value);

    private native int _getLongSockopt(int option, Long value);

    private native int _setLongSockopt(int option, long value);

    private native int _setBytesSockopt(int option, byte[] value);

    private native int _getBytesSockopt(int option, byte[] value, int valueSize);

    public void setAffinity(int value) {
        this.setIntOption(SocketOption.ZMQ_AFFINITY,value);
    }

    public long getAffinity() {
        return this.getIntOption(SocketOption.ZMQ_AFFINITY);
    }

    public void setBacklog(int value) {
        this.setIntOption(SocketOption.ZMQ_BACKLOG, value);
    }

    public int getBacklog() {
        return this.getIntOption(SocketOption.ZMQ_BACKLOG);
    }

    public String bindToDevice() {
        return this.getStringOption(SocketOption.ZMQ_BINDTODEVICE);
    }

    public void bindToDevice(String value) {
        this.setStringOption(SocketOption.ZMQ_BINDTODEVICE, value);
    }

    public void connectRoutingId(String value) {
        this.setStringOption(SocketOption.ZMQ_CONNECT_ROUTING_ID, value);
    }

    public boolean conflate() {
        return this.getBooleanOption(SocketOption.ZMQ_CONFLATE);
    }

    public void conflate(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_CONFLATE, value);
    }

    public int connectTimeout() {
        return this.getIntOption(SocketOption.ZMQ_CONNECT_TIMEOUT);
    }

    public void connectTimeout(int value) {
        this.setIntOption(SocketOption.ZMQ_CONNECT_TIMEOUT, value);
    }

    public String curvePublicKey() {
        return this.getStringOption(SocketOption.ZMQ_CURVE_PUBLICKEY);
    }

    public void curvePublicKey(String value) {
        this.setStringOption(SocketOption.ZMQ_CURVE_PUBLICKEY, value);
    }

    public void curveSecretKey(String value) {
        this.setStringOption(SocketOption.ZMQ_CURVE_SECRETKEY, value);
    }

    public String curveSecretKey() {
        return this.getStringOption(SocketOption.ZMQ_CURVE_SECRETKEY);
    }

    public boolean curveServer() {
        return this.getBooleanOption(SocketOption.ZMQ_CURVE_SERVER);
    }

    public void curveServer(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_CURVE_SERVER, value);
    }

    public String curveServerKey() {
        return this.getStringOption(SocketOption.ZMQ_CURVE_SERVERKEY);
    }

    public void curveServerKey(String value) {
        this.setStringOption(SocketOption.ZMQ_CURVE_SERVERKEY, value);
    }

    public boolean gssApiPlainText() {
        return this.getBooleanOption(SocketOption.ZMQ_GSSAPI_PLAINTEXT);
    }

    public void gssApiPlainText(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_GSSAPI_PLAINTEXT, value);
    }

    public String gssApiPrincipal() {
        return this.getStringOption(SocketOption.ZMQ_GSSAPI_PRINCIPAL);
    }

    public void gssApiPrincipal(String value) {
        this.setStringOption(SocketOption.ZMQ_GSSAPI_PRINCIPAL, value);
    }

    public boolean gssApiServer() {
        return this.getBooleanOption(SocketOption.ZMQ_GSSAPI_SERVER);
    }

    public void gssApiServer(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_GSSAPI_SERVER, value);
    }

    public String gssApiServicePrincipal() {
        return this.getStringOption(SocketOption.ZMQ_GSSAPI_SERVICE_PRINCIPAL);
    }

    public void gssApiServicePrincipal(String value) {
        this.setStringOption(SocketOption.ZMQ_GSSAPI_SERVICE_PRINCIPAL, value);
    }

    public int gssApiServicePrincipalNameType() {
        return this.getIntOption(SocketOption.ZMQ_GSSAPI_SERVICE_PRINCIPAL_NAMETYPE);
    }

    public void gssApiServicePrincipalNameType(int value) {
        this.setIntOption(SocketOption.ZMQ_GSSAPI_SERVICE_PRINCIPAL_NAMETYPE, value);
    }

    public int gssApiPrincipalNameType() {
        return this.getIntOption(SocketOption.ZMQ_GSSAPI_PRINCIPAL_NAMETYPE);
    }

    public void gssApiPrincipalNameType(int value) {
        this.setIntOption(SocketOption.ZMQ_GSSAPI_SERVICE_PRINCIPAL_NAMETYPE, value);
    }

    public int handShakeInterval() {
        return this.getIntOption(SocketOption.ZMQ_HANDSHAKE_IVL);
    }

    public void handShakeInterval(int value) {
        this.setIntOption(SocketOption.ZMQ_HANDSHAKE_IVL, value);
    }

    public int heartBeatInterval() {
        return this.getIntOption(SocketOption.ZMQ_HEARTBEAT_IVL);
    }

    public void heartBeatInterval(int value) {
        this.setIntOption(SocketOption.ZMQ_HEARTBEAT_IVL, value);
    }

    public int heartBeatTimeout() {
        return this.getIntOption(SocketOption.ZMQ_HEARTBEAT_TIMEOUT);
    }

    public void heartBeatTimeout(int value) {
        this.setIntOption(SocketOption.ZMQ_HEARTBEAT_TIMEOUT, value);
    }

    public int heartBeatTTL() {
        return this.getIntOption(SocketOption.ZMQ_HEARTBEAT_TTL);
    }

    public void heartBeatTTL(int value) {
        this.setIntOption(SocketOption.ZMQ_HEARTBEAT_TTL, value);
    }

    public boolean immediate() {
        return this.getBooleanOption(SocketOption.ZMQ_IMMEDIATE);
    }

    public void immediate(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_IMMEDIATE, value);
    }

    public boolean invertMatching() {
        return this.getBooleanOption(SocketOption.ZMQ_INVERT_MATCHING);
    }

    public void invertMatching(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_INVERT_MATCHING, value);
    }

    public boolean ipV6() {
        return this.getBooleanOption(SocketOption.ZMQ_IPV6);
    }

    public void ipV6(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_IPV6, value);
    }

    public int linger() {
        return this.getIntOption(SocketOption.ZMQ_LINGER);
    }

    public void linger(int value) {
        this.setIntOption(SocketOption.ZMQ_LINGER, value);
    }

    public long maxMessageSize() {
        return this.getLongOption(SocketOption.ZMQ_MAXMSGSIZE);
    }

    public void maxMessageSize(long value) {
        this.setLongOption(SocketOption.ZMQ_MAXMSGSIZE, value);
    }

    public int multicastHops() {
        return this.getIntOption(SocketOption.ZMQ_MULTICAST_HOPS);
    }

    public void multicastHops(int value) {
        this.setIntOption(SocketOption.ZMQ_MULTICAST_HOPS, value);
    }

    public int multicastMaxTransportDataUnit() {
        return this.getIntOption(SocketOption.ZMQ_MULTICAST_MAXTPDU);
    }

    public void multicastMaxTransportDataUnit(int value) {
        this.setIntOption(SocketOption.ZMQ_MULTICAST_MAXTPDU, value);
    }

    public String plainPassword() {
        return this.getStringOption(SocketOption.ZMQ_PLAIN_PASSWORD);
    }

    public void plainPassword(String value) {
        this.setStringOption(SocketOption.ZMQ_PLAIN_PASSWORD, value);
    }

    public boolean plainServer() {
        return this.getBooleanOption(SocketOption.ZMQ_PLAIN_SERVER);
    }

    public void plainServer(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_PLAIN_SERVER, value);
    }

    public String plainUserName() {
        return this.getStringOption(SocketOption.ZMQ_PLAIN_USERNAME);
    }

    public void plainUserName(String value) {
        this.setStringOption(SocketOption.ZMQ_PLAIN_USERNAME, value);
    }

    public int useFD() {
        return this.getIntOption(SocketOption.ZMQ_USE_FD);
    }

    public void useFD(int value) {
        this.setIntOption(SocketOption.ZMQ_USE_FD, value);
    }

    public void probeRouter(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_PROBE_ROUTER, value);
    }

    public int rate() {
        return this.getIntOption(SocketOption.ZMQ_RATE);
    }

    public void rate(int value) {
        this.setIntOption(SocketOption.ZMQ_RATE, value);
    }

    public int receiveBufferSize() {
        return this.getIntOption(SocketOption.ZMQ_RCVBUF);
    }

    public void receiveBufferSize(int value) {
        this.setIntOption(SocketOption.ZMQ_RCVBUF, value);
    }

    public int receiveHighWaterMark() {
        return this.getIntOption(SocketOption.ZMQ_RCVHWM);
    }

    public void receiveHighWaterMark(int value) {
        this.setIntOption(SocketOption.ZMQ_RCVHWM, value);
    }

    public int receiveTimeout() {
        return this.getIntOption(SocketOption.ZMQ_RCVTIMEO);
    }

    public void receiveTimeout(int value) {
        this.setIntOption(SocketOption.ZMQ_RCVTIMEO, value);
    }

    public boolean receiveMore() {
        return this.getBooleanOption(SocketOption.ZMQ_RCVMORE);
    }

    public int reconnectInterval() {
        return this.getIntOption(SocketOption.ZMQ_RECONNECT_IVL);
    }

    public void reconnectInterval(int value) {
        this.setIntOption(SocketOption.ZMQ_RECONNECT_IVL, value);
    }

    public int reconnectIntervalMax() {
        return this.getIntOption(SocketOption.ZMQ_RECONNECT_IVL_MAX);
    }

    public void reconnectIntervalMax(int value) {
        this.setIntOption(SocketOption.ZMQ_RECONNECT_IVL_MAX, value);
    }

    public void requestCorrelate(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_REQ_CORRELATE, value);
    }

    public void requestRelaxed(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_REQ_RELAXED, value);
    }

    public void routerHandOver(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_ROUTER_HANDOVER, value);
    }

    public void routerMandatory(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_ROUTER_MANDATORY, value);
    }

    public String routingId() {
        return this.getStringOption(SocketOption.ZMQ_ROUTING_ID);
    }

    public void routingId(String value) {
        this.setStringOption(SocketOption.ZMQ_ROUTING_ID, value);
    }

    public int sendBufferSize() {
        return this.getIntOption(SocketOption.ZMQ_SNDBUF);
    }

    public void sendBufferSize(int value) {
        this.setIntOption(SocketOption.ZMQ_SNDBUF, value);
    }

    public int sendHighWaterMark() {
        return this.getIntOption(SocketOption.ZMQ_SNDHWM);
    }

    public void sendHighWaterMark(int value) {
        this.setIntOption(SocketOption.ZMQ_SNDHWM, value);
    }

    public int sendTimeout() {
        return this.getIntOption(SocketOption.ZMQ_SNDTIMEO);
    }

    public void sendTimeout(int value) {
        this.setIntOption(SocketOption.ZMQ_SNDTIMEO, value);
    }

    public String socksProxy() {
        return this.getStringOption(SocketOption.ZMQ_SOCKS_PROXY);
    }

    public void socksProxy(String value) {
        this.setStringOption(SocketOption.ZMQ_SOCKS_PROXY, value);
    }

    public void streamNotify(Boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_STREAM_NOTIFY, value);
    }

    public void subscribe(String value) {
        this.setStringOption(SocketOption.ZMQ_SUBSCRIBE, value);
    }

    public int tcpKeepAlive() {
        return this.getIntOption(SocketOption.ZMQ_TCP_KEEPALIVE);
    }

    public void tcpKeepAlive(int value) {
        this.setIntOption(SocketOption.ZMQ_TCP_KEEPALIVE, value);
    }

    public int tcpKeepAliveCount() {
        return this.getIntOption(SocketOption.ZMQ_TCP_KEEPALIVE_CNT);
    }

    public void tcpKeepAliveCount(int value) {
        this.setIntOption(SocketOption.ZMQ_TCP_KEEPALIVE_CNT, value);
    }

    public int tcpKeepAliveIdle() {
        return this.getIntOption(SocketOption.ZMQ_TCP_KEEPALIVE_IDLE);
    }

    public void tcpKeepAliveIdle(int value) {
        this.setIntOption(SocketOption.ZMQ_TCP_KEEPALIVE_IDLE, value);
    }

    public int tcpKeepAliveInterval() {
        return this.getIntOption(SocketOption.ZMQ_TCP_KEEPALIVE_INTVL);
    }

    public void tcpKeepAliveInterval(int value) {
        this.setIntOption(SocketOption.ZMQ_TCP_KEEPALIVE_INTVL, value);
    }

    public int tcpMaxRetransmitTimeout() {
        return this.getIntOption(SocketOption.ZMQ_TCP_MAXRT);
    }

    public void tcpMaxRetransmitTimeout(int value) {
        this.setIntOption(SocketOption.ZMQ_TCP_MAXRT, value);
    }

    public int typeOfService() {
        return this.getIntOption(SocketOption.ZMQ_TOS);
    }

    public void typeOfService(int value) {
        this.setIntOption(SocketOption.ZMQ_TOS, value);
    }

    public void unsubscribe(String value) {
        this.setStringOption(SocketOption.ZMQ_UNSUBSCRIBE, value);
    }

    public void xPubVerbose(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_XPUB_VERBOSE, value);
    }

    public void xPubVerboser(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_XPUB_VERBOSER, value);
    }

    public void xPubManual(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_XPUB_MANUAL, value);
    }

    public boolean xPubManual() {
        return this.getBooleanOption(SocketOption.ZMQ_XPUB_MANUAL);
    }

    public void xPubNoDrop(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_XPUB_NODROP, value);
    }

    public boolean xPubNoDrop() {
        return this.getBooleanOption(SocketOption.ZMQ_XPUB_NODROP);
    }

    public void xPubWelcomeMessage(String value) {
        this.setStringOption(SocketOption.ZMQ_XPUB_WELCOME_MSG, value);
    }

    public String zapDomain() {return this.getStringOption(SocketOption.ZMQ_ZAP_DOMAIN);}

    public void zapDomain(String value) {
        this.setStringOption(SocketOption.ZMQ_ZAP_DOMAIN, value);
    }

    public String lastEndpoint() {
        return this.getStringOption(SocketOption.ZMQ_LAST_ENDPOINT);
    }

    public int bindToRandomPort(String endpoint) {
        return this.bindToRandomPort(endpoint, 2000, 20000, 100);
    }

    public int bindToRandomPort(String endpoint, int minPort) {
        return this.bindToRandomPort(endpoint, minPort, 20000, 100);
    }

    public int bindToRandomPort(String endpoint, int minPort, int maxPort) {
        return this.bindToRandomPort(endpoint, minPort, maxPort, 100);
    }

    public int bindToRandomPort(String endpoint, int minPort, int maxPort, int maxTries) {
        Random random = new Random();
        int port = 0;

        while(port < maxTries) {
            int var5 = random.nextInt(maxPort - minPort + 1) + minPort;

            try {
                this._bind(String.format("%s:%s", endpoint, var5));
                return var5;
            } catch (ZMQException var9) {
                if ((long)var9.getErrorCode() != ZMQ.EADDRINUSE()) {
                    throw var9;
                }

                ++port;
            }
        }

        throw new ZMQException("Could not bind socket to random port.", (int)ZMQ.EADDRINUSE());
    }

    public String bindToSystemRandomPort(String var1) {
        this._bind(String.format("%s:*", var1));
        String var2 = this.lastEndpoint();
        String var3 = var2.substring(var2.lastIndexOf(":") + 1);
        return var3;
    }

    private boolean getBooleanOption(int option) {
        Boolean value = Boolean.FALSE;
        int result = this._getBooleanSockopt(option, value);
        if (0 != result) {
            throw new ZMQException.InternalError();
        } else {
            return value;
        }
    }

    private void setBooleanOption(int option, boolean value) {
        int result = this._setBooleanSockopt(option, value);
        if (0 != result) {
            throw new ZMQException.InternalError();
        }
    }

    private int getIntOption(int var1) {
        Integer var2 = 0;
        int var3 = this._getIntSockopt(var1, var2);
        if (0 != var3) {
            throw new ZMQException.InternalError();
        } else {
            return var2;
        }
    }

    private void setIntOption(int option, int value) {
        int result = this._setIntSockopt(option, value);
        if (0 != result) {
            throw new ZMQException.InternalError();
        }
    }

    public void setLongOption(int var1, long var2) {
        int var4 = this._setLongSockopt(var1, (long)var1);
        if (var4 != 0) {
            throw new ZMQException.InternalError();
        }
    }

    private long getLongOption(int var1) {
        Long var2 = 0L;
        int var3 = this._getLongSockopt(var1, var2);
        if (var3 != 0) {
            throw new ZMQException.InternalError();
        } else {
            return var2;
        }
    }

    private byte[] getByteOption(int var1) {
        byte[] var2 = new byte[1024];
        int var3 = this._getBytesSockopt(var1, var2, var2.length);
        if (var3 != 0) {
            throw new ZMQException.InternalError();
        } else {
            return var2;
        }
    }

    private byte[] setByteOption(int var1, byte[] var2) {
        int var3 = this._setBytesSockopt(var1, var2);
        if (var3 != 0) {
            throw new ZMQException.InternalError();
        } else {
            return var2;
        }
    }

    private String getStringOption(int var1) {
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(this.getByteOption(var1))).toString();
    }

    private void setStringOption(int var1, String var2) {
        this.setByteOption(var1, var2.getBytes(StandardCharsets.UTF_8));
    }

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY) {
            System.loadLibrary("zmqj");
        }

        _nativeInit();
    }
}
