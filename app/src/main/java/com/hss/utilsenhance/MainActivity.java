package com.hss.utilsenhance;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.security.identity.IdentityCredential;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.util.Pair;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.hss.downloader.MyDownloader;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss.utils.enhance.BarColorUtil;
import com.hss.utils.enhance.HomeMaintaner;
import com.hss.utils.enhance.UrlEncodeUtil;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss.utils.enhance.foregroundservice.CommonProgressService;
import com.hss.utils.enhance.intent.ShareUtils;
import com.hss.utils.enhance.intent.SysIntentUtil;
import com.hss.utils.enhance.viewholder.ContainerActivity2;
import com.hss.utils.enhance.viewholder.mvvm.ContainerViewHolderWithTitleBar;
import com.hss.utilsenhance.databinding.DisplayMetaViewBinding;
import com.hss.utilsenhance.databinding.TestFullBinding;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.activityresult.TheActivityListener;
import com.hss01248.basewebview.BaseWebviewActivity;
import com.hss01248.biometric.BiometricHelper;
import com.hss01248.bitmap_saver.BitmapSaveUtil;
import com.hss01248.cipher.AesCipherUtil;
import com.hss01248.cipher.PasswordLoginByBiometric;
import com.hss01248.cipher.RsaCipherUtil;
import com.hss01248.cipher.SignUtil;
import com.hss01248.cipher.SslUtil;
import com.hss01248.cipher.file.EncryptedUtil;
import com.hss01248.cipher.sp.EnSpUtil;
import com.hss01248.fullscreendialog.FullScreenDialogUtil;
import com.hss01248.image.dataforphotoselet.ImgDataSeletor;
import com.hss01248.iwidget.BaseDialogListener;
import com.hss01248.iwidget.msg.AlertDialogImplByDialogUtil;
import com.hss01248.iwidget.msg.AlertDialogImplByMmDialog;
import com.hss01248.iwidget.msg.AlertDialogImplByXStyleDialog;
import com.hss01248.iwidget.pop.PopList;
import com.hss01248.iwidget.singlechoose.SingleChooseDialogImpl;
import com.hss01248.iwidget.singlechoose.SingleChooseDialogListener;
import com.hss01248.media.contact.ContactInfo;
import com.hss01248.media.contact.ContactPickUtil;
import com.hss01248.media.metadata.MetaDataUtil;
import com.hss01248.media.pick.CaptureAudioUtil;
import com.hss01248.media.pick.CaptureImageUtil;
import com.hss01248.media.pick.CaptureVideoUtil;
import com.hss01248.media.pick.MediaPickOrCaptureUtil;
import com.hss01248.media.pick.MediaPickUtil;
import com.hss01248.media.pick.SafUtil;
import com.hss01248.media.uri.ContentUriUtil;
import com.hss01248.openuri2.OpenUri2;
import com.hss01248.permission.MyPermissions;
import com.hss01248.qrscan.ScanCodeActivity;
import com.hss01248.sentry.SentryUtil;
import com.hss01248.toast.MyToast;
import com.hss01248.viewholder_media.FileTreeViewHolder;
import com.hss01248.webviewspider.SpiderWebviewActivity;

