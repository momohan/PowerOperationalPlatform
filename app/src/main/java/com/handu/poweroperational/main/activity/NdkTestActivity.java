package com.handu.poweroperational.main.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NdkTestActivity extends BaseActivity {

    @Bind(R.id.intbutton)
    Button intbutton;
    @Bind(R.id.inttextview)
    TextView inttextview;
    @Bind(R.id.stringbutton)
    Button stringbutton;
    @Bind(R.id.stringtextview)
    TextView stringtextview;
    @Bind(R.id.arraybutton)
    Button arraybutton;
    @Bind(R.id.arraytextview)
    TextView arraytextview;
    private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndk_test);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //整型
                    case 0: {
                        inttextview.setText(msg.obj.toString());
                        break;
                    }
                    //字符串
                    case 1: {
                        stringtextview.setText(msg.obj.toString());
                        break;
                    }
                    //数组
                    case 2: {
                        byte[] b = (byte[]) msg.obj;
                        arraytextview.setText(Byte.toString(b[0]) + Byte.toString(b[1]) + Byte.toString(b[2]) + Byte.toString(b[3]) + Byte.toString(b[4]));
                        break;
                    }
                }

            }

        };
    }

    @Override
    protected void initData() {
    }


    //本地方法，由java调用
    private native void callJNIInt(int i);

    private native void callJNIString(String s);

    private native void callJNIByte(byte[] b);


    static {
        System.loadLibrary("demo");
    }

    @OnClick({R.id.intbutton, R.id.stringbutton, R.id.arraybutton, R.id.bt_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.intbutton:
                callJNIInt(1);
                break;
            case R.id.stringbutton:
                callJNIString("中文测试");
                break;
            case R.id.arraybutton:
                callJNIByte(new byte[]{1, 2, 3, 4, 5});
                break;
            case R.id.bt_clear:
                inttextview.setText("");
                stringtextview.setText("");
                arraytextview.setText("");
                break;
        }
    }

    //被JNI调用，参数由JNI传入
    private void callbackByte(byte[] b) {
        Message msg = new Message();
        //消息类型
        msg.what = 2;
        //消息内容
        msg.obj = b;
        //发送消息
        mHandler.sendMessage(msg);
    }

    //被JNI调用，参数由JNI传入
    private void callbackString(String s) {
        Message msg = new Message();
        //消息类型
        msg.what = 1;
        //消息内容
        msg.obj = s;
        //发送消息
        mHandler.sendMessage(msg);
    }

    //被JNI调用，参数由JNI传入
    private void callbackInt(int i) {
        Message msg = new Message();
        //消息类型
        msg.what = 0;
        //消息内容
        msg.obj = i;
        //发送消息
        mHandler.sendMessage(msg);
    }
}
