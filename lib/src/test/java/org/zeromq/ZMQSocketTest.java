package org.zeromq;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.*;

public class ZMQSocketTest {

    @Test
    public void testReqRep() {

        ZMQContext context = new ZMQContext(1);
        ZMQSocket in = new ZMQSocket(context, SocketType.REQ);
        in.bind("inproc://reqrep");

        ZMQSocket out = new ZMQSocket(context, SocketType.REP);
        out.connect("inproc://reqrep");


        for (int i = 0; i < 10; i++) {
            byte[] req = ("request" + i).getBytes();
            byte[] rep = ("reply" + i).getBytes();

            assertThat(in.send(req, SendFlag.WAIT)).isTrue();
            byte[] reqTmp = out.recv(RecvFlag.WAIT);
            assertThat(req).isEqualTo(reqTmp);

            assertThat(out.send(rep, SendFlag.WAIT)).isTrue();
            byte[] repTmp = in.recv(RecvFlag.WAIT);
            assertThat(rep).isEqualTo(repTmp);
        }
    }

    @Test
    public void testReqRepWithZMQMsg() {

        ZMQContext context = new ZMQContext(1);
        ZMQSocket in = new ZMQSocket(context, SocketType.REQ);
        in.bind("inproc://reqrep");

        ZMQSocket out = new ZMQSocket(context, SocketType.REP);
        out.connect("inproc://reqrep");


        for (int i = 0; i < 100; i++) {
            byte[] req = ("request" + i).getBytes();
            byte[] rep = ("reply" + i).getBytes();

            ZFrame msgReq = new ZFrame(req);
            assertThat(in.send(msgReq, SendFlag.WAIT)).isTrue();

            try(ZFrame reqTmp = out.receive(RecvFlag.WAIT)){
                assertThat(reqTmp.data()).isEqualTo(req);
            }

            ZFrame msgRep = new ZFrame(rep);
            assertThat(out.send(msgRep, SendFlag.WAIT)).isTrue();
            try(ZFrame repTmp = in.receive(RecvFlag.WAIT)){
                assertThat(repTmp.data()).isEqualTo(rep);
            }

        }
    }

    @Test
    public void testReqRepWithZMQMsgWithByteBuffer() {

        ZMQContext context = new ZMQContext(1);
        ZMQSocket in = new ZMQSocket(context, SocketType.REQ);
        in.bind("inproc://reqrep");

        ZMQSocket out = new ZMQSocket(context, SocketType.REP);
        out.connect("inproc://reqrep");


        for (int i = 0; i < 100; i++) {
            byte[] req = ("request" + i).getBytes();
            byte[] rep = ("reply" + i).getBytes();

            var reqBuffer = ByteBuffer.allocateDirect(req.length).put(req).flip();
            var repBuffer = ByteBuffer.allocateDirect(req.length).put(rep).flip();

            ZFrame msgReq = new ZFrame(reqBuffer,true);
            assertThat(in.send(msgReq, SendFlag.WAIT)).isTrue();

            try(ZFrame reqTmp = out.receive(RecvFlag.WAIT)){
                assertThat(reqTmp.data()).isEqualTo(req);
            }

            ZFrame msgRep = new ZFrame(repBuffer,false);
            assertThat(out.send(msgRep, SendFlag.WAIT)).isTrue();
            try(ZFrame repTmp = in.receive(RecvFlag.WAIT)){
                assertThat(repTmp.data()).isEqualTo(rep);
            }
        }
    }

    @Test
    public void testXPUBSUB() {

        ZMQContext context = new ZMQContext(1);

        ZMQSocket xPub = new ZMQSocket(context, SocketType.XPUB);
        xPub.bind("inproc://xpub");

        ZMQSocket sub = new ZMQSocket(context, SocketType.SUB);
        sub.connect("inproc://xpub");
        ZMQSocket xSub = new ZMQSocket(context, SocketType.XSUB);
        xSub.connect("inproc://xpub");

        sub.subscribe("".getBytes());
        byte[] subcr = xPub.recv(RecvFlag.WAIT);
        assertThat(new byte[] { 1 }).isEqualTo(subcr);

        sub.unsubscribe("".getBytes());
        subcr = xPub.recv(RecvFlag.WAIT);
        assertThat(new byte[] { 0 }).isEqualTo(subcr);


        byte[] subscription = "subs".getBytes();

        // Append subscription
        byte[] expected = new byte[subscription.length + 1];
        expected[0] = 1;
        System.arraycopy(subscription, 0, expected, 1, subscription.length);

        sub.subscribe(subscription);
        subcr = xPub.recv(RecvFlag.WAIT);
        assertThat(subcr).isEqualTo(expected);

        // Verify xsub subscription
        xSub.send(expected, SendFlag.WAIT);
        subcr = xPub.recv(RecvFlag.DONT_WAIT);
        assertThat(subcr).isNull();


        for (int i = 0; i < 10; i++) {
            byte[] data = ("subscrip" + i).getBytes();

            assertThat(xPub.send(data, SendFlag.WAIT)).isTrue();
            // Verify SUB
            byte[] tmp = sub.recv(RecvFlag.WAIT);
            assertThat(tmp).isEqualTo(data);

            // Verify XSUB
            tmp = xSub.recv(RecvFlag.WAIT);
            assertThat(tmp).isEqualTo(data);
        }
    }

