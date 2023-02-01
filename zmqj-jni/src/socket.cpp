/*
    Copyright (c) 2007-2013 Contributors as noted in the AUTHORS file

    This file is part of 0MQ.

    0MQ is free software; you can redistribute it and/or modify it under
    the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    0MQ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#include <assert.h>
#include <string.h>
#include <zmq.h>

#include "zmqj.h"
#include "util.h"
#include "org_zeromq_ZMQSocket.h"

static jfieldID  socketHandleFID;
static jmethodID contextHandleMID;
static jmethodID limitMID;
static jmethodID positionMID;
static jmethodID setPositionMID;
static jfieldID  msgHandleFID;

static zmq_msg_t* do_read(JNIEnv *env, jobject obj, zmq_msg_t *message, int flags);

inline void *get_socket (JNIEnv *env, jobject obj)
{
    return (void*) env->GetLongField (obj, socketHandleFID);
}

inline void put_socket (JNIEnv *env, jobject obj, void *s)
{
    env->SetLongField (obj, socketHandleFID, (jlong) s);
}

inline void *fetch_context (JNIEnv *env, jobject context)
{
    return (void*) env->CallLongMethod (context, contextHandleMID);
}

inline void *get_message (JNIEnv *env, jobject obj)
{
    return (void*) env->GetLongField (obj, msgHandleFID);
}

inline void put_message (JNIEnv *env, jobject obj, void *msg)
{

    env->SetLongField (obj, msgHandleFID, (jlong) msg);
}


//inline void *fetch_msg (JNIEnv *env, jobject msg)
//{
//    return (void*) env->CallLongMethod (msg, msgHandleMID);
//}
//
//inline void clear_msg(JNIEnv *env, jobject msg){
//
//    delete message;
//     message = null;
//
//}

JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1nativeInit(JNIEnv *env, jclass c)
{
    jclass bbcls = env->FindClass("java/nio/ByteBuffer");
    limitMID = env->GetMethodID(bbcls, "limit", "()I");
    positionMID = env->GetMethodID(bbcls, "position", "()I");
    setPositionMID = env->GetMethodID(bbcls, "position", "(I)Ljava/nio/Buffer;");
    env->DeleteLocalRef(bbcls);

    jclass contextcls = env->FindClass("org/zeromq/ZMQContext");
    contextHandleMID = env->GetMethodID(contextcls, "getContextHandle", "()J");
    env->DeleteLocalRef(contextcls);
    socketHandleFID = env->GetFieldID(c, "socketHandle", "J");

    jclass msgcls = env->FindClass("org/zeromq/ZFrame");
    msgHandleFID = env->GetFieldID(msgcls, "msgHandle", "J");
//    msgHandleMID = env->GetMethodID(msgcls, "getMsgHandle", "()J");
    env->DeleteLocalRef(msgcls);

}





JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1construct(JNIEnv * env, jobject obj, jobject context, jint type)
{
    void *s = get_socket (env, obj);
    if (s)
        return;

    void *c = fetch_context (env, context);
    if (c == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    s = zmq_socket (c, type);
    int err = zmq_errno();

    if (s == NULL) {
        raise_exception (env, err);
        return;
    }
    put_socket(env, obj, s);
}

/**
 * Called to destroy a Java Socket object.
 */
JNIEXPORT void JNICALL Java_org_zeromq_ZMQContext_destroy(JNIEnv * env, jobject obj)
{
    void *s = get_socket (env, obj);
    if (! s)
        return;

    int rc = zmq_close (s);
    int err = zmq_errno();
    s = NULL;
    put_socket (env, obj, s);

    if (rc != 0) {
        raise_exception (env, err);
        return;
    }
}

JNIEXPORT jboolean JNICALL Java_org_zeromq_ZMQSocket__1getBooleanSockopt(JNIEnv * env, jobject obj, jint option)
{
    void *s = get_socket (env, obj);
    jboolean ret = JNI_FALSE;
    int rc = 0;
    int err = 0;
    
    int optval = 0;
    size_t optvallen = sizeof(optval);
    rc = zmq_getsockopt (s, option, &optval, &optvallen);
    ret =  (optval == 1) ? JNI_TRUE : JNI_FALSE;

    err = zmq_errno();

    if (rc != 0) {
        raise_exception (env, err);
        return 0L;
    }
    return ret;
}

JNIEXPORT jint JNICALL Java_org_zeromq_ZMQSocket__1getIntSockopt(JNIEnv * env, jobject obj, jint option)
{
    void *s = get_socket (env, obj);
    jint ret = 0;
    int rc = 0;
    int err = 0;
    
    int optval = 0;
    size_t optvallen = sizeof(optval);
    rc = zmq_getsockopt (s, option, &optval, &optvallen);
    ret = optval;

    err = zmq_errno();

    if (rc != 0) {
        raise_exception (env, err);
        return 0L;
    }
    return ret;
}