import org.devio.takephoto.wrap.TakeOnePhotoListener;
import org.devio.takephoto.wrap.TakePhotoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    public void urlEncode(View view) {
        String str = "1 2  34+7+8+ 9";
        String encode = UrlEncodeUtil.encode(str);
        UrlEncodeUtil.decode(encode);



      /*  String str2 = "12 34+789";
        String encode1 = UrlEncodeUtil.encode(str2);
        UrlEncodeUtil.decode(encode1);*/
    }

    public void getDecodedPath(View view) {
        //秒 +开+45 a p+i
        String url = "https://www.baidu.com/1234%2B789/%E7%A7%92%E5%BC%80%2B45%20api";
        UrlEncodeUtil.decode(url);
        UrlEncodeUtil.getDecodedPath(url);

        String url2 = "https://www.baidu.com/1234%2B789/%E7%A7%92%20%2B%E5%BC%80%2B45%20a%20p%2Bi";
        UrlEncodeUtil.decode(url2);
        UrlEncodeUtil.getDecodedPath(url2);

    }

    public void encodeFilePathToUrlUsage(View view) {
        String path = "F:/img cache/秒  +开+45 a p+i/45.jpg";
        String s = UrlEncodeUtil.encodeFilePathToUrlUsage(path);
        //看能否还原
        String url2 = "https://www.baidu.com"+s;
        UrlEncodeUtil.getDecodedPath(url2);

    }

    public void share(View view) {
        TakePhotoUtil.startPickOneWitchDialog(this, new TakeOnePhotoListener() {
            @Override
            public void onSuccess(String path) {
                ShareUtils.shareFile(new File(path));
            }

            @Override
            public void onFail(String path, String msg) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 状态栏: 透明+沉浸
     * @param view
     */
    public void statusbarTrans(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ThreadUtils.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BarColorUtil.autoFitStatusBarLightModeNow(getWindow());
            }
        },1000);

    }

    /**
     * 导航栏和状态栏均透明+沉浸
     * @param view
     */
    public void navibarTrans(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void hideStatusbar(View view) {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        BarColorUtil.autoFitStatusBarLightModeNow(getWindow());
    }

    public void hideNaviBar(View view) {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
    }

    /**
     * https://blog.csdn.net/guolin_blog/article/details/51763825
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
       /* if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow().setNavigationBarColor(Color.TRANSPARENT);
            }

        }*/
    }


    public void clickHome(View view) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        //clickHome(null);
        HomeMaintaner.onBackPressed(this,true,null);
    }

    public void local2(View view) {

        MediaPickUtil.pickVideo(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, Throwable throwable) {

            }
        });
    }

    public void pickImage(View view) {
        MediaPickUtil.pickImage(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, Throwable throwable) {

            }
        });
    }

    public void pickVideo(View view) {
        MediaPickUtil.pickVideo(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, Throwable throwable) {

            }
        });
    }

    public void takeVideo(View view) {
        CaptureVideoUtil.startVideoCapture(true,30, 1024 * 1024 * 1024, new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String path) {
                showMata(OpenUri2.fromFile(Utils.getApp(),new File(path)));
                LogUtils.d(path);
            }

            @Override
            public void onError(String code, String msg,Throwable e) {
                LogUtils.d(msg);
            }
        });
    }

    private void showMata(Uri path0) {
        final String[] path = {path0.toString()};
        LogUtils.d(path[0]);
        String desc = path[0] +"\n";

        Map map0 = new LinkedHashMap();
        Map map = ContentUriUtil.getInfos(path0);
        map0.put("uriInfo",map);

        if(!path[0].startsWith("/")){
            desc = URLDecoder.decode(path[0]);
        }else {
            try {
                //todo MetaDataUtil兼容fileprovider的uri,此时至少有读的权限
                Map map1 = MetaDataUtil.getMetaData(path[0]);
                map0.put("meta",map1);
            }catch (Throwable throwable){
                throwable.printStackTrace();
                map0.put("meta",throwable.getMessage());
                MyPermissions.request(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {

                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {

                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        desc  = path0.toString()+"\n"+  new GsonBuilder().setPrettyPrinting().create().toJson(map);
        String type = map.get("mime_type")+"";

        View contentView = null;
        if(type.contains("image") || type.contains("video")
                || path0.toString().endsWith(".mp4")
                || path0.toString().endsWith(".jpg")){
            DisplayMetaViewBinding binding = DisplayMetaViewBinding.inflate(MainActivity.this.getLayoutInflater(),findViewById(android.R.id.content),false);
            //ViewGroup group = (ViewGroup) View.inflate(this,R.layout.display_meta_view,findViewById(android.R.id.content));
            contentView = binding.getRoot();

           ImageView imageView =  binding.ivImage;
            Glide.with(this)
                    .load(path0)
                    //.override(SizeUtils.dp2px(100),SizeUtils.dp2px(100))
                    .into(imageView);

            TextView textView =  binding.tvDesc;
            textView.setText(desc);

        }

        AlertDialog.Builder builder =   new AlertDialog.Builder(this)
                .setTitle("mata data");
        if(contentView !=null){
            builder.setView(contentView);
        }else {
              builder.setMessage(desc);
        }
        builder.setPositiveButton("预览", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(map.containsKey("_data")){
                            path[0] = map.get("_data")+"";
                        }
                        SysIntentUtil.openFile(path[0]);
                    }
                }).setNegativeButton("cancel",null)
                .create().show();
    }

    public void takePicture(View view) {
        CaptureImageUtil.takePicture(true,new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                showMata(OpenUri2.fromFile(Utils.getApp(),new File(s)));
                LogUtils.d(s);
            }

            @Override
            public void onError(String code, String msg, @Nullable Throwable throwable) {
                LogUtils.w(code+"-"+msg);
            }
        });
    }

    public void pickAudio(View view) {
        MediaPickUtil.pickAudio(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri s) {
                showMata(s);
            }

            @Override
            public void onError(String code, String msg, @Nullable Throwable throwable) {

            }
        });
    }

    public void toastSuccess(View view) {
        MyToast.success("success---->");
    }

    public void toastError(View view) {
        MyToast.error("toastError---->");
    }

    public void toastNormal(View view) {
        MyToast.show("toastNormal---->");
    }

    public void toastDebug(View view) {
        MyToast.debug("toastDebug---->");
    }

    Dialog dialog;
    public void showLoading(View view) {
         dialog = MyToast.showLoadingDialog("");
        //dialog.setCancelable(true);
    }

    public void dismissLoading(View view) {
        MyToast.dismissLoadingDialog(dialog);
    }

    public void showLoadingInBack(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dialog = MyToast.showLoadingDialog("showLoadingInBackshowLoadingInBackshowLoading" +
                        "InBackshowLoadingInBackshowLoadingInBackshowLoadingInBackshowLoadingInBackshowLoadingInBackshowLoadingInBack");
            }
        }).start();
    }

    public void pickPdf(View view) {

       /* FilePickUtil.pickPdf(this, new MyCommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                showMata(OpenUri.fromFile(getApplicationContext(),new File(s)));
            }
        });*/
        MediaPickUtil.pickPdf(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                showMata(uri);
            }

            @Override
            public void onError(String msg) {
                MyCommonCallback.super.onError(msg);
                MyToast.error(msg);
            }
        });
    }

    public void externalPrint(View view) {
        Uri uriQuery =  MediaStore.Files.getContentUri("external");
        print(uriQuery);
        //除了图片,视频,音频,还是100个其他类型的文件
        //group by : https://blog.csdn.net/weixin_30755709/article/details/94945476
        //"0=0) group by (mime_type"  已经不行了  Invalid token group
    }

    public void externalImage(View view) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }

            //打开文件:
            try (ParcelFileDescriptor pfd =
                    resolver.openFileDescriptor(content-uri, readOnlyMode)) {
                // Perform operations on "pfd".
            } catch (IOException e) {
                e.printStackTrace();
            }
            //其他存储卷:
            Set<String> volumeNames = MediaStore.getExternalVolumeNames(context);
            String firstVolumeName = volumeNames.iterator().next();
