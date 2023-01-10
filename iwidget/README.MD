# 常用ui组件

[![](https://jitpack.io/v/hss01248/utilcodeEnhance.svg)](https://jitpack.io/#hss01248/utilcodeEnhance)

```groovy
api "com.github.hss01248.utilcodeEnhance:iwidget:1.1.3"
```



# alert dialog

```java
 new AlertDialogImplByDialogUtil().showMsg("普通标题",
                "想要监控对话框的生命周期，可以实现其 .setDialogLifecycleCallback(...) 接口，建议使用build()方法构建对话框",
                "确认", "取消",
                new BaseDialogListener() {
                    @Override
                    public void onConfirm() {
                        BaseDialogListener.super.onConfirm();
                    }
                });
```

单个按钮时:

![image-20230110164444584](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110164444584.png)

![image-20230110164738665](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110164738665.png)

文本超级长的情况: 中间msg可滑动

![image-20230110164528274](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110164528274.png)



# 对Androidx里alertDialog的样式全局修改:

> 可全局改成上方的DialogUtil样式. 
>
> 不能修改ProgressDialog的样式

根目录的build.gradle的buildScript里: 

```groovy
apply from: 'https://raw.githubusercontent.com/hss01248/flipperUtil/dev/remote3.gradle'
```

然后gradle.properties里加上配置:

```properties
flipper_alert_dialog_like_ios=true
```

# 单选dialog/pop

## 普通模式

![image-20230110162958923](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110162958923.png)

![image-20230110163704342](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110163704342.png)

选项超级多时:

![image-20230110164257313](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110164257313.png)

![image-20230110163113990](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110163113990.png)

```java
//底部选择框
void showAtBottom(CharSequence title, CharSequence[] datas, SingleChooseDialogListener listener);
//页面中间 弹窗
void showInCenter(CharSequence title, CharSequence[] datas, SingleChooseDialogListener listener);
//menu形式
void showInPopMenu(View view,int checkedIndex, CharSequence[] datas, SingleChooseDialogListener listener);
```



使用

```java
 new SingleChooseDialogImpl().showAtBottom(
                        StringUtils.getString(R.string.meida_pick_please_choose),
                        new String[]{StringUtils.getString(R.string.meida_pick_take_photo),
                                StringUtils.getString(R.string.meida_pick_record_video)},
                        new SingleChooseDialogListener() {
                           @Override
                            public void onItemClicked(int position, CharSequence text) {
                              //todo
                            }
                        });
```

## 可自由增减模式

```java
ISingleChooseItem:

static <T> void showAsMenu(View targetView, List<ISingleChooseItem<T>> menus, T info)

static <T> void showAtBottom( List<ISingleChooseItem<T>> menus, T info)
  
static <T> void showInCenter( List<ISingleChooseItem<T>> menus, T info)
```



# loading dialog

```java
    Dialog dialog;
    public void showLoading(View view) {
         dialog = MyToast.showLoadingDialog("");
        //dialog.setCancelable(true);
    }

    public void dismissLoading(View view) {
        MyToast.dismissLoadingDialog(dialog);
    }
```



![image-20230110165306671](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110165306671.png)

# toast

```java
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
```

![image-20230110165443700](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110165443700.png)

![image-20230110165505306](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110165505306.png)

![image-20230110165537666](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110165537666.png)

![image-20230110165615674](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20230110165615674.png)
