#include <zmq.h>

#include "zmqj.h"
#include "org_zeromq_ZMQ.h"



static void *get_socket (JNIEnv *env, jobject obj)
{
    jclass cls = env->GetObjectClass(obj);
    jfieldID socketHandleFID = env->GetFieldID (cls, "socketHandle", "J");
    env->DeleteLocalRef(cls);
    return (void*) env->GetLongField (obj, socketHandleFID);
}


JNIEXPORT jint JNICALL
Java_org_zeromq_ZMQ__1version_1full (JNIEnv *env, jclass cls)
{
    return ZMQ_VERSION;
}

JNIEXPORT jint JNICALL
Java_org_zeromq_ZMQ__1version_1major (JNIEnv *env, jclass cls)
{
    return ZMQ_VERSION_MAJOR;
}

JNIEXPORT jint JNICALL
Java_org_zeromq_ZMQ__1version_1minor (JNIEnv *env, jclass cls)
{
    return ZMQ_VERSION_MINOR;
}

JNIEXPORT jint JNICALL
Java_org_zeromq_ZMQ__1version_1patch (JNIEnv *env, jclass cls)
{
    return ZMQ_VERSION_PATCH;
}

JNIEXPORT jint JNICALL
Java_org_zeromq_ZMQ__1make_1version (JNIEnv *env, jclass cls, jint major, jint minor, jint patch)
{
    return ZMQ_MAKE_VERSION(major, minor, patch);
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1ENOTSUP (JNIEnv *env, jclass cls)
{
    return ENOTSUP;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1EPROTONOSUPPORT (JNIEnv *env, jclass cls)
{
    return EPROTONOSUPPORT;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1ENOBUFS (JNIEnv *env, jclass cls)
{
    return ENOBUFS;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1ENETDOWN (JNIEnv *env, jclass cls)
{
    return ENETDOWN;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1EADDRINUSE (JNIEnv *env, jclass cls)
{
    return EADDRINUSE;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1EADDRNOTAVAIL (JNIEnv *env, jclass cls)
{
    return EADDRNOTAVAIL;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1ECONNREFUSED (JNIEnv *env, jclass cls)
{
    return ECONNREFUSED;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1EINPROGRESS (JNIEnv *env, jclass cls)
{
    return EINPROGRESS;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1EHOSTUNREACH (JNIEnv *env, jclass cls)
{
    return EHOSTUNREACH;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1EMTHREAD (JNIEnv *env, jclass cls)
{
    return EMTHREAD;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1EFSM (JNIEnv *env, jclass cls)
{
    return EFSM;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1ENOCOMPATPROTO (JNIEnv *env, jclass cls)
{
    return ENOCOMPATPROTO;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1ETERM (JNIEnv *env, jclass cls)
{
    return ETERM;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1ENOTSOCK (JNIEnv *env, jclass cls)
{
    return ENOTSOCK;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ__1EAGAIN (JNIEnv *env, jclass cls)
{
    return EAGAIN;
}


JNIEXPORT void JNICALL
Java_org_zeromq_ZMQ__1proxy (JNIEnv *env, jclass cls, jobject frontend_, jobject backend_, jobject capture_)
{

    void *frontend = get_socket (env, frontend_);
    void *backend = get_socket (env, backend_);
    void *capture = NULL;
    if (capture_ != NULL)
        capture = get_socket (env, capture_);
    zmq_proxy (frontend, backend, capture);

}


