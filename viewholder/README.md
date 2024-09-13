# View Holder

类似flutter widget的思路,一切皆viewholder

* 利用viewBinding,只需要写xml布局和处理数据设置

* 感知生命周期

* 拦截后退键



![image-20240913100104019](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913100104019.png)

# gradle:

```groovy
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

dependencies {
	        implementation  "com.github.hss01248.utilcodeEnhance:viewholder:1.6.8"
	}

```





# demo

开启viewBinding

```groovy
viewBinding {
   enabled = true
}
```

书写xml

在类上声明对应的binding.

实现其ViewHolder(Context context)和initDataAndEventInternal方法



```java
public class DemoViewHolder extends BaseViewHolder<ActivityViewHolderDemoHttpStatusBinding,String> {
    StatefulLayout stateManager;
    public DemoViewHolder(Context context) {
        super(context);

        stateManager = StatefulLayout.wrapWithStateOfPage(rootView, new Runnable() {
            @Override
            public void run() {
                init("重试后结果成功");
            }
        });
        rootView = stateManager;
    }


    OnBackPressedCallback callback;
    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, String bean) {
        stateManager.showLoading("loading...xxxx");
        if(callback ==null){
          //初始状态为拦截后退键: true
            callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    MyToast.show("点击了后退键2222");
                }
            };

          //添加后退键的处理:
            getBackPressedDispatcher().addCallback(lifecycleOwner,callback);
        }
        ThreadUtils.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(new Random().nextBoolean()){
                    stateManager.showContent();
                    binding.tvContent.setText(bean);
                    //拦截后退键
                    callback.setEnabled(true);
                }else {
                    stateManager.showError("请求错误");
                    //不再拦截后退键
                    callback.setEnabled(false);
                }

            }
        }, 2000);
    }
}
```

外部调用为DemoViewHolder().init(String)

```java
public class ViewHolderDemoActivity extends AppCompatActivity {

    DemoViewHolder viewHolder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewHolder = new DemoViewHolder(this);
        setContentView(viewHolder.getRootView());

        viewHolder.init("请求成功");
    }
}
```

# 已写好的几个viewholder相关工具:

## ContainerViewHolderWithTitleBar

![image-20240913101311307](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913101311307.png)

![image-20240913101349120](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913101349120.png)

## ContainerActivity2

![image-20240913101601837](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913101601837.png)



> start a activity without any intent , just a callback, set your data, handle the event in the callback

```java
    public static void start(Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>> onActivityCreate){
        Intent intent = new Intent(ActivityUtils.getTopActivity(),ContainerActivity2.class);
        String hashCode = onActivityCreate.toString();
        LogUtils.w("onActivityCreate.toString(): "+hashCode);
        intent.putExtra("onActivityCreateHashCode",hashCode);
        ContainerActivity2.onActivityCreateMap.put(hashCode,onActivityCreate);
        ActivityUtils.getTopActivity().startActivity(intent);
    }
```





### 示例1:container activity WithTitle

![image-20240913102218332](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913102218332.png)

```java
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
```

### container activity With No Title, but has status bar 

![image-20240913102342176](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913102342176.png)

```java
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
```

### container activity With TransTitle

相对布局,标题透明

![image-20240913102754284](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913102754284.png)

```java
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
```

### containeractivityWithNoTitleAll  , no status bar stub

![image-20240913102443229](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913102443229.png)

```java
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
```



# viewpager的专用viewholder:

```java
public abstract class MyPagerViewHolder<VB extends ViewBinding, T> implements DefaultLifecycleObserver
```



![image-20240913104516980](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913104516980.png)

![image-20240913104850267](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913104850267.png)



# recyclerview的专用viewholder:

```java
public abstract class MyRecyclerViewHolder<VB extends ViewBinding,T> extends RecyclerView.ViewHolder implements DefaultLifecycleObserver
```



![image-20240913104347295](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913104347295.png)



![image-20240913104818773](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20240913104818773.png)

只需要新建一个类继承它即可:

binding在外部传入

无需新建adapter类.

```java
   adapter = new MyRecyclerViewAdapter() {
                @Override
                protected MyRecyclerViewHolder generateNewViewHolder(int viewType) {

                        LayoutFileItemGridBinding inflate = LayoutFileItemGridBinding.inflate(
                                LayoutInflater.from(context), binding.getRoot(), false);
                        return new FileItemGridViewHolder(inflate.getRoot())
                                .setOnItemClicked(onItemClicked)
                                .setFilterInfo(filterInfo)
                                .setBinding(inflate);
                    
                }
            };
            binding.recyclerView.setAdapter(adapter);
```

