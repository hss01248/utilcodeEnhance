# View state-View的状态管理工具

> loading(place holder/shimmer)
>
> empty
>
> error
>
> content

> 页面交互语言:
>
> 进入页面--->使用页面状态控制
>
> 表单提交类-->点击按钮后,使用loading dialog控制,错误弹出toast或alert dialog,而不是单独对提交按钮进行状态变更(too  stupid).

# 两种类型:

* 页面级: 三种或四种状态,一般列表类页面才有empty状态.
* 单个小view级别: loading和error,content三种状态

## 页面级

```java
    static ViewStateConfig globalConfig  = new Builder()
            .loadingLayout(R.layout.pager_loading)
            .emptyLayout(R.layout.pager_empty)
            .errorLayout(R.layout.pager_error)
            .build();
```



![image-20221122103609346](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac2/image-20221122103609346.png)

![image-20221122103639897](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac2/image-20221122103639897.png)



![image-20221122103557010](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac2/image-20221122103557010.png)



## 小view级别

全局配置

```java
 static ViewStateConfig globalSmallViewConfig  = new Builder()
            .loadingLayout(R.layout.small_view_loading)
            .emptyLayout(R.layout.small_view_place_holder)
            .errorLayout(R.layout.small_view_error)
            .build();
```

使用

```java
  stateLayout1 = StatefulLayout.wrapWithStateOfSmallView(binding.rl1, new Runnable() {
            @Override
            public void run() {
                stateLayout1.showLoading();
            }
        });
        stateLayout1.showLoading();
```





![image-20221122113241977](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac2/image-20221122113241977.png)

![image-20221122113210184](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac2/image-20221122113210184.png)



![image-20221122113311321](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac2/image-20221122113311321.png)



布局:

![image-20221122113526630](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac2/image-20221122113526630.png)

> **使用注意事项**: 按view本身顺序来添加state,否则顺序会错乱. 上面示例就是2-3顺序错乱了

# 接口定义

```java
public interface IViewState {

    void showLoading(@Nullable  CharSequence msg);
    void showEmpty(@Nullable  CharSequence msg,@Nullable @DrawableRes int icon,@Nullable  CharSequence btnText,@Nullable Runnable emptyClick);
    void showError(@Nullable  CharSequence msg,@Nullable @DrawableRes int icon, @Nullable  CharSequence btnText,@Nullable Runnable errorClick);
    void showContent();

    default void showLoading(){
        showLoading("");
    }
    default void showEmpty(){
        showEmpty("",0,null,null);
    }
    default void showError(String msg){
        showError(msg,0,null,null);
    }

}
```



如果没有网络,则点击重试按钮会优先弹窗引导去设置页面打开网络:

```java
   if (!NoNetworkHelper2.isNetWorkAvailable(v.getContext())) {
     	NoNetworkHelper2.showNoNetWorkDlg(v.getContext());
   } else {
     if (finalEmptyClick != null) {
       finalEmptyClick.run();
     }
   }
```



![image-20221122142054906](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac2/image-20221122142054906.png)

此弹窗样式可全局更改配置:

```java
public class NoNetworkHelper2 {

    public static void setShowDialogImpl(IShowDialog showDialog) {
        NoNetworkHelper2.showDialog = showDialog;
    }

    static IShowDialog showDialog;

    public interface IShowDialog{
        void showNoNetWorkDlg(final Context context);
    }
```





# 可配置项

```java
public class ViewStateConfig {

    static ViewStateConfig globalConfig  = new ViewStateConfig.Builder()
            .loadingLayout(R.layout.pager_loading)
            .emptyLayout(R.layout.pager_empty)
            .errorLayout(R.layout.pager_error)
            .build();
  
      static ViewStateConfig globalSmallViewConfig  = new Builder()
            .loadingLayout(R.layout.small_view_loading)
            .emptyLayout(R.layout.small_view_place_holder)
            .errorLayout(R.layout.small_view_error)
            .build();

     @LayoutRes int emptyLayout;
     @LayoutRes int errorLayout;
     @LayoutRes int loadingLayout ;


     String loadingMsg = "";
     String emptyMsg = "";

     @DrawableRes int emptyIcon = 0;
     @DrawableRes int errorIcon = 0;

    String errorBtnText = "";
    String emptyBtnText = "";
     Runnable emptyClick = null;
     Runnable errorClick = null;
```



# api-StatefulLayout

* 基于已有view包裹一层状态. 传入的view有无parent均可
* 自定义整个状态的layout界面时,只支持代码传入,不支持xml里指定layout id,没这个闲工夫支持xml这种落后的配置方式.

```java
public static StatefulLayout wrapWithState(@NonNull View view, @Nullable ViewStateConfig config)

public static StatefulLayout wrapWithStateOfSmallView(@NonNull View view, @NonNull Runnable errorClick)
public static StatefulLayout wrapWithStateOfPage(@NonNull View view,@NonNull Runnable errorClick)
```

//已有容器,再配置

```java
public void setConfig(ViewStateConfig config)
public StatefulLayout setErrorClick(@NonNull Runnable errorClick)
 
```



//现有容器,再动态添加view

```java
public void setContentView(View contentView)
```





# 示例

### xml里

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.hss01248.viewstate.StatefulLayout
        android:id="@+id/state_layout"
        android:background="#ff0000"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@color/black"
            android:padding="50dp"
            android:text="i am content"
            android:textColor="@color/white" />
    </com.hss01248.viewstate.StatefulLayout>

</LinearLayout>

```

```java
  binding.stateLayout.setConfig(ViewStateConfig.Builder
                .newBuilder()
                .errorClick(new Runnable() {
                    @Override
                    public void run() {
                        doNet();
                    }
                }).build());

        doNet();

    private void doNet() {
        binding.stateLayout.showLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int state = new Random().nextInt(3) + 1;
                switch (state) {
                    case StatefulLayout.ERROR:
                        binding.stateLayout.showError("稍候重试");
                        break;
                    case StatefulLayout.EMPTY:
                        binding.stateLayout.showEmpty();
                        break;
                    case StatefulLayout.CONTENT:
                        binding.stateLayout.showContent();
                }
            }
        }, 2000);
    }
```

## statelayout不写到xml中:

```java
 stateLayout = StatefulLayout.wrapWithState(binding.llRoot,
                ViewStateConfig.Builder
                .newBuilder()
                .errorClick(new Runnable() {
                    @Override
                    public void run() {
                        doNet();
                    }
                }).build());

        doNet();
```

