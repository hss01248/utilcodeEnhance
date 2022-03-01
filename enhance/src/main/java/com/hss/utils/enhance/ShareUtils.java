package com.hss.utils.enhance;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.hss01248.openuri.OpenUri;

import java.io.File;
import java.util.ArrayList;

/**
 * @author: Administrator
 * @date: 2022/3/1
 * @desc: //https://developer.android.google.cn/training/sharing/send?hl=zh-cn
 */
public class ShareUtils {

    /**
     *
     * 分享功能|分享单张图片
     *
     * @param context
     *            上下文
     * @param activityTitle
     *            Activity的名字
     * @param msgTitle
     *            消息标题
     * @param msgText
     *            消息内容
     * @param imgPath
     *            图片路径，不分享图片则传null
     *
     */
    public static void shareMsg(Context context, String activityTitle,
                                String msgTitle, String msgText, String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(imgPath));
                intent.setType(type);
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    /**
     * 分享多张照片
     *
     * @param context
     * @param list
     *            ArrayList＜ImageUri＞　
     */
    public static void sendMultiple(Context context,
                                    ArrayList<? extends Parcelable> list) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_TITLE, "");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "share"));
    }


    public static void shareFile( File file) {
        Uri uri = OpenUri.fromFile(Utils.getApp(), file);
        shareFile(uri);

    }

    /**
     * <ul>
     * <li>分享任意类型的<b style="color:red">单个</b>文件|不包含目录</li>
     * <li>[<b>经验证！可以发送任意类型的文件！！！</b>]</li>
     * <li># @author http://blog.csdn.net/yuxiaohui78/article/details/8232402</li>
     * <ul>
     * @param uri
     *            Uri.from(file);
     *
     */
    public static void shareFile(Uri uri) {
        // File file = new File("\sdcard\android123.cwj"); //附件文件地址

        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri); // 添加附件，附件为file对象
        if(TextUtils.isEmpty(type)){
            intent.setType("application/octet-stream"); // 其他的均使用流当做二进制数据来发送
        }else {
            intent.setType(type); // 其他的均使用流当做二进制数据来发送
        }
       // intent.setType("*/*");   //分享文件
        //ActivityUtils.getTopActivity().startActivity(Intent.createChooser(intent, "分享"));
        ActivityUtils.getTopActivity().startActivity(intent); // 调用系统的mail客户端进行发送
    }
    /**
     * <ul>
     * <li>分享任意类型的<b style="color:red">多个</b>文件|不包含目录</li>
     * <li>[<b>经验证！可以发送任意类型的文件！！！</b>]</li>
     * <li># @author http://blog.csdn.net/yuxiaohui78/article/details/8232402</li>
     * <ul>
     *
     * @param context
     * @param uris
     *            list.add(Uri.from(file));
     *
     */
    public static void shareMultipleFiles(Context context, ArrayList<Uri> uris) {

        boolean multiple = uris.size() > 1;
        Intent intent = new Intent(
                multiple ? android.content.Intent.ACTION_SEND_MULTIPLE
                        : android.content.Intent.ACTION_SEND);

        if (multiple) {
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        } else {
            Uri value = uris.get(0);
            String ext = MimeTypeMap.getFileExtensionFromUrl(value.toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if(mimeType==null){
                mimeType = "*/*";
            }
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, value);
        }
        context.startActivity(Intent.createChooser(intent, "Share"));
    }

}
