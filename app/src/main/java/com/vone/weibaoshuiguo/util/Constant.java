package com.vone.weibaoshuiguo.util;

/**
 * 常量
 */
public class Constant {
    // request参数
    public static final int REQ_QR_CODE = 11002; // // 打开扫描界面请求码
    public static final int REQ_PERM_CAMERA = 11003; // 打开摄像头
    public static final int REQ_PERM_EXTERNAL_STORAGE = 11004; // 读写文件
    public static final int REQ_PERM_SMS = 11005; // 读写短信
    public static final int REQ_READ_CONTACTS = 11006; // 读写联系人
    public static final int REQ_READ_CALL_LOG = 11007; // 读写通话记录

    public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";
}
