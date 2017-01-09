#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <jni.h>
#include <android/log.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "柳梦", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "柳梦", __VA_ARGS__))



/**********传输整数**************/
JNIEXPORT void JNICALL Java_com_handu_poweroperational_main_activity_NdkTestActivity_callJNIInt(
        JNIEnv *env, jobject obj,
        jint i) {
    //找到java中的类
    jclass cls = (*env)->FindClass(env, "com/handu/poweroperational/main/activity/NdkTestActivity");
    //再找类中的方法
    jmethodID mid = (*env)->GetMethodID(env, cls, "callbackInt", "(I)V");
    if (mid == NULL) {
        LOGI("int error");
        return;
    }
    //打印接收到的数据
    LOGI("from java int: %d", i);
    //回调java中的方法
    (*env)->CallVoidMethod(env, obj, mid, i);

}

/********传输字符串************* */
//java方法的的参数签名，(I)V表示void （int）； ([B)V表示void (byte [])；
JNIEXPORT void JNICALL Java_com_handu_poweroperational_main_activity_NdkTestActivity_callJNIString(
        JNIEnv *env,
        jobject obj,
        jstring s) {
    //找到java中的类
    jclass cls = (*env)->FindClass(env, "com/handu/poweroperational/main/activity/NdkTestActivity");
    //再找类中的方法
    jmethodID mid = (*env)->GetMethodID(env, cls, "callbackString", "(Ljava/lang/String;)V");
    if (mid == NULL) {
        LOGI("string error");
        return;
    }
    const char *ch;
    //获取由java传过来的字符串
    ch = (*env)->GetStringUTFChars(env, s, NULL);
    //打印
    LOGI("from java string: %s", ch);
    (*env)->ReleaseStringUTFChars(env, s, ch);
    //回调java中的方法
    (*env)->CallVoidMethod(env, obj, mid, (*env)->NewStringUTF(env, ch));

}

/********传输数组(byte[])************* */
JNIEXPORT void JNICALL Java_com_handu_poweroperational_main_activity_NdkTestActivity_callJNIByte(
        JNIEnv *env,
        jobject obj,
        jbyteArray b) {
    //找到java中的类
    jclass cls = (*env)->FindClass(env, "com/handu/poweroperational/main/activity/NdkTestActivity");
    //再找类中的方法
    jmethodID mid = (*env)->GetMethodID(env, cls, "callbackByte", "([B)V");
    if (mid == NULL) {
        LOGI("byte[] error");
        return;
    }

    //获取数组长度
    jsize length = (*env)->GetArrayLength(env, b);
    LOGI("length: %d", length);
    //获取接收到的数据
    int i;
    jbyte *p = (*env)->GetByteArrayElements(env, b, NULL);
    //打印
    for (i = 0; i < length; i++) {
        LOGI("%d", p[i]);
    }

    char c[5];
    c[0] = 1;
    c[1] = 2;
    c[2] = 3;
    c[3] = 4;
    c[4] = 5;
    //构造数组
    jbyteArray carr = (*env)->NewByteArray(env, length);
    (*env)->SetByteArrayRegion(env, carr, 0, length, c);
    //回调java中的方法
    (*env)->CallVoidMethod(env, obj, mid, carr);
}