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
     * Test method for {@link ZMQSocket#bindToRandomPort(String)}.
     */
    @Test
    public void testBindToRandomPort() {
        ZMQContext context = new ZMQContext(1);
        ZMQSocket socket = new ZMQSocket(context, SocketType.DEALER);

        // Check that bindToRandomport generate valid port number
        for (int i = 0; i < 100; i++) {
            socket.bindToRandomPort("tcp://127.0.0.1");
        }

        socket.close();
        final ZMQSocket dealerSocket = new ZMQSocket(context, SocketType.DEALER);

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
     * Test method for {@link ZMQSocket#bindToSystemRandomPort(String)}.
     */
    @Test
    public void testBindToSystemRandomPort() {
        ZMQSocket socket = new ZMQSocket(SocketType.REP);

        socket.bindToSystemRandomPort("tcp://127.0.0.1");
        socket.close();
    }

}
