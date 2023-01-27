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
Java_org_zeromq_ZMQ_version_1full (JNIEnv *env, jclass cls)
{
    return ZMQ_VERSION;
}

JNIEXPORT jint JNICALL
Java_org_zeromq_ZMQ_version_1major (JNIEnv *env, jclass cls)
{
    return ZMQ_VERSION_MAJOR;
}

JNIEXPORT jint JNICALL
Java_org_zeromq_ZMQ_version_1minor (JNIEnv *env, jclass cls)
{
    return ZMQ_VERSION_MINOR;
}

JNIEXPORT jint JNICALL
Java_org_zeromq_ZMQ_version_1patch (JNIEnv *env, jclass cls)
{
    return ZMQ_VERSION_PATCH;
}

JNIEXPORT jint JNICALL
Java_org_zeromq_ZMQ_make_1version (JNIEnv *env, jclass cls, jint major, jint minor, jint patch)
{
    return ZMQ_MAKE_VERSION(major, minor, patch);
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_ENOTSUP (JNIEnv *env, jclass cls)
{
    return ENOTSUP;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_EPROTONOSUPPORT (JNIEnv *env, jclass cls)
{
    return EPROTONOSUPPORT;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_ENOBUFS (JNIEnv *env, jclass cls)
{
    return ENOBUFS;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_ENETDOWN (JNIEnv *env, jclass cls)
{
    return ENETDOWN;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_EADDRINUSE (JNIEnv *env, jclass cls)
{
    return EADDRINUSE;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_EADDRNOTAVAIL (JNIEnv *env, jclass cls)
{
    return EADDRNOTAVAIL;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_ECONNREFUSED (JNIEnv *env, jclass cls)
{
    return ECONNREFUSED;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_EINPROGRESS (JNIEnv *env, jclass cls)
{
    return EINPROGRESS;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_EHOSTUNREACH (JNIEnv *env, jclass cls)
{
    return EHOSTUNREACH;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_EMTHREAD (JNIEnv *env, jclass cls)
{
    return EMTHREAD;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_EFSM (JNIEnv *env, jclass cls)
{
    return EFSM;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_ENOCOMPATPROTO (JNIEnv *env, jclass cls)
{
    return ENOCOMPATPROTO;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_ETERM (JNIEnv *env, jclass cls)
{
    return ETERM;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_ENOTSOCK (JNIEnv *env, jclass cls)
{
    return ENOTSOCK;
}

JNIEXPORT jlong JNICALL
Java_org_zeromq_ZMQ_EAGAIN (JNIEnv *env, jclass cls)
{
    return EAGAIN;
}

JNIEXPORT void JNICALL
Java_org_zeromq_ZMQ_run_1proxy (JNIEnv *env, jclass cls, jobject frontend_, jobject backend_, jobject capture_)
{
#if ZMQ_VERSION >= ZMQ_MAKE_VERSION(3,2,2)
    void *frontend = get_socket (env, frontend_);
    void *backend = get_socket (env, backend_);
    void *capture = NULL;
    if (capture_ != NULL)
        capture = get_socket (env, capture_);
    zmq_proxy (frontend, backend, capture);
#endif
}
