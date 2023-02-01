package org.zeromq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class SocketEventTest {

    @ParameterizedTest
    @EnumSource(value= EventType.class,names = {"EVENT_CONNECTED"})
    public void testEventConnected(EventType event) {

        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket reqSocket = new ZMQSocket(context, SocketType.REQ);
            ZMQSocket repSocket = new ZMQSocket(context, SocketType.REP);
            ZMQSocket monitor = new ZMQSocket(context, SocketType.PAIR)){

            int port = reqSocket.bindToRandomPort("tcp://127.0.0.1");

            monitor.receiveTimeout(100);

            assertThat(repSocket.monitor("inproc://monitor.socket", event)).isTrue();
            monitor.connect("inproc://monitor.socket");

            repSocket.connect("tcp://127.0.0.1:" + port);


            ZMQEvent zmqEvent = ZMQEvent.recv(monitor);
            assertThat(zmqEvent).isNotNull();
            assertThat(zmqEvent.getEvent()).isEqualTo(event);
            assertThat(zmqEvent.getAddress()).isEqualTo("tcp://127.0.0.1:" + port);
        }
    }
    @ParameterizedTest
    @EnumSource(value= EventType.class,names = {"EVENT_CONNECT_DELAYED","EVENT_CONNECT_RETRIED"})
    public void testEventConnect(EventType event) {

        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket socket = new ZMQSocket(context, SocketType.REP);
            ZMQSocket monitor = new ZMQSocket(context, SocketType.PAIR)){

            assertThat(socket.monitor("inproc://monitor.socket", event)).isTrue();
            monitor.connect("inproc://monitor.socket");
            socket.connect("tcp://127.0.0.1:6751");
            ZMQEvent zmqEvent = ZMQEvent.recv(monitor);
            assertThat(zmqEvent.getEvent()).isEqualTo(event);
        }
    }


    @Test
    public void testEventListening() {
        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket socket = new ZMQSocket(context, SocketType.REQ);
            ZMQSocket monitor = new ZMQSocket(context, SocketType.PAIR);
        ){
            assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_LISTENING)).isTrue();
            monitor.connect("inproc://monitor.socket");

            socket.bindToRandomPort("tcp://127.0.0.1");
            ZMQEvent event = ZMQEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_LISTENING);
        }
    }
//
    @Test
    public void testEventBindFailed() {
        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket socket = new ZMQSocket(context, SocketType.REQ);
            ZMQSocket monitor = new ZMQSocket(context, SocketType.PAIR);
        ){
            assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_BIND_FAILED)).isTrue();
            monitor.connect("inproc://monitor.socket");

            try {
                socket.bind("tcp://192.0.0.1");
            } catch (ZMQException ex) {}

            ZMQEvent event = ZMQEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_BIND_FAILED);
        }
    }
//
    @Test
    public void testEventAccepted() {
        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket socket = new ZMQSocket(context, SocketType.REQ);
            ZMQSocket monitor = new ZMQSocket(context, SocketType.PAIR);
            ZMQSocket client = new ZMQSocket(context, SocketType.REP)
        ) {
            assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_ACCEPTED)).isTrue();
            monitor.connect("inproc://monitor.socket");

            int port = socket.bindToRandomPort("tcp://127.0.0.1");
            client.connect("tcp://127.0.0.1:" + port);
            ZMQEvent event = ZMQEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_ACCEPTED);
        }
    }

    @Test
    public void testEventClosed() {

        ZMQContext context = new ZMQContext(1);
        ZMQSocket socket = new ZMQSocket(context, SocketType.REQ);
        ZMQSocket monitor = new ZMQSocket(context, SocketType.PAIR);

        assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_CLOSED)).isTrue();
        monitor.connect("inproc://monitor.socket");

        socket.bindToRandomPort("tcp://127.0.0.1");

        socket.close();

        ZMQEvent event = ZMQEvent.recv(monitor);

        assertThat(event.getEvent()).isEqualTo(EventType.EVENT_CLOSED);

        monitor.close();
        context.term();
    }

    @Test
    public void testEventDisconnectedWithServer() {
        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket server = new ZMQSocket(context, SocketType.REQ);
            ZMQSocket monitor = new ZMQSocket(context, SocketType.PAIR);
            ZMQSocket client = new ZMQSocket(context, SocketType.REP)
        ) {
            assertThat(server.monitor("inproc://monitor.socket", EventType.EVENT_DISCONNECTED)).isTrue();
            monitor.connect("inproc://monitor.socket");

            int port = server.bindToRandomPort("tcp://127.0.0.1");
            client.connect("tcp://127.0.0.1:" + port);
            client.close();
            ZMQEvent event = ZMQEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_DISCONNECTED);
            System.out.println(event.getAddress());
        }
    }
    @Test
    public void testEventDisconnectedWithClient() throws InterruptedException {
        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket server = new ZMQSocket(context, SocketType.REP);
            ZMQSocket monitor = new ZMQSocket(context, SocketType.PAIR);
            ZMQSocket client = new ZMQSocket(context, SocketType.REQ)
        ) {
            assertThat(client.monitor("inproc://monitor.socket", EventType.EVENT_DISCONNECTED)).isTrue();
            monitor.connect("inproc://monitor.socket");

            int port = server.bindToRandomPort("tcp://127.0.0.1");
            client.connect("tcp://127.0.0.1:" + port);
            client.send("hi".getBytes());

            Thread thread = new Thread(()->{
                try{
                    Thread.sleep(100);
                    server.close();
                }catch (Exception e){

                }

            });
            thread.start();

            ZMQEvent event = ZMQEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_DISCONNECTED);
            System.out.println(event.getAddress());
        }
    }

    @Test
    public void testEventMonitorStopped() {
        try(ZMQContext context = new ZMQContext(1);
            ZMQSocket socket = new ZMQSocket(context, SocketType.REQ);
            ZMQSocket monitor = new ZMQSocket(context, SocketType.PAIR);
        ){
            assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_MONITOR_STOPPED)).isTrue();
            monitor.connect("inproc://monitor.socket");

            socket.monitor(null, EventType.NONE);

            ZMQEvent event = ZMQEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_MONITOR_STOPPED);
        }
    }


}