*/
        Uri uriQuery =  MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        print(uriQuery);

    }
    void print(Uri uriQuery){
        ContentResolver cr = Utils.getApp().getContentResolver();
        Cursor query = cr.query(uriQuery, null, null, null, MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC");
        ContentUriUtil.doQuery(query,10);
    }

    public void externalAudio(View view) {
        print(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
    }

    public void externalVideo(View view) {
        print(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
    }

    public void groupby(View view) {
        Uri uriQuery =  MediaStore.Files.getContentUri("external");
        Cursor query = Utils.getApp().getContentResolver().query(uriQuery, null,
                null, null, MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC");
        ContentUriUtil.groupBy(query,"mime_type");
    }

    public void groupbyNone(View view) {
       /* String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;

        Uri uriQuery =  MediaStore.Files.getContentUri("external");
        Cursor query = Utils.getApp().getContentResolver().query(uriQuery, null,
                selection, null, MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC");
        MediaPickUtil.groupBy(query,"mime_type");*/

        String[] columns = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.DATA};
        String select = "(_data LIKE '%.pdf')";

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), columns, select, null, null);

        LogUtils.d( " pdf count " + cursor.getCount());
        int columnIndexOrThrow_DATA = 0;
        if (cursor != null) {
            columnIndexOrThrow_DATA = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(columnIndexOrThrow_DATA);

                //PDFFileInfo document = PDFUtil.getFileInfoFromFile(new File(path));

               // pdfData.add(document);
                LogUtils.d( " pdf " + path);
            }
        }
        cursor.close();


        //原文链接：https://blog.csdn.net/u012556114/article/details/101217053
    }

    public void pickPdf2(View view) {

    }

    public void saf(View view) {
        SafUtil.askAndroidDataDir();
    }

    public void pageStateXml(View view) {
        startActivity(new Intent(this, StateActivityXml.class));
    }

    public void pageStateCode(View view) {
        startActivity(new Intent(this,StateActivityHasParent.class));
    }

    public void smallView(View view) {
        startActivity(new Intent(this,StateActivitySmallView.class));
    }

    public void takeOrSelect(View view) {
        MediaPickOrCaptureUtil.pickImageOrTakePhoto(false,new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                showMata(uri);
            }
        });
    }

    public void pickMultiFiles(View view) {
        MediaPickUtil.pickMultiFiles(new MyCommonCallback<List<Uri>>() {
            @Override
            public void onSuccess(List<Uri> uris) {
                LogUtils.i(uris);
            }
        });
    }

    public void pickContact(View view) {
        ContactPickUtil.pickOneContact( new MyCommonCallback<ContactInfo>() {
            @Override
            public void onSuccess(ContactInfo contactInfos) {
                LogUtils.i(contactInfos);
            }
        });
    }

    public void captureAudio(View view) {
        CaptureAudioUtil.startRecord(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                LogUtils.i(uri);
            }
        });
    }

    public void takeOrVideo(View view) {
        new SingleChooseDialogImpl().showInPopMenu(view,3,
                //StringUtils.getString(com.hss01248.media.R.string.meida_pick_please_choose),
                new CharSequence[]{
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_choose_video_from_album),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_take_photo),
                       // "这里提供了国内下载节点， 如果您无法通过以上连接下载release包，可以尝试从下方连接下载(但您需要支付流量费用)",
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                },
                new SingleChooseDialogListener(){

                    @Override
                    public void onItemClicked(int position, CharSequence text) {
                        LogUtils.d(position+","+text);
                    }

                    @Override
                    public void onCancel(boolean fromBackPressed, boolean fromOutsideClick, boolean fromCancelButton) {
                        SingleChooseDialogListener.super.onCancel(fromBackPressed, fromOutsideClick, fromCancelButton);
                        //callback.onError(StringUtils.getString(com.hss01248.media.R.string.meida_pick_canceled));
                    }
                });
    }

    public void alertByDialogUtil(View view) {
        new AlertDialogImplByDialogUtil().showMsg("普通标题",
                "想要监控对话框的生命周期，可以实现其 .setDialogLifecycleCallback(...) 接口，建议使用build()方法构建对话框",
                "确认", "",
                new BaseDialogListener() {
                    @Override
                    public void onConfirm() {
                        BaseDialogListener.super.onConfirm();
                    }
                });

        new AlertDialogImplByDialogUtil().showMsg("超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题",
                "说明\n" +
                        "创建CI流水线，使用KubeSphere DevOps完成源码编译、镜像构建并推送到Harbor仓库或其他云仓库，最后以git commit方式更新yaml仓库中image字段。\n" +
                        "\n" +
                        "CD部分我们使用Argo CD来做，Argo CD持续监测yaml仓库配置文件变动，当 CI 部分执行git push时便会触发 Argo CD 更新 yaml 文件到 k8s 集群。\n" +
                        "\n" +
                        "准备工作\n" +
                        "安装 kubesphere，启用 KubeSphere DevOps系统。\n" +
                        "\n" +
                        "需要有一个 Docker Hub帐户，也可以自建Harbor，我这里使用的是免费阿里云仓库。\n" +
                        "\n" +
                        "需要创建一个企业空间、一个 DevOps 工程和一个帐户 (project-regular)，必须邀请该帐户至DevOps工程中并赋予operator角色。如果尚未创建，请参见 创建企业空间、项目、帐户和角色。\n" +
                        "\n" +
                        "设置 CI 专用节点来运行流水线（可选）。有关更多信息，请参见 为缓存依赖项设置 CI 节点\n" +
                        "\n" +
                        "配置邮件告警（可选）。请参见 为 KubeSphere 流水线设置电子邮件服务器，我这里计划对接微信告警。\n" +
                        "\n" +
                        "配置 SonarQube 将代码分析纳入流水线中（可选）。有关更多信息，请参见 将 SonarQube 集成到流水线。\n" +
                        "-----------------------------------\n" +
                        "©著作权归作者所有：来自51CTO博客作者品鉴初心的原创作品，请联系作者获取转载授权，否则将追究法律责任\n" +
                        "使用 KubeSphere 创建DevOps工程\n" +
                        "https://blog.51cto.com/wutengfei/3264097",
                "确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认",
                "取消取消取消取消取消取消取消取消取消取消取消取消取消取消取消",
                new BaseDialogListener() {
                    @Override
                    public void onConfirm() {
                        BaseDialogListener.super.onConfirm();
                    }
                });
    }

    public void alertByXStyleDialog(View view) {
        new AlertDialogImplByXStyleDialog().showMsg("普通标题",
                "想要监控对话框的生命周期，可以实现其 .setDialogLifecycleCallback(...) 接口，建议使用build()方法构建对话框",
                "确认", "取消",
                new BaseDialogListener() {
                    @Override
                    public void onConfirm() {
                        BaseDialogListener.super.onConfirm();
                    }
                });

        new AlertDialogImplByXStyleDialog().showMsg("超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题",
                "说明\n" +
                        "创建CI流水线，使用KubeSphere DevOps完成源码编译、镜像构建并推送到Harbor仓库或其他云仓库，最后以git commit方式更新yaml仓库中image字段。\n" +
                        "\n" +
                        "CD部分我们使用Argo CD来做，Argo CD持续监测yaml仓库配置文件变动，当 CI 部分执行git push时便会触发 Argo CD 更新 yaml 文件到 k8s 集群。\n" +
                        "\n" +
                        "准备工作\n" +
                        "安装 kubesphere，启用 KubeSphere DevOps系统。\n" +
                        "\n" +
                        "需要有一个 Docker Hub帐户，也可以自建Harbor，我这里使用的是免费阿里云仓库。\n" +
                        "\n" +
                        "需要创建一个企业空间、一个 DevOps 工程和一个帐户 (project-regular)，必须邀请该帐户至DevOps工程中并赋予operator角色。如果尚未创建，请参见 创建企业空间、项目、帐户和角色。\n" +
                        "\n" +
                        "设置 CI 专用节点来运行流水线（可选）。有关更多信息，请参见 为缓存依赖项设置 CI 节点\n" +
                        "\n" +
                        "配置邮件告警（可选）。请参见 为 KubeSphere 流水线设置电子邮件服务器，我这里计划对接微信告警。\n" +
                        "\n" +
                        "配置 SonarQube 将代码分析纳入流水线中（可选）。有关更多信息，请参见 将 SonarQube 集成到流水线。\n" +
                        "-----------------------------------\n" +
                        "©著作权归作者所有：来自51CTO博客作者品鉴初心的原创作品，请联系作者获取转载授权，否则将追究法律责任\n" +
                        "使用 KubeSphere 创建DevOps工程\n" +
                        "https://blog.51cto.com/wutengfei/3264097",
                "确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认",
                "取消取消取消取消取消取消取消取消取消取消取消取消取消取消取消",
                new BaseDialogListener() {
                    @Override
                    public void onConfirm() {
                        BaseDialogListener.super.onConfirm();
                    }
                });
    }

    public void alertByMmDialog(View view) {
        new AlertDialogImplByMmDialog().showMsg("普通标题",
                "想要监控对话框的生命周期，可以实现其 .setDialogLifecycleCallback(...) 接口，建议使用build()方法构建对话框",
                "确认", "取消",
                new BaseDialogListener() {
                    @Override
                    public void onConfirm() {
                        BaseDialogListener.super.onConfirm();
                    }
                });

        new AlertDialogImplByMmDialog().showMsg("超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题超长标题",
                "说明\n" +
                        "创建CI流水线，使用KubeSphere DevOps完成源码编译、镜像构建并推送到Harbor仓库或其他云仓库，最后以git commit方式更新yaml仓库中image字段。\n" +
                        "\n" +
                        "CD部分我们使用Argo CD来做，Argo CD持续监测yaml仓库配置文件变动，当 CI 部分执行git push时便会触发 Argo CD 更新 yaml 文件到 k8s 集群。\n" +
                        "\n" +
                        "准备工作\n" +
                        "安装 kubesphere，启用 KubeSphere DevOps系统。\n" +
                        "\n" +
                        "需要有一个 Docker Hub帐户，也可以自建Harbor，我这里使用的是免费阿里云仓库。\n" +
                        "\n" +
                        "需要创建一个企业空间、一个 DevOps 工程和一个帐户 (project-regular)，必须邀请该帐户至DevOps工程中并赋予operator角色。如果尚未创建，请参见 创建企业空间、项目、帐户和角色。\n" +
                        "\n" +
                        "设置 CI 专用节点来运行流水线（可选）。有关更多信息，请参见 为缓存依赖项设置 CI 节点\n" +
                        "\n" +
                        "配置邮件告警（可选）。请参见 为 KubeSphere 流水线设置电子邮件服务器，我这里计划对接微信告警。\n" +
                        "\n" +
                        "配置 SonarQube 将代码分析纳入流水线中（可选）。有关更多信息，请参见 将 SonarQube 集成到流水线。\n" +
                        "-----------------------------------\n" +
                        "©著作权归作者所有：来自51CTO博客作者品鉴初心的原创作品，请联系作者获取转载授权，否则将追究法律责任\n" +
                        "使用 KubeSphere 创建DevOps工程\n" +
                        "https://blog.51cto.com/wutengfei/3264097",
                "确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认确认",
                "取消取消取消取消取消取消取消取消取消取消取消取消取消取消取消",
                new BaseDialogListener() {
                    @Override
                    public void onConfirm() {
                        BaseDialogListener.super.onConfirm();
                    }
                });
    }

    public void pop(View view) {
    }

    public void qrScan(View view) {
        ScanCodeActivity.scanForResult(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                ToastUtils.showLong(s);
                BaseWebviewActivity.start(ActivityUtils.getTopActivity(),s);
            }
        });

    }

    public void centerList(View view) {
        new SingleChooseDialogImpl().showInCenter("我是标题",
                //StringUtils.getString(com.hss01248.media.R.string.meida_pick_please_choose),
                new CharSequence[]{
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_choose_video_from_album),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_take_photo),
                         "这里提供了国内下载节点， 如果您无法通过以上连接下载release包，可以尝试从下方连接下载(但您需要支付流量费用)",
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_record_video),
                        StringUtils.getString(com.hss01248.media.R.string.meida_pick_from_galerry),
                },
                new SingleChooseDialogListener(){

                    @Override
                    public void onItemClicked(int position, CharSequence text) {
                        LogUtils.d(position+","+text);
                    }

                    @Override
                    public void onCancel(boolean fromBackPressed, boolean fromOutsideClick, boolean fromCancelButton) {
                        SingleChooseDialogListener.super.onCancel(fromBackPressed, fromOutsideClick, fromCancelButton);
                        //callback.onError(StringUtils.getString(com.hss01248.media.R.string.meida_pick_canceled));
                    }
                });
    }

    public void pickImage13(View view) {
        doPick13();
    }

    private void doPick13() {
        final int maxNumPhotosAndVideos = 3;

        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        intent.setType("image/*");
        intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxNumPhotosAndVideos);
        StartActivityUtil.startActivity(this,
                null,
                intent,
                true,
                new TheActivityListener<Activity>(){
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                        super.onActivityResult(requestCode, resultCode, data);
                        //Intent { flg=0x41 ClipData.Item { U:content://media/picker/0/com.android.providers.media.photopicker/media/1000020960} }
                        //ClipData { image/* video/* 2 items: {U(content)} {U(content)} }
                        try {
                            String path = "";
                            if(data !=null && data.getData() != null){
                                path = ContentUriUtil.getRealPath(data.getData());
                            }
                            LogUtils.w(resultCode,data,path,data.getExtras(),data.getData(),data.getClipData());
                            ClipData clipData = data.getClipData();
                            int itemCount = clipData.getItemCount();
                            for (int i = 0; i < itemCount; i++) {
                                ClipData.Item itemAt = clipData.getItemAt(i);
                                Uri uri = itemAt.getUri();
                                LogUtils.i(uri,ContentUriUtil.getRealPath(uri));
                                //{_display_name=1000020960.jpg,
                                // _data=/sdcard/.transforms/synthetic/picker/0/com.android.providers.media.photopicker/media/1000020960.jpg,
                                // mime_type=image/jpeg, datetaken=996139080, _size=1171426, duration=0}
                            }
                        }catch (Throwable throwable){
                            throwable.printStackTrace();
                        }
                    }
                });
    }

    public void foregroundService(View view) {
        CommonProgressService.startS("图片上传", "上传进度:", 0,new Runnable() {
            @Override
            public void run() {
                CommonProgressService.doHttpTask();
            }
        });
    }

    public void notify(View view) {
        CommonProgressService.updateProgress(50,150,"图片上传",
                "上传进度:50/150,剩余100,正在上传:instance of leakcanary.internal.ViewModelClearedWatcher.jpg",0);

        ThreadUtils.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonProgressService.updateProgress(150,150,"图片上传",
                        "上传进度:50/150,剩余100,正在上传:instance of leakcanary.internal.ViewModelClearedWatcher.jpg",0);
            }
        },3000);

    }

    public void taskOnly(View view) {
        CommonProgressService.doHttpTask();
    }

    public void biometric(View view) {
        Cipher cipher = null;
        try {
            cipher = AesCipherUtil.getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, AesCipherUtil.getOrCreateSecretKey("pw",true));
        } catch (Throwable e) {
            LogUtils.w(e);
        }

        BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(cipher);
        BiometricHelper.showBiometricDialog(this, cryptoObject,true,new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                ToastUtils.showLong("onAuthenticationError,"+errorCode+","+errString);
            }


            /**  https://zhuanlan.zhihu.com/p/489913461
             * 加密:
             * 首先通过 KeyStore，主要是得到一个包含密码的 SecretKey ，当然这里有一个关键操作，那就是 setUserAuthenticationRequired(true)，后面我们再解释；
             * 然后利用 SecretKey 创建 Clipher ， Clipher 就是 Java 里常用于加解密的对象；
             * 利用 BiometricPrompt.CryptoObject(cipher) 去调用生物认证授权；
             * 授权成功后会得到一个 AuthenticationResult ，Result 里面包含存在密钥信息的 cryptoObject?.cipher 和 cipher.iv 加密偏移向量；
             * 利用授权成功后的 cryptoObject?.cipher 对 Token 进行加密，然后和 cipher.iv 一起保存到 SharePerferences ，就完成了基于 BiometricPrompt 的加密保存；
             *
             * 解密:
             * 在 SharePerferences 里获取加密后的 Token 和 iv 信息；
             * 同样是利用 SecretKey 创建 Clipher ，不过这次要带上保存的 iv 信息；
             * 利用 BiometricPrompt.CryptoObject(cipher) 去调用生物认证授权；
             * 通过授权成功后的 cryptoObject?.cipher 对 Token 进行加密，得到原始的 Token 信息；
             */
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                ToastUtils.showLong("验证成功:"+result.getAuthenticationType());
                //result.getCryptoObject().getSignature();
                //https://zhuanlan.zhihu.com/p/489913461   移动端系统生物认证技术详解
                if(result.getCryptoObject() ==null){
                    //DEVICE_CREDENTIAL = 1
                    //TYPE_BIOMETRIC = 2
                    LogUtils.w("result.getCryptoObject() ==null, type is "+result.getAuthenticationType());
                    return;
                }
                IdentityCredential identityCredential = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                     identityCredential = result.getCryptoObject().getIdentityCredential();
                }

                LogUtils.d(result.getCryptoObject().getCipher(),
                            result.getCryptoObject().getSignature(),
                            result.getCryptoObject().getMac(),
                            identityCredential
                            );
                try{
                    String str = "login_pw_123456";
                    Cipher cipher = result.getCryptoObject().getCipher();
                    //String encrypted =
                   // if(SpUtil.getString("en_key"),"")

                    byte[] encrypted = cipher.doFinal(str.getBytes());
                    byte[] iv = cipher.getIV();
                    String se = Base64.encodeToString(encrypted, Base64.URL_SAFE);
                    String siv = Base64.encodeToString(iv, Base64.URL_SAFE);
                    //SpUtil.putString("en_key");
                    LogUtils.i("加密后:"+se,"iv:"+siv);

                    decrypt2(encrypted,iv);



                }catch (Throwable throwable){
                    LogUtils.w(throwable);
                }


            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                ToastUtils.showLong("onAuthenticationFailed");
            }
        });
    }

    private void decrypt2(byte[] encrypted, byte[] iv) {
        Cipher cipher = null;
        try {
            cipher = AesCipherUtil.getCipher();
           int TAG_SIZE_IN_BYTES = 16;
            //IV required when decrypting
            //cipher.init(Cipher.DECRYPT_MODE, CryptUtil.getOrCreateSecretKey("pw",true));
            cipher.init(Cipher.DECRYPT_MODE, AesCipherUtil.getOrCreateSecretKey("pw",true),
                    new GCMParameterSpec(TAG_SIZE_IN_BYTES * 8, iv));
        } catch (Throwable e) {
            LogUtils.w(e);
        }
        BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(cipher);
        BiometricHelper.showBiometricDialog(this, cryptoObject,true, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Cipher cipher2 = result.getCryptoObject().getCipher();
                try {
                    LogUtils.i("解密后:"+ AesCipherUtil.decryptData(encrypted,cipher2));
                } catch (Throwable e) {
                   LogUtils.w(e);
                }

            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                ToastUtils.showLong("onAuthenticationError,"+errorCode+","+errString);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                ToastUtils.showLong("onAuthenticationFailed");
            }
        });


    }

    public void enSp(View view) {
        EnSpUtil.putString("teststr","The design is simple, the function is rich, the interaction is " +
                "smooth, and the background is pure. Every page layout design is well thought out so that you ...");
        String str = EnSpUtil.getString("teststr","defval");
        LogUtils.i("enstr",str);
    }

    public void enFile(View view) {
        MyPermissions.requestByMostEffort(false, true, new PermissionUtils.FullCallback() {
            @Override
            public void onGranted(@NonNull List<String> granted) {
                /*ExplorerConfig config = new ExplorerConfig(MainActivity.this);
                config.setOnFilePickedListener(new OnFilePickedListener() {
                    @Override
                    public void onFilePicked(@NonNull File file) {
                        testEncryptFile(file);

                    }
                });
                FilePicker filePicker = new FilePicker(MainActivity.this);
                filePicker.setExplorerConfig(config);
                filePicker.show();*/
                ImgDataSeletor.startPickOneWitchDialog(MainActivity.this, new TakeOnePhotoListener() {
                    @Override
                    public void onSuccess(String path) {
                        File file = new File(path);
                       testEncryptFile(file);
                    }

                    @Override
                    public void onFail(String path, String msg) {
                        LogUtils.w(msg);
                        ToastUtils.showLong(msg);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }

            @Override
            public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {

            }
        },Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    private void testEncryptFile(File file) {
        File en = new File(getExternalCacheDir(),"en-"+file.getName());
        try {
            EncryptedUtil.writeToEncrypted(new FileInputStream(file),en);
        } catch (Throwable e) {
            LogUtils.w(e,file.getAbsolutePath());
        }

        File ori = new File(getExternalCacheDir(),"ori-en-"+file.getName());
        try {
            EncryptedUtil.decryptFile(en.getAbsolutePath(),ori.getAbsolutePath(),true);
        } catch (Throwable e) {
            LogUtils.w(e,en,ori);
        }
    }

    public void listKeystore(View view) {
        try{
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            Enumeration<String> aliases = ks.aliases();

            Map strings = new LinkedHashMap();
            while (aliases.hasMoreElements()){
                String s = aliases.nextElement();

                Key secretKey =  ks.getKey(s, null);
                strings.put(s,secretKey.toString());
                //android.security.keystore2.AndroidKeyStoreSecretKey
                //android.security.keystore2.AndroidKeyStoreRSAPrivateKey
                if(secretKey instanceof  SecretKey){
                    //SecretKeySpec
                    //android.security.keystore2.AndroidKeyStoreSecretKey
                    //strings.put(s,secretKey.toString());
                }else{
                    KeyStore.Entry entry = ks.getEntry(s, null);
                    if (entry instanceof KeyStore.PrivateKeyEntry ) {
                        KeyStore.PrivateKeyEntry entry1 = (KeyStore.PrivateKeyEntry) entry;
                        //strings.put(s,entry1.toString());
                    }
                }

            }
            LogUtils.w(strings);
        }catch (Throwable throwable){
            LogUtils.w(throwable);
        }

    }

    public void biometricSignature(View view) {
        Signature signature = null;
        try {
            signature = Signature.getInstance("");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(signature);
        BiometricHelper.showBiometricDialog(this, null,true, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
    }

    public void rsaCipher(View view) {
        try {
            byte[] encryptedData = RsaCipherUtil.encryptByPublicKey("rsaCipher", "abc".getBytes());
            byte[] data = RsaCipherUtil.decryptByPrivateKey("rsaCipher", encryptedData);
            LogUtils.i("rsaCipher-解密后数据: "+new String(data));
        } catch (Throwable e) {
            LogUtils.w(e);
        }
    }

    public void bioDecryptByPrivate(View view) {

        try {
            byte[]  encryptedData = RsaCipherUtil.encryptByPublicKeyWithUserVerify("bio-rsaCipher", "123456".getBytes());
            RsaCipherUtil.decryptByPrivateKeyWithUserVerify("bio-rsaCipher", encryptedData, true,
                    new MyCommonCallback3<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            LogUtils.i("bio-rsaCipher-解密后: "+new String(bytes));
                        }
                    });


        } catch (Throwable e) {
            LogUtils.w(e);
        }

    }

    public void signAndVerify(View view) {

        try {
            byte[] sign = SignUtil.sign("signtest", "123456".getBytes());
            boolean signtest = SignUtil.verify("signtest", sign, "123456".getBytes());
            LogUtils.i("签名验证结果: "+signtest);
        } catch (Throwable e) {
            LogUtils.w(e);
        }
    }

    public void signAndVerifyByBio(View view) {
        SignUtil.signWithUserVerify("signtest-bio", true,
                new MyCommonCallback3<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        try {
                            boolean signtest = SignUtil.verifyWithUserVerify("signtest-bio", bytes, "123456".getBytes());
                            LogUtils.i("签名验证结果: "+signtest);
                        } catch (Throwable e) {
                            LogUtils.w(e);
                        }
                    }
                },
                "123456".getBytes()
        );
    }

    public void aesEncrypt(View view) {
        String str = "123456789U";
        try {
            byte[] aeskeys = AesCipherUtil.encrypt("aeskey", str.getBytes());
            byte[] aeskeys1 = AesCipherUtil.decrypt("aeskey", aeskeys);
            LogUtils.d("解密后数据: "+new String(aeskeys1));
        } catch (Throwable e) {
            LogUtils.w(e);
        }

    }

    public void sslMock(View view) {
        SslUtil.test();
    }

    public void bioDecryptByPrivateForPasswordLogin(View view) {
        PasswordLoginByBiometric.savePw("7788","1234");
        PasswordLoginByBiometric.getPwByName("7788",
                true, new MyCommonCallback3<String>() {
            @Override
            public void onSuccess(String s) {
                MyToast.show("密码是: "+s);
            }
        });
    }



    public void crashTest(View view) {
        int i = 1/0;
    }

    public void viewHolderDemo(View view) {
        startActivity(new Intent(this, ViewHolderDemoActivity.class));
    }

    public void dirTree(View view) {


        BitmapSaveUtil.askWritePermission(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                FileTreeViewHolder.viewExternalStorage();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        FileTreeViewHolder.viewAppDir();


       /* Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(viewHolder.getRootView());
        dialog1.show();*/
    }

    public void goWebSpider(View view) {
        List<String> menus = SpiderWebviewActivity.getSpiders();
        menus.add("浏览全部下载列表");
        menus.add("浏览下载列表");
        menus.add("修复升级前的数据");
        menus.add("继续下载未完成的图片");

        PopList.showPop(this, -1, view, menus, new PopList.OnItemClickListener() {
            @Override
            public void onClick(int position, String str) {
                if(position == menus.size()-1){
                    MyDownloader.continueDownload();
                    //ImgDownloader.downladUrlsInDB(MainActivity.this,new File(SpiderWebviewActivity.getSaveDir("继续下载","")));
                }else if(position ==  menus.size()-4) {
                    MyDownloader.showWholeDownloadPage();
                }else if(position ==  menus.size()-3) {
                    MyDownloader.showDownloadPage();
                }else if(position == menus.size()-2) {
                    MyDownloader.fixDbWhenUpdate();
                }else {
                    SpiderWebviewActivity.start(MainActivity.this,str);
                }

            }
        });


    }

    public void bitmapSaveConfig(View view) {
        BitmapSaveUtil.openConfigPage();
    }

    public void bitmapSaveAction(View view) {
        try {
            view.setDrawingCacheEnabled(true);
            BitmapSaveUtil.saveBitmap(view.getDrawingCache());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fullScreenDialog2(View view) {
        TestFullBinding binding = TestFullBinding.inflate(getLayoutInflater());
        FullScreenDialogUtil.showFullScreen(binding.getRoot());
    }

    public void containeractivityWithTitle(View view) {
        ContainerActivity2.start( new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                TextView textView = new TextView(pair.first);
                textView.setBackgroundColor(Color.GREEN);
                textView.setText("center.........");
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(params);
                pair.second.getBinding().rlContainer.addView(textView);
                pair.second.getBinding().realTitleBar.setTitle("我是标题啦啦啦啦啦我是");

                pair.second.showRightMoreIcon(false);
                pair.second.getBinding().realTitleBar.getRightView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyToast.show("more icon clicked");
                    }
                });

            }
        });
    }

    public void containeractivityWithNoTitle(View view) {
        ContainerActivity2.start( new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                TextView textView = new TextView(pair.first);
                textView.setBackgroundColor(Color.GREEN);
                textView.setText("center.........");
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(params);
                pair.second.getBinding().rlContainer.addView(textView);
                pair.second.getBinding().realTitleBar.setTitle("我是标题啦啦啦啦啦我是");

                pair.second.setTitleBarHidden(true);

            }
        });
    }

    public void containeractivityWithTransTitle(View view) {
        ContainerActivity2.start( new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                TextView textView = new TextView(pair.first);
                textView.setBackgroundColor(Color.GREEN);
                textView.setText("center.........");
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(params);
                pair.second.getBinding().rlContainer.addView(textView);
                pair.second.getBinding().realTitleBar.setTitle("我是标题啦啦啦啦啦我是");
                pair.second.setTitleBarTransplantAndRelative(true);

            }
        });
    }

    public void containeractivityWithNoTitleAll(View view) {
        ContainerActivity2.start( new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                TextView textView = new TextView(pair.first);
                textView.setBackgroundColor(Color.GREEN);
                textView.setText("center.........");
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(params);
                pair.second.getBinding().rlContainer.addView(textView);
                pair.second.getBinding().realTitleBar.setTitle("我是标题啦啦啦啦啦我是");

                pair.second.setTitleBarHidden(false);


            }
        });
    }

    public void saveBitmapToAlbumWithoutPermission(View view) {
        try {
            View view1 = findViewById(android.R.id.content);
            view1.setDrawingCacheEnabled(true);
            Bitmap drawingCache = view1.getDrawingCache();

            /*File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
            externalFilesDir.mkdirs();
            File file = new File(externalFilesDir,System.currentTimeMillis()+".jpg");
            drawingCache.compress(Bitmap.CompressFormat.JPEG,85,new FileOutputStream(file));
            MediaStoreRefresher.refreshMediaCenter(getApplicationContext(),file.getAbsolutePath());*/

            BitmapSaveUtil.saveBitmap(drawingCache);
            ToastUtils.showShort("保存成功,跳去选择界面查看");
            MediaPickUtil.pickImage(new MyCommonCallback<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                }
            });
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            ToastUtils.showShort(throwable.getMessage());
        }

    }

    public void createDoc(View view) {
        // 创建一个新的 Intent 对象来创建文档
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
// 设置这个文件可以被打开
        intent.addCategory(Intent.CATEGORY_OPENABLE);
// 设置文件的 MIME 类型为 PDF
        intent.setType("text/plain");
// 设置创建的文件名
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.txt");

// 可选：为系统文件选择器指定打开的初始目录的 URI
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "file://"+Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Environment.DIRECTORY_DOWNLOADS);

