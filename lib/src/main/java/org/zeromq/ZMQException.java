package org.zeromq;

public class ZMQException extends RuntimeException {
    private int errorCode = 0;

    public int getErrorCode() {
        return this.errorCode;
    }

    public ZMQException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ZMQException(ZMQException cause) {
        super(cause.getMessage(), cause);
        this.errorCode = cause.errorCode;
    }

    public ZMQException(int errorCode) {
        super(ZMQ.zmq_error_msg(errorCode));
        this.errorCode = errorCode;
    }

//    public static class InternalError extends ZMQException {
//        public InternalError() {
//            super(ZMQ.zmq_error_msg(ZMQ.zmq_errno()), ZMQ.zmq_errno());
//        }
//
//        public InternalError(int errorCode) {
//            super(errorCode);
//        }
//    }
//
//    public static class VersionError extends ZMQException {
//        public VersionError(String message) {
//            super(String.format("more than minimum %s version available", message), 0);
//        }
//    }
}