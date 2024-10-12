package com.iknow.android.features.trim;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.iknow.android.interfaces.VideoTrimListener;
import com.mobile.ffmpeg.util.FFmpegAsyncUtils;
import com.mobile.ffmpeg.util.FFmpegExecuteCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoTrimmerUtil {

  private static final String TAG = VideoTrimmerUtil.class.getSimpleName();
  public static final long MIN_SHOOT_DURATION = 500L;// 最小剪辑时间500ms
  public static final int VIDEO_MAX_TIME = 10;// 10秒
  public static final long MAX_SHOOT_DURATION = VIDEO_MAX_TIME * 3000L;//视频最多剪切多长时间10s

  public static final int MAX_COUNT_RANGE = 15;  //seekBar的区域内一共有多少张图片
  private static final int SCREEN_WIDTH_FULL = ScreenUtils.getScreenWidth();
  public static final int RECYCLER_VIEW_PADDING = SizeUtils.dp2px(35);
  public static final int VIDEO_FRAMES_WIDTH = SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2;
  public static final int THUMB_WIDTH = (SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2) / VIDEO_MAX_TIME;
  private static final int THUMB_HEIGHT = SizeUtils.dp2px(50);


  public static void trimLast(String path, long reduceLastInMills,MyCommonCallback3<String> callback3){
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    retriever.setDataSource(path);
    long mills = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    mills = mills - reduceLastInMills;
    //ffmpeg -i input.mp4 -t 00:02:29.500 -c copy output.mp4
    //String  time = convertSecondsToTime2(mills);
    File file =  new File(path);
    File file1 = new File(file.getParentFile(),"sub-"+file.getName());
    String cmd = "-i " + path + " -t "+mills+" -c copy "+file1.getAbsolutePath();
   String[] command = cmd.split(" ");

    FFmpegAsyncUtils asyncTask =new FFmpegAsyncUtils();

    asyncTask.setCallback(new FFmpegExecuteCallback() {

      @Override
      public void onFFmpegStart() {

      }

      @Override
      public void onFFmpegSucceed( String executeOutput) {
        callback3.onSuccess(file1.getAbsolutePath());

      }

      @Override
      public void onFFmpegFailed(@Nullable String executeOutput) {
        //ToastUtil.show(context.getApplicationContext(), executeOutput);
        callback3.onError(executeOutput+"");
      }

      @Override
      public void onFFmpegProgress(@Nullable Integer progress) {
// fload mprogress = progress/执行视频文件或语音文件时长
      }

      @Override
      public void onFFmpegCancel() {

      }
    });
    asyncTask.execute(command);


  }

  public static void trim(Context context, String inputFile, String outputFile, long startMs, long endMs, final VideoTrimListener callback) {
    final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    final String outputName = "trimmedVideo_" + timeStamp + ".mp4";
    outputFile = outputFile + "/" + outputName;

    float start = startMs / 1000f;
    float duration = (endMs - startMs) / 1000f;
            //convertSecondsToTime((endMs - startMs) / 1000);

    //ffmpeg -i input.mp4 -t 00:02:29.500 -c copy output.mp4


    //String start = String.valueOf(startMs);
    //String duration = String.valueOf(endMs - startMs);

    /** 裁剪视频ffmpeg指令说明：
     * ffmpeg -ss START -t DURATION -i INPUT -codec copy -avoid_negative_ts 1 OUTPUT
     -ss 开始时间，如： 00:00:20，表示从20秒开始；
     -t 时长，如： 00:00:10，表示截取10秒长的视频；
     -i 输入，后面是空格，紧跟着就是输入视频文件；
     -codec copy -avoid_negative_ts 1 表示所要使用的视频和音频的编码格式，这里指定为copy表示原样拷贝；
     INPUT，输入视频文件；
     OUTPUT，输出视频文件
     */
    //TODO: Here are some instructions
    //https://trac.ffmpeg.org/wiki/Seeking
    //https://superuser.com/questions/138331/using-ffmpeg-to-cut-up-video

    String cmd = "-ss " + start + " -t " + duration + " -accurate_seek" + " -i " + inputFile + " -codec copy -avoid_negative_ts 1 " + outputFile;
    //String cmd = "-ss " + start + " -i " + inputFile + " -ss " + start + " -t " + duration + " -vcodec copy " + outputFile;
    //{"ffmpeg", "-ss", "" + startTime, "-y", "-i", inputFile, "-t", "" + induration, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", outputFile}
    //String cmd = "-ss " + start + " -y " + "-i " + inputFile + " -t " + duration + " -vcodec " + "mpeg4 " + "-b:v " + "2097152 " + "-b:a " + "48000 " + "-ac " + "2 " + "-ar " + "22050 "+ outputFile;

    LogUtils.d("cmd list: "+cmd);
    String[] command = cmd.split(" ");
    try {
      final String tempOutFile = outputFile;

      FFmpegAsyncUtils asyncTask =new FFmpegAsyncUtils();
      String finalOutputFile = outputFile;
      asyncTask.setCallback(new FFmpegExecuteCallback() {

        @Override
        public void onFFmpegStart() {
          callback.onStartTrim();
        }

        @Override
        public void onFFmpegSucceed( String executeOutput) {
          callback.onFinishTrim(finalOutputFile);
        }

        @Override
        public void onFFmpegFailed(@Nullable String executeOutput) {
          //ToastUtil.show(context.getApplicationContext(), executeOutput);
          callback.onCancel();
        }

        @Override
        public void onFFmpegProgress(@Nullable Integer progress) {
// fload mprogress = progress/执行视频文件或语音文件时长
        }

        @Override
        public void onFFmpegCancel() {

        }
      });
      asyncTask.execute(command);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void shootVideoThumbInBackground(final Context context, final Uri videoUri, final int totalThumbsCount, final long startPosition,
      final long endPosition, final MyCommonCallback3<Pair<Bitmap, Integer>> callback) {

    ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Pair<Bitmap,Integer>>() {
      @Override
      public Pair<Bitmap, Integer> doInBackground() throws Throwable {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(context, videoUri);
        // Retrieve media data use microsecond
        long interval = (endPosition - startPosition) / (totalThumbsCount - 1);
        for (long i = 0; i < totalThumbsCount; ++i) {
          long frameTime = startPosition + interval * i;
          Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
          if(bitmap == null) continue;
          try {
            bitmap = Bitmap.createScaledBitmap(bitmap, THUMB_WIDTH, THUMB_HEIGHT, false);
          } catch (final Throwable t) {
            t.printStackTrace();
          }
          callback.onSuccess(new Pair<>(bitmap, (int) interval));
        }
        mediaMetadataRetriever.release();
        return new Pair<>(null,0);
      }

      @Override
      public void onSuccess(Pair<Bitmap, Integer> result) {

      }

      @Override
      public void onFail(Throwable t) {
        super.onFail(t);
        callback.onError(t.getMessage());
      }
    });
  }

  public static String getVideoFilePath(String url) {
    if (TextUtils.isEmpty(url) || url.length() < 5) return "";
    if (url.substring(0, 4).equalsIgnoreCase("http")) {

    } else {
      url = "file://" + url;
    }

    return url;
  }

  public static String convertSecondsToTime(long seconds) {
    String timeStr;
    int hour;
    int minute;
    int second;
    if (seconds <= 0) {
      return "00:00";
    } else {
      minute = (int) seconds / 60;
      if (minute < 60) {
        second = (int) seconds % 60;
        timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
      } else {
        hour = minute / 60;
        if (hour > 99) return "99:59:59";
        minute = minute % 60;
        second = (int) (seconds - hour * 3600 - minute * 60);
        timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
      }
    }
    return timeStr;
  }

  public static String convertSecondsToTime2(long mills) {
    String timeStr;
    int hour;
    int minute;
    int second;
    if (mills <= 0) {
      return "00:00:000";
    } else {
      if (mills < 1000) {
        return "00:00:" + unitFormat2((int) mills);
      }
      int mil = (int) (mills % 1000);
      long se =  (mills / 1000);
      return convertSecondsToTime(se) + ":" + unitFormat2(mil);
    }
  }

  private static String unitFormat(int i) {
    String retStr;
    if (i >= 0 && i < 10) {
      retStr = "0" + i;
    } else {
      retStr = "" + i;
    }
    return retStr;
  }
  private static String unitFormat2(int i) {
    String retStr ;
    if (i >= 0 && i < 10) {
      retStr = "00" + i;
    } else if(i > 10 && i < 100){
      retStr = "0" + i;
    }else {
      retStr = "" + i;
    }
    return retStr;
  }
}
