package com.revo.aesrsa;

/**
 * author : HJY
 * date   : 2021/11/8
 * desc   :
 */
public class JniUtil {

    static {
        System.loadLibrary("JniUtil");
    }

    public static native String getSignature();

    public static native boolean activeSuccess(String code);

}
