package org.zeromq;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ZPollerTest {

    @Test
    public void testPollerUnregister() {
        ZContext context = new ZContext(1);
        ZSocket socketOne = new ZSocket(context,SocketType.SUB);
        ZSocket socketTwo = new ZSocket(context,SocketType.REP);
        ZPoller poller = new ZPoller(2);
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
        ZContext context = new ZContext(1);
        ZSocket socketOne = new ZSocket(context,SocketType.SUB);
        ZPoller poller = new ZPoller(2);


        poller.register(socketOne, PollEvent.POLLIN);
        socketOne.close();

        assertThatThrownBy(()->{
            poller.poll(100);
        }).isInstanceOf(ZMQException.class);

        context.term();
    }

}
