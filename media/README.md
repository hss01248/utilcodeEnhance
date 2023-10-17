# 多媒体相关-系统能力封装

* 多媒体选择-选图,选音频,选视频,选文件/文档
* 多媒体制作-拍照,录音,录像

# api

![image-20231017151042192](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20231017151042192.png)



# android13照片/视频选择器

> 结论: 不需要权限,但只能看到相机,截图,下载 三个文件夹的图,其他公开的图都看不到,选不了. 
>
> 辣鸡api!

```java
final int maxNumPhotosAndVideos = 10;
Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxNumPhotosAndVideos);
startActivityForResult(intent, PHOTO_PICKER_MULTI_SELECT_REQUEST_CODE);
```

没有权限时, 只能访问绝对公开的照片,连mediastore中其他文件夹的都不能访问:

申请了Manifest.permission.READ_MEDIA_IMAGES,依然只有这些文件夹,不能访问其他公开的媒体文件夹.

![image-20231017154828604](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20231017154828604.png)

而通过以前的intent的方式(Intent.ACTION_PICK),在申请了Manifest.permission.READ_MEDIA_IMAGES权限后,能够看到mediastore

的所有图片:

![image-20231017155738471](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20231017155738471.png)

官方文档:

https://developer.android.com/about/versions/13/features/photopicker?hl=zh-cn

https://developer.android.com/training/data-storage/shared/photopicker?hl=zh-cn