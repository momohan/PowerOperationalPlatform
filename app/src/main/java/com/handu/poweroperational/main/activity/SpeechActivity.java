package com.handu.poweroperational.main.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.utils.speech.FucUtil;
import com.handu.poweroperational.utils.speech.JsonParser;
import com.handu.poweroperational.utils.speech.SpeechApkInstaller;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.Tools;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpeechActivity extends BaseActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    public static final int REQUEST_CODE = 121;
    public static final String REQUEST_RESULT = "result";
    public static final String IS_NEED_PUNCTUATION = "punctuation";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iat_text)
    EditText mResultText;
    @Bind(R.id.rg_engineType)
    RadioGroup rgEngineType;
    @Bind(R.id.iat_confirm)
    ImageButton btConfirm;

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    // 引擎类型(默认云端，需要网络)
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    //语音合成对象
    private SpeechSynthesizer mTts;
    // 语记安装助手类
    private SpeechApkInstaller mInstaller;
    //默认需要标点符号
    private String punctuation = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(this);
        FlowerCollector.onPageStart(AppConstant.TAG);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(AppConstant.TAG);
        FlowerCollector.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
    }

    /**
     * @param activity    对象
     * @param punctuation 是否需要标点
     */
    public static void runSpeechActivityForResult(Activity activity, boolean punctuation) {
        Intent intent = new Intent(activity, SpeechActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_NEED_PUNCTUATION, punctuation);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * @param activity 对象
     *                 默认需要标点
     */
    public static void runSpeechActivityForResult(Activity activity) {
        Intent intent = new Intent(activity, SpeechActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_NEED_PUNCTUATION, true);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    //获取手机录音机使用权限
    private void requestAudioPermission() {
        //位置信息权限
        AndPermission.with(SpeechActivity.this)
                .requestCode(REQUEST_RECORD_AUDIO_PERMISSION)
                .permission(
                        Manifest.permission.RECORD_AUDIO
                ).send();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, new PermissionListener() {
            @Override
            public void onSucceed(int requestCode) {
                if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
                    // 设置参数
                    setParam();
                    // 移动数据分析，收集开始听写事件
                    FlowerCollector.onEvent(SpeechActivity.this, "iat_recognize");
                    // 清空显示内容
                    mIatResults.clear();
                    // 显示听写对话框
                    mIatDialog.setListener(mRecognizerDialogListener);
                    mIatDialog.show();
                }
            }

            @Override
            public void onFailed(int requestCode) {
                if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
                    showSnackbar(toolbar, "录音权限申请被禁止，语音录入功能未启动!", "关闭", v -> {
                    }, false);
                }
            }
        });
    }

    @Override
    protected void initView() {
        mInstaller = new SpeechApkInstaller(SpeechActivity.this);
        initToolBar(toolbar, getString(R.string.title_speech), true, v -> finish(), true, R.menu.menu_speech, item -> {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    break;
            }
            return false;
        });
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        //创建 SpeechSynthesizer对象,
        // 第二个参数：本地合成时传
        mTts = SpeechSynthesizer.createSynthesizer(this, mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(this, mInitListener);
        rgEngineType.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < rgEngineType.getChildCount(); i++) {
                View view = rgEngineType.getChildAt(i);
                if (view instanceof RadioButton) {
                    RadioButton bt = (RadioButton) view;
                    if (bt.getId() != checkedId)
                        bt.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    else
                        bt.setTextColor(ContextCompat.getColor(mContext, R.color.red_light));
                }
            }
            switch (checkedId) {
                case R.id.rb_online:
                    mEngineType = SpeechConstant.TYPE_CLOUD;
                    break;
                case R.id.rb_local:
                    mEngineType = SpeechConstant.TYPE_LOCAL;
                    /**
                     * 选择本地听写 判断是否安装语记,未安装则跳转到提示安装页面
                     */
                    if (!SpeechUtility.getUtility().checkServiceInstalled()) {
                        mInstaller.install();
                    } else {
                        String result = FucUtil.checkLocalResource();
                        if (!TextUtils.isEmpty(result)) {
                            Tools.toastInfo(result);
                        }
                    }
                    break;
            }
        });
        mResultText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    btConfirm.setVisibility(View.GONE);
                } else {
                    btConfirm.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = this.getIntent();
        boolean isPunctuation = intent.getBooleanExtra(IS_NEED_PUNCTUATION, true);
        if (isPunctuation) {
            punctuation = "1";
        } else {
            punctuation = "0";
        }
        requestAudioPermission();
    }

    @OnClick({R.id.iat_recognize, R.id.iat_start_synthesizer, R.id.iat_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iat_recognize:
                // 设置参数
                setParam();
                // 移动数据分析，收集开始听写事件
                FlowerCollector.onEvent(this, "iat_recognize");
                // 清空显示内容
                mIatResults.clear();
                // 显示听写对话框
                mIatDialog.setListener(mRecognizerDialogListener);
                mIatDialog.show();
                break;
            case R.id.iat_start_synthesizer:
                // 设置参数
                setParam();
                String result = mResultText.getText().toString();
                if (mTts != null && !TextUtils.isEmpty(result)) {
                    if (mTts.isSpeaking()) mTts.stopSpeaking();
                    //开始合成
                    mTts.startSpeaking(result, new SynthesizerListener() {
                        @Override
                        public void onSpeakBegin() {

                        }

                        @Override
                        public void onBufferProgress(int i, int i1, int i2, String s) {

                        }

                        @Override
                        public void onSpeakPaused() {

                        }

                        @Override
                        public void onSpeakResumed() {

                        }

                        @Override
                        public void onSpeakProgress(int i, int i1, int i2) {

                        }

                        @Override
                        public void onCompleted(SpeechError speechError) {

                        }

                        @Override
                        public void onEvent(int i, int i1, int i2, Bundle bundle) {

                        }
                    });
                } else {
                    Tools.toastInfo("您还没有输入任何内容呢...");
                }
                break;
            case R.id.iat_confirm:
                String str = mResultText.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(REQUEST_RESULT, str);
                    gotoActivitySetResult(bundle, REQUEST_CODE);
                }
                finish();
                break;
        }
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {

        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results, isLast);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
        }

    };

    private void printResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        if (isLast) {
            StringBuilder resultBuffer = new StringBuilder();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }
            mResultText.append(resultBuffer.toString());
            mResultText.setSelection(mResultText.length());
            if (!TextUtils.isEmpty(mResultText.getText().toString())) {
                btConfirm.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = code -> {
        if (code != ErrorCode.SUCCESS) {
            Tools.toastError("讯飞语音初始化失败...");
            AppLogger.e("SpeechRecognizer init() code = " + code);
        }
    };

    /**
     * 参数设置
     */
    public void setParam() {
        // 听写参数设置
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "zh_cn");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "5000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, punctuation);
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        // mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/HanDu_App/iat.wav");

        // 合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        // 设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "vixq");
        // 设置语速
        mTts.setParameter(SpeechConstant.SPEED, "40");
        // 设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.VOLUME, "100");
    }
}
