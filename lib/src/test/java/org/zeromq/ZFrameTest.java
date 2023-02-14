package org.zeromq;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

import static org.assertj.core.api.Assertions.*;

public class ZFrameTest {

    @Test
    void createTest(){
        byte[] message = "hellow".getBytes();
        ZFrame frame = new ZFrame(message,0,message.length);
        frame.close();
    }

    @Test
    void byteBufferTest(){
        byte[] data = "hello".getBytes();

        ByteBuffer buffer = ByteBuffer.allocateDirect(100);
        buffer.put(data);

        ZFrame zeroCopyMsg = new ZFrame(buffer.flip(),true);

        assertThat(zeroCopyMsg.data()).isEqualTo(data);

        zeroCopyMsg.close();

        ZFrame frame = new ZFrame(buffer,false);
        frame.close();

        assertThatThrownBy(()->{
            new ZFrame(ByteBuffer.allocate(100));
        }).isInstanceOf(InvalidParameterException.class);

    }



    @Test
    void dataTest(){
        byte[] message = "hellow".getBytes();
        ZFrame frame = new ZFrame(message,0,message.length);
        assertThat(frame.data()).isEqualTo(message);
        frame.close();
    }

    @Test
    public void testReqRepWithZFrame() {

        ZContext context = new ZContext(1);
        ZSocket in = new ZSocket(context, SocketType.REQ);
        in.bind("inproc://reqrep");

        ZSocket out = new ZSocket(context, SocketType.REP);
        out.connect("inproc://reqrep");


        for (int i = 0; i < 100; i++) {
            byte[] req = ("request" + i).getBytes();
            byte[] rep = ("reply" + i).getBytes();

            ZFrame msgReq = new ZFrame(req);
            assertThat(in.send(msgReq, SendFlag.WAIT)).isTrue();

            try(ZFrame reqTmp = out.receive(RecvFlag.WAIT)){
                assertThat(reqTmp.data()).isEqualTo(req);
            }

            ZFrame msgRep = new ZFrame(rep);
            assertThat(out.send(msgRep, SendFlag.WAIT)).isTrue();
            try(ZFrame repTmp = in.receive(RecvFlag.WAIT)){
                assertThat(repTmp.data()).isEqualTo(rep);
            }
        }

        in.close();
        out.close();

        context.close();
    }

    @Test
    public void testReqRepWithZFrameWithByteBuffer() {

        ZContext context = new ZContext(1);
        ZSocket in = new ZSocket(context, SocketType.REQ);
        in.bind("inproc://reqrep");

        ZSocket out = new ZSocket(context, SocketType.REP);
        out.connect("inproc://reqrep");


        for (int i = 0; i < 100; i++) {
            byte[] req = ("request" + i).getBytes();
            byte[] rep = ("reply" + i).getBytes();

            var reqBuffer = ByteBuffer.allocateDirect(req.length).put(req).flip();
            var repBuffer = ByteBuffer.allocateDirect(req.length).put(rep).flip();

            ZFrame msgReq = new ZFrame(reqBuffer,true);
            assertThat(in.send(msgReq, SendFlag.WAIT)).isTrue();

            try(ZFrame reqTmp = out.receive(RecvFlag.WAIT)){
                assertThat(reqTmp.data()).isEqualTo(req);
            }

            ZFrame msgRep = new ZFrame(repBuffer,false);
            assertThat(out.send(msgRep, SendFlag.WAIT)).isTrue();
            try(ZFrame repTmp = in.receive(RecvFlag.WAIT)){
                assertThat(repTmp.data()).isEqualTo(rep);
            }
        }
    }

}
