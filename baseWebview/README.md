# webview

# 核心类: BaseQuickWebview

主要api:

![image-20230112163507622](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112163507622.png)



### 提供activity承载: BaseWebviewActivity

```java
BaseWebviewActivity.start(Activity activity, String url)
```

# demo下载

https://www.pgyer.com/kYyS

![img](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/kYyS.png)

## 主要测试页面:

https://navi.hss01248.tech/tabShare/12

![image-20230112163605022](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112163605022.png)

# webchromeClient的所有回调方法的含义

https://www.jianshu.com/p/a7ac66691359



# titlebar

* 左边有返回和关闭按钮
* title为跑马灯,无限循环播放
* 右边有菜单按钮和刷新按钮
* titlebar和webview主题之间有阴影



![image-20230112163219210](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112163219210.png)

## 一些js弹窗

alert

![image-20230112162857419](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112162857419.png)

prompt

![image-20230112162920098](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112162920098.png)

prompt

![image-20230112163003184](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112163003184.png)

## 新开window的处理:

[JsCreateNewWinImpl.java](https://github.com/hss01248/utilcodeEnhance/blob/master/baseWebview/src/main/java/com/hss01248/basewebview/dom/JsCreateNewWinImpl.java)

window.open

a标签 target=blank

这些方式可以开启新的窗口

在uc,chrome里一般用fragment承载,我这里直接以新activity承载

对一些下载,广告的新弹窗进行的处理.

禁用了activity退出动画,避免闪屏



## 权限的处理

webRtc用到的getUserMedia需要录像,会用到camera和audio权限,需要适配

Geolocation api会需要定位权限,也需要处理. 同时要处理定位开关的问题.

完美实现: 

[JsPermissionImpl.java](https://github.com/hss01248/utilcodeEnhance/blob/master/baseWebview/src/main/java/com/hss01248/basewebview/dom/JsPermissionImpl.java)

## 文件选择的完美适配

intput标签 file, 以及一些文件类型的限制,是否实时采集的限制.

什么时候用intent的什么action,都是有讲究的

这才是完美的适配:

[FileChooseImpl.java](https://github.com/hss01248/utilcodeEnhance/blob/master/baseWebview/src/main/java/com/hss01248/basewebview/dom/FileChooseImpl.java)

![image-20230112162718178](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112162718178.png)

# 菜单功能可扩展

![image-20230112154111564](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112154111564.png)



```java
public void addRightMenus(IShowRightMenus showRightMenus)
```



# 扫码功能

使用: [qr-scan](https://github.com/hss01248/utilcodeEnhance/tree/master/qr-scan)

基于BGAQRCode-Android:zbar

扫码后调用quickwebview.loadUrl(str)

如果是http开头,就直接加载网页,如果不是,就会调用百度搜索该文字

![image-20230112160422475](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112160422475.png)

![image-20230112160716563](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112160716563.png)



# 分享

调用系统分享功能

![image-20230112160810205](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112160810205.png)

# 长按查看图片功能

[TheLongPressListener.java](https://github.com/hss01248/utilcodeEnhance/blob/master/baseWebview/src/main/java/com/hss01248/basewebview/TheLongPressListener.java)

![image-20230112155858600](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112155858600.png)

![image-20230112160019746](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112160019746.png)

# 收藏/浏览记录

> onPageFinished时会自动添加到浏览记录里
>
> 收藏: 点击菜单栏可添加到收藏
>
> 均存储在本地数据库,分页查看,每页20条
>
> 依然是用全屏dialog展示

![image-20230112161009975](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112161009975.png)



# 搜索功能和搜索历史

[WebSearchViewHolder.java](https://github.com/hss01248/utilcodeEnhance/blob/master/baseWebview/src/main/java/com/hss01248/basewebview/search/WebSearchViewHolder.java)

> 模仿uc的交互,点击网页title,跳到搜索页面. 搜索页面以全屏dialog承载,看起来和activity没有任何区别,但不会影响webview所在activity的生命周期,也更方便通信

![image-20230112153952100](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112153952100.png)

# 软键盘适配

[MyKeyboardUtil.java](https://github.com/hss01248/utilcodeEnhance/blob/master/lib-base/src/main/java/com/hss/utils/enhance/MyKeyboardUtil.java)

在BaseQuickWebview构造方法里:

```java
MyKeyboardUtil.adaptView(this);
```

内部通过context取appcompactActivity,添加生命周期,自动注销监听.

![image-20230112155438447](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112155438447.png)



# 广告拦截

参考 [android WebView实践总结(四) WebView网页广告拦截(AdBlock)](https://www.jianshu.com/p/0664d3398076)

内置广告域名/path黑名单

在shouldInterceptRequest以及新开window的shouldOverrideUrlLoading拦截.



# 小说网页开启阅读模式和预下载

//todo

# 下载管理

//todo 实现app内下载和维护下载列表

>  目前默认使用系统的downloadManager, 没有在本app内维护列表.

![image-20230112161745641](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112161745641.png)

![image-20230112161825066](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230112161825066.png)
