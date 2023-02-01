package org.zeromq;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ZMQPollerTest {

    @Test
    public void testPollerUnregister() {
        ZMQContext context = new ZMQContext(1);
        ZMQSocket socketOne = new ZMQSocket(context,SocketType.SUB);
        ZMQSocket socketTwo = new ZMQSocket(context,SocketType.REP);
        ZMQPoller poller = new ZMQPoller(2);
        poller.register(socketOne, PollEvent.POLLIN);
        poller.register(socketTwo, PollEvent.POLLIN);

        socketOne.linger(0);
        socketOne.close();
        socketTwo.linger(0);
        socketTwo.close();

        poller.unregister(socketOne);
        poller.unregister(socketTwo);

        context.term();
    }

    @Test
    public void testPollingInvalidSockets() {
        ZMQContext context = new ZMQContext(1);
        ZMQSocket socketOne = new ZMQSocket(context,SocketType.SUB);
        ZMQPoller poller = new ZMQPoller(2);


        poller.register(socketOne, PollEvent.POLLIN);
        socketOne.close();

        assertThatThrownBy(()->{
            poller.poll(100);
        }).isInstanceOf(ZMQException.class);

        context.term();
    }

}