    @Test
    public void testSetXPubVerbose() {

        ZMQContext context = new ZMQContext(1);

        byte[] topic = "topic".getBytes();
        byte[] subscription = new byte[topic.length + 1];
        subscription[0] = 1;
        System.arraycopy(topic, 0, subscription, 1, topic.length);

        ZMQSocket xPubVerbose = new ZMQSocket(context, SocketType.XPUB);
        xPubVerbose.xPubVerbose(true);
        xPubVerbose.bind("inproc://xpub_verbose");

        ZMQSocket xPubDefault = new ZMQSocket(context, SocketType.XPUB);
        xPubDefault.bind("inproc://xpub_default");

        ZMQSocket[] xSubs = new ZMQSocket[3];
        for (int i = 0; i < xSubs.length; i++) {
            xSubs[i] = new ZMQSocket(context, SocketType.XSUB);
            xSubs[i].connect("inproc://xpub_verbose");
            xSubs[i].connect("inproc://xpub_default");
        }

        for (int i = 0; i < xSubs.length; i++) {
            xSubs[i].send(subscription, SendFlag.WAIT);
            assertThat(xPubVerbose.recv(RecvFlag.WAIT)).isEqualTo(subscription);
            if (i == 0) {
                assertThat(xPubDefault.recv(RecvFlag.WAIT)).isEqualTo(subscription) ;
            }
            else {
                assertThat(xPubDefault.recv(RecvFlag.DONT_WAIT)).isNull();
            }
        }

        for (int i = 0; i < xSubs.length; i++) {
            xSubs[i].close();
        }
        xPubVerbose.close();
        xPubDefault.close();
        context.term();
    }

    static class Client extends Thread {

        private ZMQSocket socket;
        private String name;
        public Client(ZMQContext ctx, String name_) {
            socket = new ZMQSocket(ctx, SocketType.REQ);
            name = name_;
            socket.routingId(name.getBytes());
        }
        @Override
        public void run() {
            socket.connect("tcp://127.0.0.1:6660");
            socket.send("hello".getBytes(), SendFlag.WAIT);
            String msg = new String(socket.recv(RecvFlag.WAIT));
            socket.send("world".getBytes(), SendFlag.WAIT);
            msg = new String(socket.recv(RecvFlag.WAIT));

            socket.close();
        }
    }

    static class Dealer extends Thread {

        private ZMQSocket socket;
        private String name;

        public Dealer(ZMQContext ctx, String name_) {
            socket = new ZMQSocket(ctx, SocketType.DEALER);
            name = name_;

            socket.routingId(name.getBytes());
        }

        @Override
        public void run() {

            socket.connect("tcp://127.0.0.1:6661");
            int count = 0;
            while (count < 2) {
                String msg = new String(socket.recv(RecvFlag.WAIT));
                if (msg == null) {
                    throw new RuntimeException();
                }
                String identity = msg;
                msg = new String(socket.recv(RecvFlag.WAIT));
                if (msg == null) {
                    throw new RuntimeException();
                }

                msg = new String(socket.recv(RecvFlag.WAIT));
                if (msg == null) {
                    throw new RuntimeException();
                }

                socket.send(identity.getBytes(), SendFlag.SEND_MORE);
                socket.send("".getBytes(), SendFlag.SEND_MORE);
                String response = "OK " + msg;

                socket.send(response.getBytes(), SendFlag.WAIT);
                count++;
            }
            socket.close();
        }
    }

    static class Main extends Thread {

        ZMQContext ctx;

        Main(ZMQContext ctx_) {
            ctx = ctx_;
        }

