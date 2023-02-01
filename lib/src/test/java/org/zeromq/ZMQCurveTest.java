package org.zeromq;

import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

public class ZMQCurveTest {
    @Test
    public void testCurveZ85Keys() {

        final Charset utf8 = Charset.forName("UTF-8");
        final String endpoint = "tcp://127.0.0.1:5000";

        final ZMQCurve.KeyPair req_key = ZMQCurve.generateKeyPair();
        final ZMQCurve.KeyPair rep_key = ZMQCurve.generateKeyPair();

        final byte[] req_pk = req_key.publicKey.getBytes(utf8);
        final byte[] req_sk = req_key.secretKey.getBytes(utf8);
        final byte[] rep_pk = rep_key.publicKey.getBytes(utf8);
        final byte[] rep_sk = rep_key.secretKey.getBytes(utf8);

        ZMQContext context = new ZMQContext(1);

        ZMQSocket rep = new ZMQSocket(context, SocketType.REP);
        rep.curveServer(true);
        rep.curveSecretKey(rep_sk);
        rep.bind(endpoint);

        ZMQSocket req =new ZMQSocket(context,SocketType.REQ);
        req.curvePublicKey(req_pk);
        req.curveSecretKey(req_sk);
        req.curveServerKey(rep_pk);
        req.connect(endpoint);

        final String sent = "Hello World";
        req.send(sent.getBytes());
        final String received = new String(rep.recv(),utf8);
        assertThat(received).isEqualTo(sent);

        req.close();
        rep.close();
        context.term();
    }

    @Test
    public void testCurveBinaryKeys() {

        final Charset utf8 = Charset.forName("UTF-8");
        final String endpoint = "tcp://127.0.0.1:5000";

        final byte[] req_pk = DatatypeConverter.parseHexBinary(
                "BB88471D65E2659B30C55A5321CEBB5AAB2B70A398645C26DCA2B2FCB43FC518");
        final byte[] req_sk = DatatypeConverter.parseHexBinary(
                "7BB864B489AFA3671FBE69101F94B38972F24816DFB01B51656B3FEC8DFD0888");
        final byte[] rep_pk = DatatypeConverter.parseHexBinary(
                "54FCBA24E93249969316FB617C872BB0C1D1FF14800427C594CBFACF1BC2D652");
        final byte[] rep_sk = DatatypeConverter.parseHexBinary(
                "8E0BDD697628B91D8F245587EE95C5B04D48963F79259877B49CD9063AEAD3B7");

        ZMQContext context = new ZMQContext(1);

        ZMQSocket rep = new ZMQSocket(context,SocketType.REP);
        rep.curveServer(true);
        rep.curveSecretKey(rep_sk);
        rep.bind(endpoint);

        ZMQSocket req = new ZMQSocket(context,SocketType.REQ);
        req.curvePublicKey(req_pk);
        req.curveSecretKey(req_sk);
        req.curveServerKey(rep_pk);
        req.connect(endpoint);

        final String sent = "Hello World";
        req.send(sent.getBytes());
        final String received = new String(rep.recv(),utf8);
        assertThat(received).isEqualTo(sent);

        req.close();
        rep.close();
        context.term();
    }
//
    @Test
    public void testKeyEncode() {
        final String expected = "Yne@$w-vo<fVvi]a<NY6T1ed:M$fCG*[IaLV{hID";
        final String actual = ZMQCurve.z85Encode(DatatypeConverter.parseHexBinary(
                "BB88471D65E2659B30C55A5321CEBB5AAB2B70A398645C26DCA2B2FCB43FC518"));
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void testKeyDecode() {
        final byte[] expected = DatatypeConverter.parseHexBinary(
                "BB88471D65E2659B30C55A5321CEBB5AAB2B70A398645C26DCA2B2FCB43FC518");
        final byte[] actual = ZMQCurve.z85Decode("Yne@$w-vo<fVvi]a<NY6T1ed:M$fCG*[IaLV{hID");
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void testKeyEncodeDecode() {
        for (int i = 0; i < 100; i++) {
            final ZMQCurve.KeyPair pair = ZMQCurve.generateKeyPair();
            assertThat(ZMQCurve.z85Encode(ZMQCurve.z85Decode(pair.publicKey))).isEqualTo(pair.publicKey);
            assertThat(ZMQCurve.z85Encode(ZMQCurve.z85Decode(pair.secretKey))).isEqualTo(pair.secretKey);
        }
    }
}
