package org.zeromq;

import org.assertj.core.internal.ByteArrays;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.*;

public class ZSocketTest {

    @Test
    public void testReqRep() {

        ZContext context = new ZContext(1);
        ZSocket in = new ZSocket(context, SocketType.REQ);
        in.bind("inproc://reqrep");

        ZSocket out = new ZSocket(context, SocketType.REP);
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
    public void testXPUBSUB() {

        ZContext context = new ZContext(1);

        ZSocket xPub = new ZSocket(context, SocketType.XPUB);
        xPub.bind("inproc://xpub");

        ZSocket sub = new ZSocket(context, SocketType.SUB);
        sub.connect("inproc://xpub");
        ZSocket xSub = new ZSocket(context, SocketType.XSUB);
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

        ZContext context = new ZContext(1);

        byte[] topic = "topic".getBytes();
        byte[] subscription = new byte[topic.length + 1];
        subscription[0] = 1;
        System.arraycopy(topic, 0, subscription, 1, topic.length);

        ZSocket xPubVerbose = new ZSocket(context, SocketType.XPUB);
        xPubVerbose.xPubVerbose(true);
        xPubVerbose.bind("inproc://xpub_verbose");

        ZSocket xPubDefault = new ZSocket(context, SocketType.XPUB);
        xPubDefault.bind("inproc://xpub_default");

        ZSocket[] xSubs = new ZSocket[3];
        for (int i = 0; i < xSubs.length; i++) {
            xSubs[i] = new ZSocket(context, SocketType.XSUB);
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
        context.close();
    }

    static class Client extends Thread {

        private ZSocket socket;
        private String name;
        public Client(ZContext ctx, String name_) {
            socket = new ZSocket(ctx, SocketType.REQ);
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

        private ZSocket socket;
        private String name;

        public Dealer(ZContext ctx, String name_) {
            socket = new ZSocket(ctx, SocketType.DEALER);
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

        ZContext ctx;

        Main(ZContext ctx_) {
            ctx = ctx_;
        }

        @Override
        public void run() {
            ZSocket frontend = new ZSocket(ctx, SocketType.ROUTER);

            assertThat(frontend).isNotNull();
            frontend.bind("tcp://127.0.0.1:6660");

            ZSocket backend = new ZSocket(ctx, SocketType.DEALER);
            assertThat(backend).isNotNull();
            backend.bind("tcp://127.0.0.1:6661");

            ZMQ.proxy(frontend, backend, null);

            frontend.close();
            backend.close();
        }

    }

    @Test
    public void testProxy() throws Exception {

        ZContext ctx = new ZContext(1);
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

        ZContext context = new ZContext(1);

        ZSocket routerSocket = new ZSocket(context, SocketType.ROUTER);
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

        ZSocket serverSocket = new ZSocket(SocketType.ROUTER);
        serverSocket.routingId("Server".getBytes());
        serverSocket.bind("tcp://127.0.0.1:7777");

        ZSocket clientSocket = new ZSocket(SocketType.ROUTER);
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

        try(ZContext context = new ZContext(1);
            ZSocket reply = new ZSocket(context, SocketType.REP);
            ZSocket req = new ZSocket(context, SocketType.REQ)
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

        try(ZContext context = new ZContext(1);
            ZSocket reply = new ZSocket(context, SocketType.REP);
            ZSocket req = new ZSocket(context, SocketType.REQ)
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

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
    public static byte[] getId(byte[] input) {
        if (input.length == 0) {
            return input;
        }
        byte[] result = new byte[input.length - 1];
        System.arraycopy(input, 1, result, 0, result.length);
        return result;
    }

    public static byte[] getId(int identify){
        byte[] result = new byte[5];
        result[0] = 0;
        byte[] input = ByteBuffer.allocate(4).putInt(identify).array();

        System.arraycopy(input, 0, result, 1, input.length);
        return result;
    }


    static class StreamServer implements Runnable {

        private ZContext context = new ZContext(1);
        private ZSocket socket = new ZSocket(context,SocketType.STREAM);
        private Set<String> clientList = new HashSet<>();

        private boolean isFinish = false;
        private String endpoint = "";
        private String states ="";

        public StreamServer(String endpoint){
            this.endpoint = endpoint;
        }

        void finish(){
            this.isFinish = true;
        }
        @Override
        public void run(){
            socket.bind(endpoint);
            System.out.println("Server started");
            while(!isFinish){
                byte[] identify = socket.recv();
                String routerId = new String(identify);
                String message = new String(socket.recv());

                //                assertThat(identify).isEqualTo(routerId.getBytes());

                if(message.length() == 0){

                    if(!clientList.contains(routerId)){
                        System.out.println(String.format("%s : connected",routerId));
                        states = "connected";
                        clientList.add(routerId);
                    }else{
                        System.out.println(String.format("%s : disconnected",routerId));
                        states = "disconnected";
                        clientList.remove(routerId);
                    }

                }else{
                    int id = ByteBuffer.wrap(getId(identify)).getInt();
                    System.out.println( id + " : "+ message);
                    System.out.println( byteArrayToHex(getId(id)) + " : "+ message);

                    assertThat(identify).isEqualTo(getId(id));

                    socket.send(identify,SendFlag.SEND_MORE);
                    socket.send(message.getBytes(),SendFlag.DONT_WAIT);
                }

            }
        }



    }
    @Test
    public void testStreamSocket() throws IOException, InterruptedException {

        StreamServer server = new StreamServer("tcp://127.0.0.1:5555");
        Thread thread = new Thread(server);
        thread.start();


        Thread.sleep(100);

        Socket socket = new Socket();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1",5555);
        socket.connect(address);

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hello world".getBytes());


        InputStream inputStream = socket.getInputStream();

        byte[] message = new byte[11];
        inputStream.read(message);

        System.out.println("received : "+ new String(message));

        socket.close();

        Thread.sleep(100);

        server.finish();

        System.out.println("server close");

    }



    @Test
    public void testStreamWebSocket() throws IOException, InterruptedException, URISyntaxException, ExecutionException {

        StreamServer server = new StreamServer("ws://127.0.0.1:8080");
        Thread thread = new Thread(server);
        thread.start();

        ZSocket client = new ZSocket(SocketType.STREAM);
        client.connect("ws://127.0.0.1:8080");
        byte[] identify = client.recv();
//        client.recv();
//
//        client.send(identify,SendFlag.SEND_MORE);
//        client.send("hello world".getBytes(),SendFlag.WAIT);



        //String response2 = new String(client.recv());
        //System.out.println(response2);

//        StreamServer server = new StreamServer();
//        Thread thread = new Thread(new StreamServer());
//        thread.start();
//
//
//        Thread.sleep(100);


//        WebSocket socket = new WebSocket();
//        InetSocketAddress address = new InetSocketAddress("127.0.0.1",5555);
//        socket.connect(address);
//
//        OutputStream outputStream = socket.getOutputStream();
//        outputStream.write("hello world".getBytes());
//
//
//        InputStream inputStream = socket.getInputStream();
//
//        byte[] message = new byte[11];
//        inputStream.read(message);
//
//        System.out.println("received : "+ new String(message));
//
//        socket.close();
//
//        Thread.sleep(100);
//
//        server.finish();
//
//        System.out.println("server close");

    }

}
