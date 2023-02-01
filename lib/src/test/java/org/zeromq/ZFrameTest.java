package org.zeromq;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class ZFrameTest {

    @Test
    void createTest(){
        byte[] message = "hellow".getBytes();
        ZFrame frame = new ZFrame(message,0,message.length);
        frame.close();
    }

    @Test
    void byteBufferTest(){
        ByteBuffer buffer = ByteBuffer.allocateDirect(100);
        buffer.put("hello".getBytes());

        ZFrame zeroCopyMsg = new ZFrame(buffer,true);
        zeroCopyMsg.close();

        ZFrame frame = new ZFrame(buffer,false);
        frame.close();
    }

    @Test
    void dataTest(){
        byte[] message = "hellow".getBytes();
        ZFrame frame = new ZFrame(message,0,message.length);
        assertThat(frame.data()).isEqualTo(message);
        frame.close();
    }
}
