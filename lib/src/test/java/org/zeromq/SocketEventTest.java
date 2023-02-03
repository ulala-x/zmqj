package org.zeromq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class SocketEventTest {

    @ParameterizedTest
    @EnumSource(value= EventType.class,names = {"EVENT_CONNECTED"})
    public void testEventConnected(EventType event) {

        try(ZContext context = new ZContext(1);
            ZSocket reqSocket = new ZSocket(context, SocketType.REQ);
            ZSocket repSocket = new ZSocket(context, SocketType.REP);
            ZSocket monitor = new ZSocket(context, SocketType.PAIR)){

            int port = reqSocket.bindToRandomPort("tcp://127.0.0.1");

            monitor.receiveTimeout(100);

            assertThat(repSocket.monitor("inproc://monitor.socket", event)).isTrue();
            monitor.connect("inproc://monitor.socket");

            repSocket.connect("tcp://127.0.0.1:" + port);


            ZEvent zEvent = ZEvent.recv(monitor);
            assertThat(zEvent).isNotNull();
            assertThat(zEvent.getEvent()).isEqualTo(event);
            assertThat(zEvent.getAddress()).isEqualTo("tcp://127.0.0.1:" + port);
        }
    }
    @ParameterizedTest
    @EnumSource(value= EventType.class,names = {"EVENT_CONNECT_DELAYED","EVENT_CONNECT_RETRIED"})
    public void testEventConnect(EventType event) {

        try(ZContext context = new ZContext(1);
            ZSocket socket = new ZSocket(context, SocketType.REP);
            ZSocket monitor = new ZSocket(context, SocketType.PAIR)){

            assertThat(socket.monitor("inproc://monitor.socket", event)).isTrue();
            monitor.connect("inproc://monitor.socket");
            socket.connect("tcp://127.0.0.1:6751");
            ZEvent zEvent = ZEvent.recv(monitor);
            assertThat(zEvent.getEvent()).isEqualTo(event);
        }
    }


    @Test
    public void testEventListening() {
        try(ZContext context = new ZContext(1);
            ZSocket socket = new ZSocket(context, SocketType.REQ);
            ZSocket monitor = new ZSocket(context, SocketType.PAIR);
        ){
            assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_LISTENING)).isTrue();
            monitor.connect("inproc://monitor.socket");

            socket.bindToRandomPort("tcp://127.0.0.1");
            ZEvent event = ZEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_LISTENING);
        }
    }
//
    @Test
    public void testEventBindFailed() {
        try(ZContext context = new ZContext(1);
            ZSocket socket = new ZSocket(context, SocketType.REQ);
            ZSocket monitor = new ZSocket(context, SocketType.PAIR);
        ){
            assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_BIND_FAILED)).isTrue();
            monitor.connect("inproc://monitor.socket");

            try {
                socket.bind("tcp://192.0.0.1");
            } catch (ZMQException ex) {}

            ZEvent event = ZEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_BIND_FAILED);
        }
    }
//
    @Test
    public void testEventAccepted() {
        try(ZContext context = new ZContext(1);
            ZSocket socket = new ZSocket(context, SocketType.REQ);
            ZSocket monitor = new ZSocket(context, SocketType.PAIR);
            ZSocket client = new ZSocket(context, SocketType.REP)
        ) {
            assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_ACCEPTED)).isTrue();
            monitor.connect("inproc://monitor.socket");

            int port = socket.bindToRandomPort("tcp://127.0.0.1");
            client.connect("tcp://127.0.0.1:" + port);
            ZEvent event = ZEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_ACCEPTED);
        }
    }

    @Test
    public void testEventClosed() {

        ZContext context = new ZContext(1);
        ZSocket socket = new ZSocket(context, SocketType.REQ);
        ZSocket monitor = new ZSocket(context, SocketType.PAIR);

        assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_CLOSED)).isTrue();
        monitor.connect("inproc://monitor.socket");

        socket.bindToRandomPort("tcp://127.0.0.1");

        socket.close();

        ZEvent event = ZEvent.recv(monitor);

        assertThat(event.getEvent()).isEqualTo(EventType.EVENT_CLOSED);

        monitor.close();
        context.term();
    }

    @Test
    public void testEventDisconnectedWithServer() {
        try(ZContext context = new ZContext(1);
            ZSocket server = new ZSocket(context, SocketType.REQ);
            ZSocket monitor = new ZSocket(context, SocketType.PAIR);
            ZSocket client = new ZSocket(context, SocketType.REP)
        ) {
            assertThat(server.monitor("inproc://monitor.socket", EventType.EVENT_DISCONNECTED)).isTrue();
            monitor.connect("inproc://monitor.socket");

            int port = server.bindToRandomPort("tcp://127.0.0.1");
            client.connect("tcp://127.0.0.1:" + port);
            client.close();
            ZEvent event = ZEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_DISCONNECTED);
            System.out.println(event.getAddress());
        }
    }
    @Test
    public void testEventDisconnectedWithClient() throws InterruptedException {
        try(ZContext context = new ZContext(1);
            ZSocket server = new ZSocket(context, SocketType.REP);
            ZSocket monitor = new ZSocket(context, SocketType.PAIR);
            ZSocket client = new ZSocket(context, SocketType.REQ)
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

            ZEvent event = ZEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_DISCONNECTED);
            System.out.println(event.getAddress());
        }
    }

    @Test
    public void testEventMonitorStopped() {
        try(ZContext context = new ZContext(1);
            ZSocket socket = new ZSocket(context, SocketType.REQ);
            ZSocket monitor = new ZSocket(context, SocketType.PAIR);
        ){
            assertThat(socket.monitor("inproc://monitor.socket", EventType.EVENT_MONITOR_STOPPED)).isTrue();
            monitor.connect("inproc://monitor.socket");

            socket.monitor(null, EventType.NONE);

            ZEvent event = ZEvent.recv(monitor);
            assertThat(event.getEvent()).isEqualTo(EventType.EVENT_MONITOR_STOPPED);
        }
    }


}
