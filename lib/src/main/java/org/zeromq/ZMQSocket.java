package org.zeromq;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZMQSocket implements AutoCloseable {
    private long socketHandle;
    private final ZMQContext context;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final int maxOptionSize = 256;
    private static native void _nativeInit();

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY){
            System.loadLibrary("libzmqj");
        }
        _nativeInit();
    }

    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            this._destroy();
        }

    }

    public long getSocketHandle() {
        return this.socketHandle;
    }

    private native void _construct(ZMQContext context, int socketType);

    private native void _destroy();

    public ZMQSocket(ZMQContext context, SocketType socketType) {
        this.context = context;
        this._construct(this.context, socketType.value());
    }

    public ZMQSocket(SocketType socketType) {
        this.context = new ZMQContext(1);
        this._construct(this.context, socketType.value());
    }

    private native void _bind(String endpoint);

    public void bind(String endpoint) {
         this._bind(endpoint);
    }

    private native void _unbind(String endpoint);

    public void unbind(String endpoint) {
        this._unbind(endpoint);
    }

    private native void _connect(String endpoint);

    public void connect(String endpoint) {
        this._connect(endpoint);
    }

    private native void _disconnect(String endpoint);

    public void disconnect(String endpoint) {
        this._disconnect(endpoint);
    }

    private native boolean _monitor(String endpoint, int socketEvent);

    public boolean monitor(String endpoint, EventType eventType) {
        return this._monitor(endpoint, eventType.value());
    }


    /*
    send/recv
     */

    /**
     * Send a message.
     *
     * @param msg the message to send, as an array of bytes.
     * @return true if send was successful, false otherwise.
     */
    public boolean send(byte[] msg){
        return send(msg,SendFlag.WAIT);
    }

    /**
     * @param msg
     * @param sendFlag
     * @return
     */
    public boolean send(byte[] msg, SendFlag sendFlag) {
        return send(msg, 0, msg.length, sendFlag);
    }

    public boolean send(byte[] msg, int offset, int len, SendFlag sendFlag){
        return _send(msg,offset,len,sendFlag.value());
    }

    public boolean send(ZFrame msg, SendFlag sendFlag){
        try(msg){
            return _send(msg,sendFlag.value());
        }
    }

    public native boolean _send(byte[] msg, int offset, int len, int flags);

    public native boolean _send(ZFrame msg, int flags);



    private native ZFrame _receive(int flags);
    public ZFrame receive(RecvFlag recvFlag){
        return _receive(recvFlag.value());
    }


    /**
     * Receive a message.
     *
     * @param flags the flags to apply to the receive operation.
     * @return the message received, as an array of bytes; null on error.
     */
    private native byte[] _recv(int flags);

    public  byte[] recv(RecvFlag flags){
        return _recv(flags.value());
    }
    public byte[] recv(){
        return recv(RecvFlag.WAIT);
    }

    /**
     * Receive a message in to a specified buffer.
     *
     * @param buffer byte[] to copy zmq message payload in to.
     * @param offset offset in buffer to write data
     * @param len max bytes to write to buffer. If len is smaller than the incoming message size, the message will
     *            be truncated.
     * @param flags the flags to apply to the receive operation.
     * @return the number of bytes read, -1 on error
     */
    private native int _recv(byte[] buffer, int offset, int len, int flags);
    public int recv(byte[] buffer, int offset, int len, RecvFlag flags){
        return _recv(buffer,offset,len,flags.value());
    }

   /*
    option
     */

    private native boolean _getBooleanSockopt(int option);

    private native void _setBooleanSockopt(int option, boolean value);

    private native int _getIntSockopt(int option);

    private native void _setIntSockopt(int option, int value);

    private native long _getLongSockopt(long option);

    private native void _setLongSockopt(int option, long value);

    private native void _setBytesSockopt(int option, byte[] value);

    private native byte[] _getBytesSockopt(int option,int optionValueSize);

    public void affinity(int value) {
        this.setLongOption(SocketOption.ZMQ_AFFINITY,value);
    }

    public long affinity() {
        return this.getLongOption(SocketOption.ZMQ_AFFINITY);
    }

    public void backlog(int value) {
        this.setIntOption(SocketOption.ZMQ_BACKLOG, value);
    }

    public int backlog() {
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

    public byte[] curvePublicKey() {
        return this.getByteOption(SocketOption.ZMQ_CURVE_PUBLICKEY,32);
    }

    public void curvePublicKey(byte[] value) {
        this.setByteOption(SocketOption.ZMQ_CURVE_PUBLICKEY, value);
    }

    public void curveSecretKey(byte[] value) {
        this.setByteOption(SocketOption.ZMQ_CURVE_SECRETKEY, value);
    }

    public byte[] curveSecretKey() {
        return this.getByteOption(SocketOption.ZMQ_CURVE_SECRETKEY,32);
    }

    public boolean curveServer() {
        return this.getBooleanOption(SocketOption.ZMQ_CURVE_SERVER);
    }

    public void curveServer(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_CURVE_SERVER, value);
    }

    public byte[] curveServerKey() {
        return this.getByteOption(SocketOption.ZMQ_CURVE_SERVERKEY,32);
    }

    public void curveServerKey(byte[] value) {
        this.setByteOption(SocketOption.ZMQ_CURVE_SERVERKEY, value);
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

    public void gssApiPrincipalNameType(GssApiPrincipalNameTypes types) {
        this.setIntOption(SocketOption.ZMQ_GSSAPI_SERVICE_PRINCIPAL_NAMETYPE, types.value());
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

    public byte[] routingId() {
        return this.getByteOption(SocketOption.ZMQ_ROUTING_ID,maxOptionSize);
    }

    public void routingId(byte[] value) {
        this.setByteOption(SocketOption.ZMQ_ROUTING_ID, value);
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

    public void subscribe(byte[] value) {
        this.setByteOption(SocketOption.ZMQ_SUBSCRIBE, value);
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

    public void unsubscribe(byte[] value) {
        this.setByteOption(SocketOption.ZMQ_UNSUBSCRIBE, value);
    }

    public long vmciBufferSize(){
        return this.getLongOption(SocketOption.ZMQ_VMCI_BUFFER_SIZE);
    }
    public void vmciBufferSize(long value){
        this.setLongOption(SocketOption.ZMQ_VMCI_BUFFER_SIZE,value);
    }

    public long vmciBufferMinSize(){
        return this.getLongOption(SocketOption.ZMQ_VMCI_BUFFER_MIN_SIZE);
    }
    public void vmciBufferMinSize(long value){
        this.setLongOption(SocketOption.ZMQ_VMCI_BUFFER_MIN_SIZE,value);
    }

    public long vmciBufferMaxSize(){
        return this.getLongOption(SocketOption.ZMQ_VMCI_BUFFER_MAX_SIZE);
    }
    public void vmciBufferMaxSize(long value){
        this.setLongOption(SocketOption.ZMQ_VMCI_BUFFER_MAX_SIZE,value);
    }

    public int vmciConnectTimeout(){
        return this.getIntOption(SocketOption.ZMQ_VMCI_CONNECT_TIMEOUT);
    }
    public void vmciConnectTimeout(int value){
        this.setIntOption(SocketOption.ZMQ_VMCI_CONNECT_TIMEOUT,value);
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


    public void xPubWelcomeMessage(String value) {
        this.setStringOption(SocketOption.ZMQ_XPUB_WELCOME_MSG, value);
    }

    public String zapDomain() {return this.getStringOption(SocketOption.ZMQ_ZAP_DOMAIN);}

    public void zapDomain(String value) {
        this.setStringOption(SocketOption.ZMQ_ZAP_DOMAIN, value);
    }

    // draft api
    public boolean zapEnforceDomain() {return this.getBooleanOption(SocketOption.ZMQ_ZAP_ENFORCE_DOMAIN);}

    // draft api
    public void zapEnforceDomain(boolean value) {
        this.setBooleanOption(SocketOption.ZMQ_ZAP_ENFORCE_DOMAIN, value);
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
        int port;
        Random rand = new Random();
        for (int i = 0; i < maxTries; i++) {
            port = rand.nextInt(maxPort - minPort + 1) + minPort;
            try {
                bind(String.format("%s:%s", endpoint, port));
                return port;
            } catch (ZMQException e) {
                if (e.getErrorCode() != ZMQ.EADDRINUSE()) {
                    throw e;
                }
                continue;
            }
        }
        throw new ZMQException("Could not bind socket to random port.", (int) ZMQ.EADDRINUSE());
    }

    public String bindToSystemRandomPort(String endpoint) {
        this._bind(String.format("%s:*", endpoint));
        String lastEndpoint = this.lastEndpoint();
        String port = lastEndpoint.substring(lastEndpoint.lastIndexOf(":") + 1);
        return port;
    }

    private boolean getBooleanOption(int option) {
        return this._getBooleanSockopt(option);
    }

    private void setBooleanOption(int option, boolean value) {
        this._setBooleanSockopt(option, value);
    }

    private int getIntOption(int option) {
        return  this._getIntSockopt(option);
    }

    private void setIntOption(int option, int value) {
        this._setIntSockopt(option, value);
    }

    public void setLongOption(int option, long value) {
        this._setLongSockopt(option,value);
    }

    private long getLongOption(long option) {
        return this._getLongSockopt(option);
    }

    private byte[] getByteOption(int option,int optionValueSize) {
        return this._getBytesSockopt(option,optionValueSize);
    }

    private void setByteOption(int option, byte[] value) {
        this._setBytesSockopt(option, value);
    }

    private String getStringOption(int option) {
        byte[] byteOption = this.getByteOption(option,maxOptionSize);
        if( byteOption.length <= 1){
            return "";
        }
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(byteOption,0,byteOption.length-1)).toString();
    }

    private void setStringOption(int option, String value) {
        this.setByteOption(option, value.getBytes(StandardCharsets.UTF_8));
    }


    public int fd() {
        return getIntOption(SocketOption.ZMQ_FD);
    }


    public void metaData(String value) {
        this.setStringOption(SocketOption.ZMQ_METADATA,value);
    }

    public boolean multicastLoop() {
        return this.getBooleanOption(SocketOption.ZMQ_MULTICAST_LOOP);
    }

    public void multicastLoop(boolean value) {
         this.setBooleanOption(SocketOption.ZMQ_MULTICAST_LOOP,value);
    }

    public RouterNotifyOption routerNotify() {
        return RouterNotifyOption.valueOf(this.getIntOption(SocketOption.ZMQ_ROUTER_NOTIFY));
    }

    public void routerNotify(RouterNotifyOption value) {
        this.setIntOption(SocketOption.ZMQ_ROUTER_NOTIFY,value.value());
    }

    public int events() {
        return this.getIntOption(SocketOption.ZMQ_EVENTS);
    }

    public SocketType type() {
        return SocketType.valueOf(this.getIntOption(SocketOption.ZMQ_TYPE));
    }
}
