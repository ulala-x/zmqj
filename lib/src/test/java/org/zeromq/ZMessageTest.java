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
}
