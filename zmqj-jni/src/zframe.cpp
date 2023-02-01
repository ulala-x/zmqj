#include <zmq.h>
#include <assert.h>
#include <cstring>
#include "zmqj.h"
#include "util.h"
#include "org_zeromq_ZFrame.h"

static jfieldID  msgHandleFID;

static jmethodID limitMID;
static jmethodID positionMID;
static jmethodID setPositionMID;

inline void *get_message (JNIEnv *env, jobject obj)
{
    return (void*) env->GetLongField (obj, msgHandleFID);
}

inline void put_message (JNIEnv *env, jobject obj, void *msg)
{
    env->SetLongField (obj, msgHandleFID, (jlong) msg);
}

typedef struct _jzmq_zerocopy_t {
    JNIEnv *env;
    jobject ref_buffer;
} jzmq_zerocopy_t;

static void s_delete_ref (void *ptr, void *hint)
{
    jzmq_zerocopy_t *free_hint = (jzmq_zerocopy_t *)hint;
    free_hint->env->DeleteGlobalRef(free_hint->ref_buffer);
    delete free_hint;
}


static bool s_zerocopy_init (JNIEnv *env, zmq_msg_t *message, jobject obj,int offset, int length)
{
    jobject ref_buffer = env->NewGlobalRef(obj);
    jzmq_zerocopy_t *free_hint = new jzmq_zerocopy_t;

    free_hint->env = env;
    free_hint->ref_buffer = ref_buffer;

    jbyte* buf = (jbyte*) env->GetDirectBufferAddress(ref_buffer);

    int rc = zmq_msg_init_data (message, buf+offset, length, s_delete_ref, free_hint);
    if (rc != 0) {
        int err = zmq_errno();
        raise_exception (env, err);
        return false;
    }
    return true;
}


JNIEXPORT void JNICALL Java_org_zeromq_ZFrame__1nativeInit(JNIEnv *env, jclass c)
{
    msgHandleFID = env->GetFieldID(c, "msgHandle", "J");

    jclass bbcls = env->FindClass("java/nio/ByteBuffer");
    limitMID = env->GetMethodID(bbcls, "limit", "()I");
    positionMID = env->GetMethodID(bbcls, "position", "()I");
    setPositionMID = env->GetMethodID(bbcls, "position", "(I)Ljava/nio/Buffer;");
    env->DeleteLocalRef(bbcls);

}


JNIEXPORT void JNICALL Java_org_zeromq_ZFrame__1construct___3BII(JNIEnv * env,
                                                          jobject obj,
                                                          jbyteArray buffer,
                                                          jint offset,
                                                          jint length)
{

    zmq_msg_t* message = (zmq_msg_t*)get_message (env, obj);
    if (message)
        return;

    message = new zmq_msg_t();
   int rc = zmq_msg_init_size (message, length);
   int err = zmq_errno();
   if (rc != 0) {
       raise_exception (env, err);
   }

   void* pd = zmq_msg_data (message);
   env->GetByteArrayRegion(buffer, offset, length, (jbyte*) pd);

    put_message(env, obj, message);
}

JNIEXPORT void JNICALL Java_org_zeromq_ZFrame__1construct__Ljava_nio_ByteBuffer_2Z(JNIEnv* env,
                                                                                    jobject obj,
                                                                                    jobject buffer,
                                                                                    jboolean useZeroCopy)
{


    zmq_msg_t* message = (zmq_msg_t*)get_message (env, obj);
    if (message)
        return;

    // init the message
    message = new zmq_msg_t();

    int lim = env->CallIntMethod(buffer, limitMID);
    int pos = env->CallIntMethod(buffer, positionMID);
    int rem = pos <= lim ? lim - pos : 0;

    if(useZeroCopy == JNI_TRUE){
        bool retval = s_zerocopy_init (env, message, buffer, pos,rem);
        if (retval == false){
            return ;
        }
        put_message(env, obj, message);
    }else{
        jbyte* buf = (jbyte*) env->GetDirectBufferAddress(buffer);
        if(buf == NULL){
            return;
        }
        int rc = zmq_msg_init_size (message, rem);
        int err = zmq_errno();
        if (rc != 0) {
            raise_exception (env, err);
            return;
        }
        void* pd = zmq_msg_data (message);
//        env->GetByteArrayRegion(buf, pos, rem, (jbyte*) pd);
        memcpy(pd,buf+pos,rem);
        put_message(env, obj, message);
    }
}

/**
 * Called to destroy a Java Socket object.
 */
JNIEXPORT void JNICALL Java_org_zeromq_ZFrame__1destroy(JNIEnv * env, jobject obj)
{
    zmq_msg_t* message = (zmq_msg_t*)get_message (env, obj);
    if (message==NULL)
       return;

    int rc = zmq_msg_close (message);
    int err = zmq_errno();
    delete message;
    message = NULL;
    put_message (env, obj, message);

    if (rc != 0) {
        raise_exception (env, err);
        return;
    }
}

JNIEXPORT jbyteArray JNICALL Java_org_zeromq_ZFrame__1data(JNIEnv * env, jobject obj)
{
    zmq_msg_t* message = (zmq_msg_t*)get_message (env, obj);
    if (message==NULL){
         raise_exception(env,EFAULT);
         return NULL;
    }

    int sz = zmq_msg_size (message);
    void* pd = zmq_msg_data (message);

    jbyteArray data = env->NewByteArray (sz);
    if (! data) {
        raise_exception (env, EINVAL);
        return NULL;
    }

    env->SetByteArrayRegion (data, 0, sz, (jbyte*) pd);
    return data;

}
