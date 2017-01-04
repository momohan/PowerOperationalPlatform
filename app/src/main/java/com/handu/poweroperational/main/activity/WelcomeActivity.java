package com.handu.poweroperational.main.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.utils.Tools;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.tv_version)
    TextView tvVersion;
    private MsgHandler msgHandler = new MsgHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        String versionName = Tools.getVersionName(mContext);
        tvVersion.append(versionName);
        msgHandler.sendEmptyMessageDelayed(1, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static class MsgHandler extends Handler {

        private WeakReference<Context> weakReference;

        public MsgHandler(Context context) {
            weakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            WelcomeActivity activity = (WelcomeActivity) weakReference.get();
            switch (msg.what) {
                case 1:
                    activity.gotoActivity(LoginActivity.class, true);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        msgHandler.removeCallbacksAndMessages(null);//移除回调
    }
}