// 开始 Activity 并请求返回结果
        StartActivityUtil.goOutAppForResult(this, intent, new ActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                LogUtils.d(data,data==null ? "null":data.getData());
                if(data !=null){
                    MyToast.show("可以往这个uri写文件了: \n"+data.getDataString());
                }

                //data.getData()
                //然后就可以往这个uri写文件流了
            }

            @Override
            public void onActivityNotFound(Throwable e) {

            }
        });
    }

    public void safDir(View view) {
        // 使用系统文件选择器选择一个目录
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        // 可选，指定一个 URI，作为系统文件选择器加载时应该打开的目录
        String pkgName = "com.hss01248.finalcompress";
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+pkgName);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        StartActivityUtil.goOutAppForResult(this, intent, new ActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                LogUtils.d(data,data.getData());
                MyToast.show("可以遍历这个目录了: \n"+data.getDataString());
                //data.getData()
                //然后就可以往这个uri写文件流了
            }

            @Override
            public void onActivityNotFound(Throwable e) {

            }
        });
    }

    public void sentryException(View view) {

        SentryUtil.testException();
    }

    public void sentryMsg(View view) {
        SentryUtil.testMsg(" i am a msg");
    }

    public void testMetrics1(View view) {
        SentryUtil.testMetrics1();
    }

    public void testMetrics2(View view) {
        SentryUtil.testMetrics2();
    }

    public void testInstrumentation(View view) {
        SentryUtil.testInstrumentation();
    }
}