JNIEXPORT jlong JNICALL Java_org_zeromq_ZMQSocket__1getLongSockopt(JNIEnv * env, jobject obj, jlong option)
{
    void *s = get_socket (env, obj);
    jlong ret = 0;
    int rc = 0;
    int err = 0;
    int64_t optval = 0; 
    size_t optvallen = sizeof(optval);
    rc = zmq_getsockopt (s, option, &optval, &optvallen);
    ret = (jlong) optval;
    
    err = zmq_errno();

    if (rc != 0) {
        raise_exception (env, err);
        return 0L;
    }
    return ret;
}
JNIEXPORT jbyteArray JNICALL Java_org_zeromq_ZMQSocket__1getBytesSockopt(JNIEnv* env, jobject obj, jint option,jint optValSize)
{
    void *s = get_socket (env, obj);
    // Warning: hard-coded limit here.
    size_t optvallen = optValSize;
    char optval[optvallen];

    memset(&optval,-1,optvallen);
    
    if(option == ZMQ_CURVE_PUBLICKEY){
        optvallen = 32;
    }

    int rc = zmq_getsockopt (s, option, optval, &optvallen);
    int err = zmq_errno();
    if (rc != 0) {
        raise_exception (env, err);
        return env->NewByteArray (0);
    }

    jbyteArray array = env->NewByteArray (optvallen);
    if (array == NULL) {
        raise_exception (env, EINVAL);
        return env->NewByteArray(0);
    }

    env->SetByteArrayRegion (array, 0, optvallen, (jbyte*) optval);
    return array;
}


/*
 * Called by Java's Socket::setBoolSockopt(int option, long value).
 */
JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1setBooleanSockopt  (JNIEnv * env, jobject obj, jint option, jboolean value)
{
    void *s = get_socket (env, obj);
    int rc = 0;
    int err = 0;

    int optval =  (value == JNI_TRUE) ? 1: 0;
    size_t optvallen = sizeof(optval);
    rc = zmq_setsockopt (s, option, &optval, optvallen);
    err = zmq_errno();

    if (rc != 0 && err != ETERM) {
        raise_exception (env, err);
    }
}


/*
 * Called by Java's Socket::setIntSockopt(int option, long value).
 */
JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1setIntSockopt  (JNIEnv * env, jobject obj, jint option, jint value)
{
    void *s = get_socket (env, obj);
    int rc = 0;
    int err = 0;

    int optval = (int) value;
    size_t optvallen = sizeof(optval);
    rc = zmq_setsockopt (s, option, &optval, optvallen);
    err = zmq_errno();

    if (rc != 0 && err != ETERM) {
        raise_exception (env, err);
    }
}

/*
 * Called by Java's Socket::setLongSockopt(int option, long value).
 */
JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1setLongSockopt  (JNIEnv * env, jobject obj, jint option, jlong value)
{
    void *s = get_socket (env, obj);
    int rc = 0;
    int err = 0;

    uint64_t optval = (uint64_t) value;
    size_t optvallen = sizeof(optval);
    rc = zmq_setsockopt (s, option, &optval, optvallen);
    err = zmq_errno();

    if (rc != 0 && err != ETERM) {
        raise_exception (env, err);
    }
}


/**
 * Called by Java's Socket::setBytesSockopt(int option, byte[] value).
 */
JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1setBytesSockopt(JNIEnv * env, jobject obj, jint option, jbyteArray value)
{
    if (value == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    void *s = get_socket (env, obj);

    jbyte *optval = env->GetByteArrayElements (value, NULL);
    if (! optval) {
        raise_exception (env, EINVAL);
        return;
    }
    size_t optvallen = env->GetArrayLength (value);
    int rc = zmq_setsockopt (s, option, optval, optvallen);
    int err = zmq_errno();
    env->ReleaseByteArrayElements (value, optval, 0);
    if (rc != 0) {
        raise_exception (env, err);
    }
}

/**
 * Called by Java's Socket::bind(String addr).
 */
JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1bind(JNIEnv* env, jobject obj, jstring addr)
{
    void *s = get_socket (env, obj);

    if (addr == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    const char *c_addr = env->GetStringUTFChars (addr, NULL);
    if (c_addr == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    int rc = zmq_bind (s, c_addr);
    int err = zmq_errno();
    env->ReleaseStringUTFChars (addr, c_addr);

    if (rc != 0) {
        raise_exception (env, err);
        return;
    }
}

/**
 * Called by Java's Socket::unbind(String addr).
 */
JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1unbind(JNIEnv* env, jobject obj, jstring addr)
{
    void *s = get_socket (env, obj);

    if (addr == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    const char *c_addr = env->GetStringUTFChars (addr, NULL);
    if (c_addr == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    int rc = zmq_unbind (s, c_addr);
    int err = zmq_errno();
    env->ReleaseStringUTFChars (addr, c_addr);

    if (rc != 0) {
        raise_exception (env, err);
        return;
    }
}

/**
 * Called by Java's Socket::connect(String addr).
 */
JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1connect(JNIEnv *env, jobject obj, jstring addr)
{
    void *s = get_socket (env, obj);

    if (addr == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    const char *c_addr = env->GetStringUTFChars (addr, NULL);
    if (c_addr == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    int rc = zmq_connect (s, c_addr);
    int err = zmq_errno();
    env->ReleaseStringUTFChars (addr, c_addr);

    if (rc != 0) {
        raise_exception (env, err);
        return;
    }
}

/**
 * Called by Java's Socket::disconnect(String addr).
 */
 JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1disconnect(JNIEnv * env, jobject obj, jstring addr)
{
    void *s = get_socket (env, obj);

    if (addr == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    const char *c_addr = env->GetStringUTFChars (addr, NULL);
    if (c_addr == NULL) {
        raise_exception (env, EINVAL);
        return;
    }

    int rc = zmq_disconnect (s, c_addr);
    int err = zmq_errno();
    env->ReleaseStringUTFChars (addr, c_addr);

    if (rc != 0) {
        raise_exception (env, err);
        return;
    }
}

JNIEXPORT void JNICALL Java_org_zeromq_ZMQSocket__1destroy(JNIEnv * env, jobject obj)
{
    void *s = get_socket (env, obj);
    if (! s)
        return;

    int rc = zmq_close (s);
    int err = zmq_errno();
    s = NULL;
    put_socket (env, obj, s);

    if (rc != 0) {
        raise_exception (env, err);
        return;
    }
}


/**
 * Called by Java's Socket::send(byte [] msg, int offset, int flags).
 */
 JNIEXPORT jboolean JNICALL Java_org_zeromq_ZMQSocket__1send___3BIII(JNIEnv *env,
                                                            jobject obj, 
                                                            jbyteArray msg, 
                                                            jint offset, 
                                                            jint length, 
                                                            jint flags)
{
    void *s = get_socket (env, obj);

    if (length < 0) {
        raise_exception(env, EINVAL);
        return JNI_FALSE;
    }

    zmq_msg_t message;
    int rc = zmq_msg_init_size (&message, length);
    int err = zmq_errno();
    if (rc != 0) {
        raise_exception (env, err);
        return JNI_FALSE;
    }
    
    void* pd = zmq_msg_data (&message);
    env->GetByteArrayRegion(msg, offset, length, (jbyte*) pd);

    rc = zmq_msg_send(&message,s,flags);

    err = zmq_errno();

    if (rc < 0 && err == EAGAIN) {
        rc = zmq_msg_close (&message);
        err = zmq_errno();
        if (rc != 0) {
            raise_exception (env, err);
            return JNI_FALSE;
        }
        return JNI_FALSE;
    }
    
    if (rc < 0) {
        raise_exception (env, err);
        rc = zmq_msg_close (&message);
        err = zmq_errno();
        if (rc != 0) {
            raise_exception (env, err);
            return JNI_FALSE;
        }
        return JNI_FALSE;
    }

    rc = zmq_msg_close (&message);
    err = zmq_errno();
    if (rc != 0) {
        raise_exception (env, err);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/**
 * Called by Java's Socket::send(byte [] msg, int offset, int flags).
 */
 JNIEXPORT jboolean JNICALL Java_org_zeromq_ZMQSocket__1send__Lorg_zeromq_ZFrame_2I(
                                                            JNIEnv * env,
                                                            jobject obj,
                                                            jobject zmqMsg,
                                                            jint flags)
{
    void *s = get_socket (env, obj);


    zmq_msg_t* message = (zmq_msg_t*) get_message(env,zmqMsg);

    if(message == NULL){
        raise_exception(env, EINVAL);
        return JNI_FALSE;
    }
//    zmq_msg_t message;
//    int rc = zmq_msg_init_size (&message, length);
//    int err = zmq_errno();
//    if (rc != 0) {
//        raise_exception (env, err);
//        return JNI_FALSE;
//    }
//
//    void* pd = zmq_msg_data (&message);
//    env->GetByteArrayRegion(msg, offset, length, (jbyte*) pd);

    int rc = zmq_msg_send(message,s,flags);

    int err = zmq_errno();

    if (rc < 0 && err == EAGAIN) {
        rc = zmq_msg_close (message);
        err = zmq_errno();

        delete message;
        message = NULL;
        put_message(env,zmqMsg,message);

        if (rc != 0) {
            raise_exception (env, err);
            return JNI_FALSE;
        }
        return JNI_FALSE;
    }

    if (rc < 0) {
        raise_exception (env, err);
        rc = zmq_msg_close (message);
        err = zmq_errno();

        delete message;
        message = NULL;
        put_message(env,zmqMsg,message);

        if (rc != 0) {
            raise_exception (env, err);
            return JNI_FALSE;
        }
        return JNI_FALSE;
    }

    rc = zmq_msg_close (message);
    err = zmq_errno();

    delete message;
    message = NULL;
    put_message(env,zmqMsg,message);

    if (rc != 0) {
        raise_exception (env, err);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

JNIEXPORT jint JNICALL Java_org_zeromq_ZMQSocket__1recv___3BIII (JNIEnv *env,
                                                                     jobject obj, 
                                                                     jbyteArray buff, 
                                                                     jint offset, 
                                                                     jint len, 
                                                                     jint flags)
{
    zmq_msg_t message;
    if (!do_read(env,obj,&message,flags)) {
        return -1;
    }
    // No errors are defined for these two functions. Should they?
    int sz = zmq_msg_size (&message);
    void* pd = zmq_msg_data (&message);
    
    int stored = sz > len ? len : sz;
    env->SetByteArrayRegion (buff, offset, stored, (jbyte*) pd);

    int rc = zmq_msg_close(&message);
    if(rc == -1) {
        int err = zmq_errno();
        raise_exception (env, err);
        return -1;
    } 
    return stored;
}


/**
 * Called by Java's Socket::recv(int flags).
 */
JNIEXPORT jbyteArray JNICALL Java_org_zeromq_ZMQSocket__1recv__I(JNIEnv *env,
                                                                jobject obj,
                                                                jint flags)
{
    zmq_msg_t message;
    if (!do_read(env,obj,&message,flags)) {
        return NULL;
    }
    // No errors are defined for these two functions. Should they?
    int sz = zmq_msg_size (&message);
    void* pd = zmq_msg_data (&message);

    jbyteArray data = env->NewByteArray (sz);
    if (! data) {
        raise_exception (env, EINVAL);
        return NULL;
    }

    env->SetByteArrayRegion (data, 0, sz, (jbyte*) pd);

    int rc = zmq_msg_close(&message);
    if(rc == -1) {
        int err = zmq_errno();
        raise_exception (env, err);
        return NULL;
    } 
    return data;
}

JNIEXPORT jobject JNICALL Java_org_zeromq_ZMQSocket__1receive(JNIEnv *env,
                                                              jobject obj,
                                                              jint flags)
{
    zmq_msg_t* message = new zmq_msg_t();
    if (!do_read(env,obj,message,flags)) {
        return NULL;
    }

   jclass clz = env->FindClass ("org/zeromq/ZFrame");
   assert (clz);
   jmethodID midInit = env->GetMethodID (clz, "<init>", "(J)V");
   assert (midInit);
   jobject result = env->NewObject (clz, midInit, (void*)message);
   assert (result);

   return result;
}


static
zmq_msg_t *do_read(JNIEnv *env, jobject obj, zmq_msg_t *message, int flags)
{
    void *socket = get_socket (env, obj);

    int rc = zmq_msg_init (message);
    if (rc != 0) {
        raise_exception (env, zmq_errno());
        return NULL;
    }

    
    rc = zmq_msg_recv (message,socket, flags);

    int err = zmq_errno();
    if (rc < 0 && err == EAGAIN) {
        rc = zmq_msg_close (message);
        err = zmq_errno();
        if (rc != 0) {
            raise_exception (env, err);
            return NULL;
        }
        return NULL;
    }
    if (rc < 0) {
        raise_exception (env, err);
        rc = zmq_msg_close (message);
        err = zmq_errno();
        if (rc != 0) {
            raise_exception (env, err);
            return NULL;
        }
        return NULL;
    }
    return message;
}

JNIEXPORT jboolean JNICALL Java_org_zeromq_ZMQSocket__1monitor(JNIEnv *env,
                                                            jobject obj,
                                                            jstring addr,
                                                            jint events)
{
  void *socket = get_socket (env, obj);

  const char *c_addr = addr ? env->GetStringUTFChars (addr, NULL) : NULL;

  int rc = zmq_socket_monitor(socket , c_addr, events);
  int err = rc < 0 ? zmq_errno() : 0;

  env->ReleaseStringUTFChars (addr, c_addr);

  if (rc < 0) {
      raise_exception (env, err);
      return JNI_FALSE;
  }
  return JNI_TRUE;
 }
