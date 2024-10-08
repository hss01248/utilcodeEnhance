package com.hss01248.basewebviewdemo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hss01248.basewebview.BaseWebviewActivity;
import com.hss01248.basewebview.video.ClipboardMonitorService;



import top.zibin.luban.LubanUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LubanUtil.setEnableLog(true,true);
        ClipboardMonitorService.startMonitor();
    }

    public void geoLocation(View view) {
        BaseWebviewActivity.start(this,"https://www.runoob.com/try/try.php?filename=tryhtml5_geolocation");
    }

    public void test401Login(View view) {
        BaseWebviewActivity.start(this,"http://192.168.1.12:9625");
    }

    public void showOpenFilePicker(View view) {
        BaseWebviewActivity.start(this,"https://www.zhangxinxu.com/study/202108/button-picker-file-upload.php");
    }

    public void jsInputFile(View view) {
        BaseWebviewActivity.start(this,"https://developer.mozilla.org/zh-CN/docs/Web/HTML/Element/Input/file");
    }

    public void jstest(View view) {
        BaseWebviewActivity.start(this,"https://test-ec-mall.xxx.com/jstest/input_test.html");
    }
    public void jsInputFile3(View view) {
        BaseWebviewActivity.start(this,"https://web.dev/read-files/");
    }

    public void jsPop(View view) {
        BaseWebviewActivity.start(this,"https://www.runoob.com/js/js-popup.html");
    }

    public void playVideo(View view) {
        BaseWebviewActivity.start(this,"https://www.runoob.com/tags/tag-video.html");
    }
    public void getUserMedia(View view) {
        //https://developer.mozilla.org/zh-CN/docs/Web/API/MediaDevices/getUserMedia
        BaseWebviewActivity.start(this,"https://static.hss01248.tech/webrtc.html");
    }

    public void windowHistory(View view) {
        BaseWebviewActivity.start(this,"https://www.runoob.com/js/js-window-history.html");
    }
    public void windowLocation(View view) {
        BaseWebviewActivity.start(this,"https://www.runoob.com/js/js-window-location.html");
    }

    public void windowOpen(View view) {
        //BaseWebviewActivity.start(this,"https://www.w3schools.com/jsref/met_win_close.asp");
        BaseWebviewActivity.start(this,"https://test-ec-mall.xxx.com/jstest/index.html");
    }

    public void recordAudio(View view) {
        //https://juejin.cn/post/6844903621599952909
        BaseWebviewActivity.start(this,"https://xiangyuecn.github.io/Recorder/");
    }

    public void jsInputFileCapture(View view) {
        BaseWebviewActivity.start(this,"https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/capture");
    }

    public void toTest(View view) {
        BaseWebviewActivity.start(this,"https://navi.hss01248.tech/tabShare/12");
    }

    public void sentry_msg(View view) {
        //SentryUtil.testMsg("msg test");
    }

    public void sentry_ex(View view) {
        //SentryUtil.testException();
    }
}