        @Override
        public void run() {
            ZMQSocket frontend = new ZMQSocket(ctx, SocketType.ROUTER);

            assertThat(frontend).isNotNull();
            frontend.bind("tcp://127.0.0.1:6660");

            ZMQSocket backend = new ZMQSocket(ctx, SocketType.DEALER);
            assertThat(backend).isNotNull();
            backend.bind("tcp://127.0.0.1:6661");

            ZMQ.proxy(frontend, backend, null);

            frontend.close();
            backend.close();
        }

    }

    @Test
    public void testProxy() throws Exception {

        ZMQContext ctx = new ZMQContext(1);
        assert (ctx != null);

        Main main = new Main(ctx);
        main.start();
        new Dealer(ctx, "AA").start();
        new Dealer(ctx, "BB").start();

        Thread.sleep(100);
        Thread c1 = new Client(ctx, "X");
        c1.start();

        Thread c2 = new Client(ctx, "Y");
        c2.start();

        c1.join();
        c2.join();

        ctx.term();
    }

    /**
     * Test method for Router Mandatory
     */
    @Test
    public void testRouterMandatory() {

        ZMQContext context = new ZMQContext(1);

        ZMQSocket routerSocket = new ZMQSocket(context, SocketType.ROUTER);
        boolean ret = routerSocket.send("UNREACHABLE".getBytes(), SendFlag.SEND_MORE);
        assertThat(ret).isTrue();
        routerSocket.send("END".getBytes(), SendFlag.WAIT);

        routerSocket.routerMandatory(true);

        assertThatThrownBy(()->{
            routerSocket.send("UNREACHABLE".getBytes(), SendFlag.SEND_MORE);
        }).isInstanceOf(ZMQException.class);


        try {
            routerSocket.send("UNREACHABLE".getBytes(), SendFlag.SEND_MORE);
        } catch (ZMQException e) {
            assertThat(e.getErrorCode()).isEqualTo(ZMQ.EHOSTUNREACH());
        }

        routerSocket.close();
        context.term();
    }

    @Test
    public void testRouterToRouter() throws InterruptedException {

        ZMQSocket serverSocket = new ZMQSocket(SocketType.ROUTER);
        serverSocket.routingId("Server".getBytes());
        serverSocket.bind("tcp://127.0.0.1:7777");

        ZMQSocket clientSocket = new ZMQSocket(SocketType.ROUTER);
        clientSocket.routingId("Client".getBytes());
        clientSocket.connect("tcp://127.0.0.1:7777");

        Thread.sleep(100);


        clientSocket.send("Server".getBytes(), SendFlag.SEND_MORE);
        clientSocket.send("hello".getBytes(), SendFlag.WAIT);

//        clientSocket.routerMandatory(true);
//
//        for(int i=0 ; i< 200000; ++i){
//            clientSocket.send("Server" .getBytes(),SendFlag.sendMore);
//            clientSocket.send(String.format("hello %d",i).getBytes(),SendFlag.dontWait);
//        }
        String routingId =new String(serverSocket.recv(RecvFlag.WAIT));
        String message = new String(serverSocket.recv(RecvFlag.WAIT));

        assertThat(routingId).isEqualTo("Client");
        assertThat(message).isEqualTo("hello");
    }

    @Test
    public void testSendMoreRequestReplyOverTcp() {

        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket reply = new ZMQSocket(context, SocketType.REP);
            ZMQSocket req = new ZMQSocket(context, SocketType.REQ)
        ) {
            reply.bind("tcp://*:12345");

            req.connect("tcp://localhost:12345");
            req.send("test1".getBytes(), SendFlag.SEND_MORE);
            req.send("test2".getBytes(), SendFlag.WAIT);
            assertThat(new String(reply.recv())).isEqualTo("test1");
            assertThat(reply.receiveMore()).isTrue();
            assertThat(new String(reply.recv())).isEqualTo("test2");
        }
    }

    @Test
    public void testWritingToClosedSocket() {

        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket reply = new ZMQSocket(context, SocketType.REP);
            ZMQSocket req = new ZMQSocket(context, SocketType.REQ)
        ) {
            reply.bind("tcp://*:12345");

            req.connect("tcp://localhost:12345");
            req.close();
            assertThatThrownBy(() -> {
                req.send("test".getBytes(),SendFlag.DONT_WAIT);
            }).isInstanceOf(ZMQException.class);

            try {
                req.send("test".getBytes(),SendFlag.DONT_WAIT);
            }catch (ZMQException e){
                assertThat(e.getErrorCode()).isEqualTo(ZMQ.ENOTSOCK());
            }

        }

    }


}
