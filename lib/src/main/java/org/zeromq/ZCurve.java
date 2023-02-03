package org.zeromq;

public class ZCurve {

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY){
            System.loadLibrary("libzmqj");
        }
    }
    /**
     * A container for a public and a corresponding secret key
     */

    public static class KeyPair {
        public final String publicKey;
        public final String secretKey;

        public KeyPair(final String publicKey, final String secretKey) {
            this.publicKey = publicKey;
            this.secretKey = secretKey;
        }
    }

    /**
     * Returns a newly generated random keypair consisting of a public key
     * and a secret key.
     *
     * <p>The keys are encoded using {@link #z85Encode}.</p>
     *
     * @return Randomly generated {@link KeyPair}
     */
    public native static KeyPair _generateKeyPair();
    public static KeyPair generateKeyPair(){
        return _generateKeyPair();
    }

    /**
     * The function shall decode given key encoded as Z85 string into byte array.
     *
     * <p>The decoding shall follow the ZMQ RFC 32 specification.</p>
     *
     * @param key Key to be decoded
     * @return The resulting key as byte array
     */
    private native static byte[] _z85Decode(String key);
    public static byte[] z85Decode(String key){
        return _z85Decode(key);
    }

    /**
     * The function shall encode the binary block specified into a string.
     *
     * <p>The encoding shall follow the ZMQ RFC 32 specification.</p>
     *
     * @param key Key to be encoded
     * @return The resulting key as String in Z85
     */
    private native static String _z85Encode(byte[] key);
    public static String z85Encode(byte[] key){
        return  _z85Encode(key);
    }
}