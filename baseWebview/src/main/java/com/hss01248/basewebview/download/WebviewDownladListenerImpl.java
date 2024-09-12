package com.hss01248.basewebview.download;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hss.downloader.api.DownloadApi;
import com.hss.downloader.callback.DefaultSilentDownloadCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebviewDownladListenerImpl implements DownloadListener {

    static Pattern pattern = Pattern.compile("filename=['\"]?([^'\";]+)['\"]?");
    public static String parseFileName(String contentDisposition) {
        if (contentDisposition == null) {
            return null;
        }

        // 正则表达式匹配 filename="xxx" 或 filename='xxx' 或 filename=xxx（不带引号）的形式

        Matcher matcher = pattern.matcher(contentDisposition);

        if (matcher.find()) {
            return matcher.group(1);
            // 返回匹配到的文件名部分，去掉引号和前缀 "filename=" 等字符串。  注意这里假设了第一个捕获组就是我们要找的文件名内容。
            // 如果有多余的前后缀需要进一步处理可以自行修改正则表达式或者增加额外的逻辑判断来确保准确性。
            // 例如对于某些特殊情况可能还需要考虑编码问题等等细节因素影响最终结果展示效果和用户体验感受度提升空间还是很大的哦！不过目前这个简单版本已经能够满足大部分常见场景需求啦~
        }
        return null;
    }

    public static void download(String url,String fileName){
        new WebviewDownladListenerImpl().onDownloadStart(url,"","filename=\""+fileName+"\"","",0);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        LogUtils.i(url,userAgent,contentDisposition,mimetype,contentLength);
        String name = URLUtil.guessFileName(url,contentDisposition,mimetype);
        //contentDisposition:  attachment; filename="redditsave.com_p0dqho9nqh891.gif"
        String name2 = parseFileName(contentDisposition);
        if(!TextUtils.isEmpty(name2) && name2.contains(".")){
            name = name2;
        }
        String size = "";
        if(contentLength >0){
            size = ConvertUtils.byte2FitMemorySize(contentLength,1);
        }
        String str = "是否从\n"+url+"\n下载文件:\n"+name+"?\n文件大小预计:"+ size;
        String str2 = "是否下载文件:\n"+name+"?";
        if(!TextUtils.isEmpty(size)){
            str2 += "\n文件大小预计:"+size;
        }
        int[] position = new int[]{0};
        String finalName = name;
        AlertDialog dialog  = new AlertDialog.Builder(ActivityUtils.getTopActivity())
                .setTitle(str2)
                .setSingleChoiceItems(new String[]{"下载到普通文件夹", "下载到隐藏文件夹"}, position[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        position[0] = which;
                        doDownload(which ==1,url, finalName,contentLength);

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


    }

    private void doDownload(boolean hidden,String url, String name,long contentLength) {
        DownloadApi.create(url)
                .setName(name)
                .useServiceToDownload(contentLength > 5*1024*1024)
                .setCutToMediaStore(!hidden)
                .setSaveToHiddenDir(hidden)
                .setShowDefaultLoadingAndToast(true)
                .callback(new DefaultSilentDownloadCallback());

    }



}
