package org.zeromq;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ZMQTest {
    @Test
    public void testMakeVersion() {
        assertThat(ZMQ.getFullVersion()).isEqualTo(
                ZMQ.makeVersion(ZMQ.getMajorVersion(), ZMQ.getMinorVersion(), ZMQ.getPatchVersion())
        );
    }

    /**
     * Test method for {@link org.zeromq.ZMQ#getVersionString()}.
     */
    @Test
    public void testGetVersion() {
        assertThat(ZMQ.getMajorVersion() + "." + ZMQ.getMinorVersion() + "." + ZMQ.getPatchVersion()).isEqualTo(
                ZMQ.getVersionString()
        );
    }

    /**
     * Test method for {@link ZSocket#bindToRandomPort(String)}.
     */
    @Test
    public void testBindToRandomPort() {
        ZContext context = new ZContext(1);
        ZSocket socket = new ZSocket(context, SocketType.DEALER);

        // Check that bindToRandomport generate valid port number
        for (int i = 0; i < 100; i++) {
            socket.bindToRandomPort("tcp://127.0.0.1");
        }

        socket.close();
        final ZSocket dealerSocket = new ZSocket(context, SocketType.DEALER);

        assertThatThrownBy(() -> {
            dealerSocket.bindToRandomPort("noprotocol://127.0.0.1");
        }).isInstanceOf(ZMQException.class);

        try {
            dealerSocket.bindToRandomPort("noprotocol://127.0.0.1");
        } catch (ZMQException e) {
            assertThat(e.getErrorCode()).isEqualTo(ZMQ.EPROTONOSUPPORT());
        }
    }

    /**
     * Test method for {@link ZSocket#bindToSystemRandomPort(String)}.
     */
    @Test
    public void testBindToSystemRandomPort() {
        ZSocket socket = new ZSocket(SocketType.REP);

        socket.bindToSystemRandomPort("tcp://127.0.0.1");
        socket.close();
    }

    @Test
    public  void testError(){

        System.out.println(ZMQ.zmq_error_msg(ZMQ.zmq_errno()));

    }

}
