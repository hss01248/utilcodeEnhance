# utilcode的加强版

> 基于com.blankj:utilcodex:1.30.0

内含:

```groovy
    api 'com.github.hss01248.HttpUtil2:openuri:3.0.5'
//Android7.0 file转uri,expose
    api 'com.github.hss01248.StartActivityResult:activityresult:1.1.4'
//回调形式的startActivityForResult,完美兼容应用内,应用外跳转, 兼容Android4.4-Android12
    api 'com.blankj:utilcodex:1.30.0'//基底
    api "io.reactivex.rxjava2:rxjava:2.2.6"
    api "io.reactivex.rxjava2:rxandroid:2.1.0"
    api "com.google.code.gson:gson:2.8.5"
    api group: 'com.squareup.okhttp3', name: 'okhttp', version: '3.12.12'
//兼容Android5以下的okhttp版本
    api 'com.github.hss01248.MyPermission:permission:1.0.6'
//基于utilcodex里的permissionutil的产品级权限库,开箱即用,内置权限交互的产品逻辑
    api 'com.github.hss01248.MyPermission:location:1.0.6' 
//尽最大努力获取定位,饱和式定位申请
    api 'com.github.hss01248.MyPermission:ext:1.0.6' 
//一行代码进行特殊权限申请
    api 'com.github.hss01248:MyDataStore:1.0.1' 
//mmkv和shareprefence的api包装,优先mmkv,无缝切换,无崩溃之忧


// 最新:
com.github.hss01248.utilcodeEnhance:toast:1.6.4
com.github.hss01248.utilcodeEnhance:biometric:1.6.4
com.github.hss01248.utilcodeEnhance:lock-app:1.6.4
com.github.hss01248.utilcodeEnhance:search-history-api:1.6.4
com.github.hss01248.utilcodeEnhance:openuri:1.6.4
com.github.hss01248.utilcodeEnhance:IReporter:1.6.4
com.github.hss01248.utilcodeEnhance:crash-remote:1.6.4
com.github.hss01248.utilcodeEnhance:lib-base:1.6.4
com.github.hss01248.utilcodeEnhance:viewholder:1.6.4
com.github.hss01248.utilcodeEnhance:common:1.6.4
com.github.hss01248.utilcodeEnhance:bitmap-saver:1.6.4
com.github.hss01248.utilcodeEnhance:qr-scan:1.6.4
com.github.hss01248.utilcodeEnhance:fullScreenDialog:1.6.4
com.github.hss01248.utilcodeEnhance:ext:1.6.4
com.github.hss01248.utilcodeEnhance:crash:1.6.4
com.github.hss01248.utilcodeEnhance:media:1.6.4
com.github.hss01248.utilcodeEnhance:cipher:1.6.4
com.github.hss01248.utilcodeEnhance:baseWebview:1.6.4
com.github.hss01248.utilcodeEnhance:viewholder-media:1.6.4
com.github.hss01248.utilcodeEnhance:refresh-loadmore:1.6.4
com.github.hss01248.utilcodeEnhance:iwidget:1.6.4
com.github.hss01248.utilcodeEnhance:viewState:1.6.4
com.github.hss01248.utilcodeEnhance:sentry:1.6.4
com.github.hss01248.utilcodeEnhance:lib-base-base:1.6.4
```



[![](https://jitpack.io/v/hss01248/utilcodeEnhance.svg)](https://jitpack.io/#hss01248/utilcodeEnhance)

# 使用

```groovy
api 'com.github.hss01248.utilcodeEnhance:xxx:1.2.0'

com.github.hss01248.utilcodeEnhance:ext:1.2.0
com.github.hss01248.utilcodeEnhance:lib-base:1.2.0   //核心库
com.github.hss01248.utilcodeEnhance:common:1.2.0
com.github.hss01248.utilcodeEnhance:media:1.2.0   //媒体文件选择/拍照,录制
com.github.hss01248.utilcodeEnhance:qr-scan:1.2.0
com.github.hss01248.utilcodeEnhance:fullScreenDialog:1.2.0
com.github.hss01248.utilcodeEnhance:toast:1.2.0
com.github.hss01248.utilcodeEnhance:search-history-api:1.2.0
com.github.hss01248.utilcodeEnhance:iwidget:1.2.0
com.github.hss01248.utilcodeEnhance:openuri:1.2.0
com.github.hss01248.utilcodeEnhance:refresh-loadmore:1.2.0
com.github.hss01248.utilcodeEnhance:basewebview-ajax-proxy:1.2.0
com.github.hss01248.utilcodeEnhance:baseWebview:1.2.0
com.github.hss01248.utilcodeEnhance:viewState:1.2.0
com.github.hss01248.utilcodeEnhance:IReporter:1.2.0
```



# 二维码和条形码扫码

内部基于 com.github.bingoogolapple.BGAQRCode-Android:zbar:1.3.8

```groovy
api 'com.github.hss01248.utilcodeEnhance:qr-scan:1.0.8'
```



```java
ScanCodeActivity.scanForResult(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                ToastUtils.showLong(s);
                BaseWebviewActivity.start(ActivityUtils.getTopActivity(),s);
            }
        });
```





![image-20221220175628125](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20221220175628125.png)

# 软键盘适配

```java
//内部自动在ondestory时取消
new MyKeyboardUtil(this)
        .addOnKeyBoardStateListener(new MyKeyboardUtil.OnKeyBoardStateListener() {
            @Override
            public void onSoftKeyBoardShow(int keyboardHeight) {
                ViewGroup.LayoutParams layoutParams = quickWebview.getLayoutParams();
                layoutParams.height = totalHeight - keyboardHeight;
                quickWebview.setLayoutParams(layoutParams);
            }

            @Override
            public void onSoftKeyBoardHide() {
                ViewGroup.LayoutParams layoutParams = quickWebview.getLayoutParams();
                layoutParams.height = totalHeight ;
                quickWebview.setLayoutParams(layoutParams);
            }
        });

//对应activity需要配置:
      <activity android:name=".BaseWebviewActivity"
            android:configChanges="orientation|keyboardHidden|keyboard|screenSize"
            android:windowSoftInputMode="adjustPan"
```
