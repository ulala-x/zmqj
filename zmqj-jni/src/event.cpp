#include <assert.h>
#include <zmq.h>
#include <cstdlib>
#include <cstring>

#include "zmqj.h"
#include "util.h"
#include "org_zeromq_ZEvent.h"

static jmethodID constructor;


JNIEXPORT void JNICALL Java_org_zeromq_ZEvent__1nativeInit(JNIEnv *env, jclass cls)
{
    constructor = env->GetMethodID(cls, "<init>", "(IILjava/lang/String;)V");
    assert(constructor);
}

static zmq_msg_t*
read_msg(JNIEnv *env, void *socket, zmq_msg_t *msg, int flags)
{
    int rc = zmq_msg_init (msg);
    if (rc != 0) {
        raise_exception (env, zmq_errno());
        return NULL;
    }

    rc = zmq_msg_recv(msg,socket,flags);

    int err = zmq_errno();
    if (rc < 0 && err == EAGAIN) {
        rc = zmq_msg_close (msg);
        err = zmq_errno();
        if (rc != 0) {
            raise_exception (env, err);
            return NULL;
        }
        return NULL;
    }
    if (rc < 0) {
        raise_exception (env, err);
        rc = zmq_msg_close (msg);
        err = zmq_errno();
        if (rc != 0) {
            raise_exception (env, err);
            return NULL;
        }
        return NULL;
    }
    return msg;
}

JNIEXPORT jobject JNICALL Java_org_zeromq_ZEvent__1recv(JNIEnv *env, jclass cls, jlong socket, jint flags)
{
    zmq_msg_t event_msg;

    // read event message
    if (!read_msg(env, (void *) socket, &event_msg, flags))
        return NULL;

    assert (zmq_msg_more(&event_msg) != 0);

    uint16_t event;
    int32_t value;
    // copy event data
    char *data = (char *) zmq_msg_data(&event_msg);
    memcpy(&event, data, sizeof(event));
    memcpy(&value, data + sizeof(event), sizeof(value));

    if (zmq_msg_close(&event_msg) < 0) {
        raise_exception(env, zmq_errno());
        return NULL;
    }

    char addr[1025];
    char *paddr;
    zmq_msg_t addr_msg;

    // read address message
    if (!read_msg(env, (void *) socket, &addr_msg, flags))
        return NULL;
    assert (zmq_msg_more(&addr_msg) == 0);

    // copy the address string
    const size_t len = zmq_msg_size(&addr_msg);

    paddr = (char *)(len >= sizeof(addr) ? malloc(len + 1) : &addr);
    memcpy(paddr, zmq_msg_data(&addr_msg), len);
    *(paddr + len) = '\0';

    if (zmq_msg_close(&addr_msg) < 0) {
        raise_exception(env, zmq_errno());
        return NULL;
    }

    jstring address = env->NewStringUTF(paddr);
    if (len >= sizeof(addr))
        free(paddr);
    assert(address);

    return env->NewObject(cls, constructor, event, value, address);

}
