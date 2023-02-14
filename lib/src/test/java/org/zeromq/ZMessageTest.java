package org.zeromq;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ZMessageTest {

    @Test
    public void testMessageEquals() {
        ZMessage message = new ZMessage();
        ZFrame hello = new ZFrame("Hello".getBytes());
        ZFrame world = new ZFrame("World".getBytes());
        message.add(hello);
        message.add(world);
        assertThat(message.size()).isEqualTo(2);

        ZMessage reverseMsg = new ZMessage();
        message.add(world);
        message.addFirst(hello);
        assertThat(reverseMsg).isEqualTo(reverseMsg);
    }

    @Test
    public void testSingleFrameMessage() throws InterruptedException {
        ZContext ctx = new ZContext(1);

        ZSocket output = new ZSocket(ctx,SocketType.PAIR);
        output.bind("inproc://zmsg.test");
        ZSocket input = new ZSocket(ctx,SocketType.PAIR);
        input.connect("inproc://zmsg.test");

        // Test send and receive of a single ZMsg
        ZMessage sendMessage = new ZMessage();
        ZFrame frame = new ZFrame("Hello".getBytes());
        sendMessage.addFirst(frame);
        assertThat(sendMessage.size()).isEqualTo(1);
        assertThat(sendMessage.contentSize()).isEqualTo(5);

        output.send(sendMessage,false);

        ZMessage receiveMessage = new ZMessage();
        assertThat(input.receive(receiveMessage,false)).isTrue();
        assertThat(receiveMessage.size()).isEqualTo(1);
        assertThat(receiveMessage.contentSize()).isEqualTo(5);

        sendMessage.close();
        receiveMessage.close();

        output.close();
        input.close();
        ctx.close();
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


        ZMessage sendMessage = new ZMessage();
        sendMessage.add(new ZFrame("Server".getBytes()));
        sendMessage.add(new ZFrame("hello".getBytes()));

        clientSocket.send(sendMessage);


        String routingId =new String(serverSocket.recv(RecvFlag.WAIT));
        String message = new String(serverSocket.recv(RecvFlag.WAIT));

        assertThat(routingId).isEqualTo("Client");
        assertThat(message).isEqualTo("hello");
    }



}
