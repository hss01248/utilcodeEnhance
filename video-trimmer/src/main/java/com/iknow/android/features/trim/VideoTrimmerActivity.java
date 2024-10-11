package com.iknow.android.features.trim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.activityresult.TheActivityListener;
import com.hss01248.video_trimmer.R;
import com.hss01248.video_trimmer.databinding.ActivityVideoTrimBinding;
import com.iknow.android.features.common.ui.BaseActivity;
import com.iknow.android.interfaces.VideoTrimListener;
import com.iknow.android.utils.ToastUtil;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoTrimmerActivity extends BaseActivity implements VideoTrimListener {

  private static final String VIDEO_PATH_KEY = "video-file-path";
  public static final int VIDEO_TRIM_REQUEST_CODE = 0x001;
  private ActivityVideoTrimBinding mBinding;
  private ProgressDialog mProgressDialog;

  public static void start(String videoPath, TheActivityListener<VideoTrimmerActivity> callback) {

      Bundle bundle = new Bundle();
      bundle.putString(VIDEO_PATH_KEY, videoPath);
      Intent intent = new Intent(ActivityUtils.getTopActivity(), VideoTrimmerActivity.class);
      intent.putExtras(bundle);
    StartActivityUtil.startActivity(ActivityUtils.getTopActivity(),VideoTrimmerActivity.class,intent,true,
            callback);

  }

  @Override public void initUI() {
    mBinding = ActivityVideoTrimBinding.inflate(getLayoutInflater());
    setContentView(mBinding.getRoot());
    Bundle bd = getIntent().getExtras();
    String path = "";
    if (bd != null) path = bd.getString(VIDEO_PATH_KEY);
    mBinding.trimmerView.setOnTrimVideoListener(this);
    mBinding.trimmerView.initVideoByURI(Uri.parse(path));
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onPause() {
    super.onPause();
    mBinding.trimmerView.onVideoPause();
    mBinding.trimmerView.setRestoreState(true);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mBinding.trimmerView.onDestroy();
  }

  @Override public void onStartTrim() {
    buildDialog(getResources().getString(R.string.trimming)).show();
  }

  @Override public void onFinishTrim(String in) {
    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    ToastUtil.longShow(this, getString(R.string.trimmed_done));
    Intent intent =new Intent();
    intent.putExtra("path",in);
    setResult(RESULT_OK,intent);
    finish();
    //TODO: please handle your trimmed video url here!!!
    //String out = StorageUtil.getCacheDir() + File.separator + COMPRESSED_VIDEO_FILE_NAME;
    //buildDialog(getResources().getString(R.string.compressing)).show();
    //VideoCompressor.compress(this, in, out, new VideoCompressListener() {
    //  @Override public void onSuccess(String message) {
    //  }
    //
    //  @Override public void onFailure(String message) {
    //  }
    //
    //  @Override public void onFinish() {
    //    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    //    finish();
    //  }
    //});
  }

  @Override public void onCancel() {
    mBinding.trimmerView.onDestroy();
    finish();
  }

  private ProgressDialog buildDialog(String msg) {
    if (mProgressDialog == null) {
      mProgressDialog = ProgressDialog.show(this, "", msg);
    }
    mProgressDialog.setMessage(msg);
    return mProgressDialog;
  }
}
