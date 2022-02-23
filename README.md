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
```



[![](https://jitpack.io/v/hss01248/utilcodeEnhance.svg)](https://jitpack.io/#hss01248/utilcodeEnhance)

# 使用

```groovy
api 'com.github.hss01248.utilcodeEnhance:enhance:1.0.0'
api 'com.github.hss01248.utilcodeEnhance:ext:1.0.0'
```

