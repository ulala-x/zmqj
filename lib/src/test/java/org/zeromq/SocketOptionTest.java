package org.zeromq;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class SocketOptionTest {

    ZSocket socket;

    final String publicKey = "kFgdO=-)YJPbg(.?6yilC5MWmnp}0K.-";
    final String secretKey = "0*YCOy+-t/r}@>KIxUx5(H@rzjUOAj9X";

    @BeforeEach
    void setUp(){
        socket = new ZSocket(SocketType.ROUTER);
    }

    @AfterEach
    void tearDown(){
        socket.close();
    }

    @Test
    void receiveMore(){
        assertThat(socket.receiveMore()).isFalse();
    }

    @Test
    void affinity(){
        assertThat(socket.affinity()).isEqualTo(0);
        socket.affinity(1);
        assertThat(socket.affinity()).isEqualTo(1);
    }

    @Test
    void bindToDevice(){
        assertThat(socket.bindToDevice()).isEqualTo("");
        socket.bindToDevice("VRF");
        assertThat(socket.bindToDevice()).isEqualTo("VRF");
    }

    @Test
    void backlog(){
        assertThat(socket.backlog()).isEqualTo(100);
        socket.backlog(10000);
        assertThat(socket.backlog()).isEqualTo(10000);
    }

    @Test
    void connectRoutingId(){
        socket.connectRoutingId("routerId");
    }

    @Test
    void conflate(){
        assertThat(socket.conflate()).isFalse();
        socket.conflate(true);
        assertThat(socket.conflate()).isTrue();
    }
    @Test
    void connectTimeout(){
        assertThat(socket.connectTimeout()).isEqualTo(0);
        socket.connectTimeout(10000);
        assertThat(socket.connectTimeout()).isEqualTo(10000);
    }

    @Test
    void curvePublicKey(){
        assertThat(socket.curvePublicKey()).isEqualTo(new byte[32]);
        socket.curvePublicKey(publicKey.getBytes());
        assertThat(socket.curvePublicKey()).isEqualTo(publicKey.getBytes());
    }

    @Test
    void curveSecretKey(){
        assertThat(socket.curveSecretKey()).isEqualTo(new byte[32]);
        socket.curveSecretKey(secretKey.getBytes());
        assertThat(socket.curveSecretKey()).isEqualTo(secretKey.getBytes());
    }

    @Test
    void curveServer(){
        assertThat(socket.curveServer()).isFalse();
        socket.curveServer(true);
        assertThat(socket.curveServer()).isTrue();
    }

    @Test
    void curveServerKey(){
        assertThat(socket.curveServerKey()).isEqualTo(new byte[32]);
        socket.curveServerKey(publicKey.getBytes());
        assertThat(socket.curveServerKey()).isEqualTo(publicKey.getBytes());
    }

    @Test
    void fd() throws InterruptedException {

        long fd = socket.fd();
        System.out.println(fd);

    }

    @Test
    void gssApixxx(){
        /*
         if (!getenv ("KRB5_KTNAME") || !getenv ("KRB5_CLIENT_KTNAME")) {
            TEST_IGNORE_MESSAGE ("KRB5 environment unavailable, skipping test");
        }
         */
//        socket.gssApiServer(true);
//        socket.gssApiServicePrincipal("local@REALM");
//        socket.gssApiPrincipalNameType(GssApiPrincipalNameTypes.NT_HOSTBASED);
//
//        assertThat(socket.gssApiPlainText()).isFalse();
//        socket.gssApiPlainText(true);
//        assertThat(socket.gssApiPlainText()).isTrue();
    }

    @Test
    void handShakeInterval(){
        assertThat(socket.handShakeInterval()).isEqualTo(30000);
        socket.handShakeInterval(100000);
        assertThat(socket.handShakeInterval()).isEqualTo(100000);
    }
    @Test
    void heartBeatInterval(){
        assertThat(socket.heartBeatInterval()).isEqualTo(0);
        socket.heartBeatInterval(100000);
        assertThat(socket.heartBeatInterval()).isEqualTo(100000);
    }
    @Test
    void heartBeatTimeout(){
        assertThat(socket.heartBeatTimeout()).isEqualTo(-1);
        socket.heartBeatTimeout(100000);
        assertThat(socket.heartBeatTimeout()).isEqualTo(100000);
    }

    @Test
    void heartBeatTTL(){
        assertThat(socket.heartBeatTTL()).isEqualTo(0);
        socket.heartBeatTTL(100000);
        assertThat(socket.heartBeatTTL()).isEqualTo(100000);
    }

    @Test
    void immediate(){
        assertThat(socket.immediate()).isFalse();
        socket.immediate(true);
        assertThat(socket.immediate()).isTrue();
    }

    @Test
    void invertMatching() throws IOException {
        var pubSocket = new ZSocket(SocketType.PUB);
        assertThat(pubSocket.invertMatching()).isFalse();
        pubSocket.invertMatching(true);
        assertThat(pubSocket.invertMatching()).isTrue();
    }

    @Test
    void ipV6() {
        assertThat(socket.ipV6()).isFalse();
        socket.ipV6(true);
        assertThat(socket.ipV6()).isTrue();
    }

    @Test
    void linger(){
        assertThat(socket.linger()).isEqualTo(-1);
        socket.linger(100000);
        assertThat(socket.linger()).isEqualTo(100000);
    }

    @Test
    void maxMsgSize(){
        assertThat(socket.maxMessageSize()).isEqualTo(-1);
        socket.maxMessageSize(100000);
        assertThat(socket.maxMessageSize()).isEqualTo(100000);
    }

    @Test
    void multicastHops(){
        assertThat(socket.multicastHops()).isEqualTo(1);
        socket.multicastHops(100000);
        assertThat(socket.multicastHops()).isEqualTo(100000);
    }

    @Test
    void multicastMaxTransportDataUnit(){
        assertThat(socket.multicastMaxTransportDataUnit()).isEqualTo(1500);
        socket.multicastMaxTransportDataUnit(100000);
        assertThat(socket.multicastMaxTransportDataUnit()).isEqualTo(100000);
    }

    @Test
    void plainPassword(){
        assertThat(socket.plainPassword()).isEqualTo("");
        socket.plainPassword("password");
        assertThat(socket.plainPassword()).isEqualTo("password");
    }


    @Test
    void plainServer() {
        assertThat(socket.plainServer()).isFalse();
        socket.plainServer(true);
        assertThat(socket.plainServer()).isTrue();
    }

    @Test
    void plainUserName(){
        assertThat(socket.plainUserName()).isEqualTo("");
        socket.plainUserName("userName");
        assertThat(socket.plainUserName()).isEqualTo("userName");
    }

    @Test
    void useFD(){
        assertThat(socket.useFD()).isEqualTo(-1);
        socket.useFD(1000);
        assertThat(socket.useFD()).isEqualTo(1000);
    }

    @Test
    void probeRouter(){
        socket.probeRouter(true);
    }

    @Test
    void receiveBufferSize(){
        assertThat(socket.receiveBufferSize()).isEqualTo(-1);
        socket.receiveBufferSize(64*1000);
        assertThat(socket.receiveBufferSize()).isEqualTo(64*1000);
    }

    @Test
    void receiveHighWaterMark(){
        assertThat(socket.receiveHighWaterMark()).isEqualTo(1000);
        socket.receiveHighWaterMark(0);
        assertThat(socket.receiveHighWaterMark()).isEqualTo(0);
    }


    @Test
    void rate(){
        assertThat(socket.rate()).isEqualTo(100);
        socket.rate(1000);
        assertThat(socket.rate()).isEqualTo(1000);
    }

    @Test
    void receiveTimeout(){
        assertThat(socket.receiveTimeout()).isEqualTo(-1);
        socket.receiveTimeout(0);
        assertThat(socket.receiveTimeout()).isEqualTo(0);
    }

    @Test
    void reconnectInterval(){
        assertThat(socket.reconnectInterval()).isEqualTo(100);
        socket.reconnectInterval(-1);
        assertThat(socket.reconnectInterval()).isEqualTo(-1);
    }

    @Test
    void reconnectIntervalMax(){
        assertThat(socket.reconnectIntervalMax()).isEqualTo(0);
        socket.reconnectIntervalMax(1000);
        assertThat(socket.reconnectIntervalMax()).isEqualTo(1000);
    }

    @Test
    void requestCorrelate(){
        var serverSocket= new ZSocket(SocketType.REQ);
        serverSocket.requestCorrelate(true);
    }

    @Test
    void requestRelaxed(){
        var serverSocket= new ZSocket(SocketType.REQ);
        serverSocket.requestRelaxed(true);
    }

    @Test
    void routerHandOver(){
        socket.routerHandOver(true);
    }

    @Test
    void routerMandatory(){
        socket.routerMandatory(true);
    }

    @Test
    void routingID(){
        assertThat(socket.routingId()).isEqualTo(new byte[0]);
        socket.routingId("id".getBytes());
        assertThat(socket.routingId()).isEqualTo("id".getBytes());
    }


    @Test
    void sendBufferSize(){
        assertThat(socket.sendBufferSize()).isEqualTo(-1);
        socket.sendBufferSize(64000);
        assertThat(socket.sendBufferSize()).isEqualTo(64000);
    }

    @Test
    void sendHighWaterMark(){
        assertThat(socket.sendHighWaterMark()).isEqualTo(1000);
        socket.sendHighWaterMark(0);
        assertThat(socket.sendHighWaterMark()).isEqualTo(0);
    }


    @Test
    void sendTimeout(){
        assertThat(socket.sendTimeout()).isEqualTo(-1);
        socket.sendTimeout(0);
        assertThat(socket.sendTimeout()).isEqualTo(0);
    }

    @Test
    void socksProxy(){
        assertThat(socket.socksProxy()).isEqualTo("");
        socket.socksProxy("proxyAddress");
        assertThat(socket.socksProxy()).isEqualTo("proxyAddress");
    }

    @Test
    void streamNotify() {
        ZSocket streamSocket = new ZSocket(SocketType.STREAM);
        streamSocket.streamNotify(false);
    }

    @Test
    void subscribe(){
        ZSocket subSocket = new ZSocket(SocketType.SUB);
        subSocket.subscribe("sub".getBytes());
    }

    @Test
    void tcpKeepAlive(){
        assertThat(socket.tcpKeepAlive()).isEqualTo(-1);
        socket.tcpKeepAlive(0);
        assertThat(socket.tcpKeepAlive()).isEqualTo(0);
    }

    @Test
    void tcpKeepAliveCount(){
        assertThat(socket.tcpKeepAliveCount()).isEqualTo(-1);
        socket.tcpKeepAliveCount(10);
        assertThat(socket.tcpKeepAliveCount()).isEqualTo(10);
    }

    @Test
    void tcpKeepAliveIdle(){
        assertThat(socket.tcpKeepAliveIdle()).isEqualTo(-1);
        socket.tcpKeepAliveIdle(10);
        assertThat(socket.tcpKeepAliveIdle()).isEqualTo(10);
    }

    @Test
    void tcpKeepAliveInterval(){
        assertThat(socket.tcpKeepAliveInterval()).isEqualTo(-1);
        socket.tcpKeepAliveInterval(10);
        assertThat(socket.tcpKeepAliveInterval()).isEqualTo(10);
    }

    @Test
    void tcpMaxRetransmitTimeout(){
        assertThat(socket.tcpMaxRetransmitTimeout()).isEqualTo(0);
        socket.tcpMaxRetransmitTimeout(100);
        assertThat(socket.tcpMaxRetransmitTimeout()).isEqualTo(100);
    }

    @Test
    void typeOfService(){
        assertThat(socket.typeOfService()).isEqualTo(0);
        socket.typeOfService(100);
        assertThat(socket.typeOfService()).isEqualTo(100);
    }

    @Test
    void unsubscribe(){
        ZSocket subSocket = new ZSocket(SocketType.SUB);
        subSocket.unsubscribe("sub".getBytes());
    }

    @Test
    void xPubVerbose(){
        ZSocket xpubSocket = new ZSocket(SocketType.XPUB);
        xpubSocket.xPubVerbose(true);
    }

    @Test
    void xPubVerboser(){
        ZSocket xpubSocket = new ZSocket(SocketType.XPUB);
        xpubSocket.xPubVerboser(true);
    }

    @Test
    void xPubManual(){
        ZSocket xpubSocket = new ZSocket(SocketType.XPUB);
        xpubSocket.xPubManual(true);
    }

    @Test
    void xPubNoDrop(){
        ZSocket xPubSocket = new ZSocket(SocketType.XPUB);
        xPubSocket.xPubNoDrop(true);
    }

    @Test
    void xPubWelcomeMessage(){
        ZSocket xpubSocket = new ZSocket(SocketType.XPUB);
        xpubSocket.xPubWelcomeMessage("welcome");
    }

    @Test
    void zapDomain(){
        assertThat(socket.zapDomain()).isEqualTo("");
        socket.zapDomain("domain");
        assertThat(socket.zapDomain()).isEqualTo("domain");
    }

    @Test
    void events(){
        System.out.println(socket.events());
    }

    @Test
    void type(){
        assertThat(socket.type()).isEqualTo(SocketType.ROUTER);
    }


    // draft
    @Test
    void zapEnforceDomain(){

        assertThat(socket.zapEnforceDomain()).isFalse();
        socket.zapEnforceDomain(true);
        assertThat(socket.zapEnforceDomain()).isTrue();
    }

    @Test
    void metaData(){
        socket.metaData("X-key:value");
    }

    @Test
    void multicastLoop(){
        assertThat(socket.multicastLoop()).isTrue();
        socket.multicastLoop(false);
        assertThat(socket.multicastLoop()).isFalse();
    }

    @Test
    void routerNotify(){
        assertThat(socket.routerNotify()).isEqualTo(RouterNotifyOption.NOTIFY_NONE);
        socket.routerNotify(RouterNotifyOption.NOTIFY_CONNECT);
        assertThat(socket.routerNotify()).isEqualTo(RouterNotifyOption.NOTIFY_CONNECT);
    }


    @Test
    /*
    test in vmware
     */
    void vmciBufferSize() {
//        socket.bind("vmci://*:5555");
//        assertThat(socket.vmciBufferSize()).isEqualTo(65546L);
//        socket.vmciBufferSize(1000000);
//        assertThat(socket.vmciBufferSize()).isEqualTo(1000000L);
    }
}
