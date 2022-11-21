# View state-View的状态管理

> loading-empty-error-content



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



# 可配置项

```java
public class ViewStateConfig {

    static ViewStateConfig globalConfig  = new ViewStateConfig.Builder()
            .loadingLayout(R.layout.pager_loading)
            .emptyLayout(R.layout.pager_empty)
            .errorLayout(R.layout.pager_error)
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

基于已有view包裹一层状态. 传入的view有无parent均可

```java
public static StatefulLayout wrapWithState(@NonNull View view, @Nullable ViewStateConfig config)

public static StatefulLayout wrapWithState(@NonNull View view, @NonNull Runnable errorClick)
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





# 三种使用方式示例

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

