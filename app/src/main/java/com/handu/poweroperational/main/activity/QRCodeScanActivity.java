package com.handu.poweroperational.main.activity;

import android.Manifest;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.utils.AppLogger;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QRCodeScanActivity extends BaseActivity implements QRCodeView.Delegate {

    public static final int REQUEST_CODE = 100;
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    public static final String SCAN_RESULT = "result";
    public static final String SCAN_RESULT_ID = "id";
    public static final String SCAN_RESULT_TEXT = "text";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.zxingview)
    ZXingView mQRCodeView;
    private MediaPlayer mediaPlayer;
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);
        ButterKnife.bind(this);
        initView();
        initData();
        initSound();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQrcodePermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mQRCodeView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void initView() {
        initToolBar(toolbar, "二维码扫描", true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void initData() {
    }

    //申请扫描二维码权限
    private void requestCodeQrcodePermission() {
        //扫描二维码权限
        AndPermission.with(QRCodeScanActivity.this)
                .requestCode(REQUEST_CODE_QRCODE_PERMISSIONS)
                .permission(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).send();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, new PermissionListener() {
            @Override
            public void onSucceed(int requestCode) {
                if (requestCode == REQUEST_CODE_QRCODE_PERMISSIONS) {
                    mQRCodeView.startCamera();
                    mQRCodeView.startSpot();
                }
            }

            @Override
            public void onFailed(int requestCode) {
                if (requestCode == REQUEST_CODE_QRCODE_PERMISSIONS) {
                    showSnackbar(toolbar, "扫描二维码权限被禁止，扫描功能将受影响!", "关闭", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, false);
                }
            }
        });
    }

    //震动提示
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATE_DURATION);
    }

    //声音提示
    private void initSound() {
        if (mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.seekTo(0);
                }
            });
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        if (TextUtils.isEmpty(result)) {
            mQRCodeView.startSpotAndShowRect();
        } else {
            vibrate();
            mediaPlayer.start();
            mQRCodeView.stopSpotAndHiddenRect();
            mQRCodeView.stopCamera();
            Bundle bundle = new Bundle();
            bundle.putString(SCAN_RESULT, result);
            gotoActivitySetResult(bundle, REQUEST_CODE);
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        AppLogger.e("打开相机出错");
    }

    @OnClick({R.id.start_spot, R.id.stop_spot, R.id.open_flashlight, R.id.close_flashlight,
            R.id.scan_barcode, R.id.scan_qrcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_spot:
                mQRCodeView.startSpotAndShowRect();
                break;
            case R.id.stop_spot:
                mQRCodeView.stopSpotAndHiddenRect();
                mQRCodeView.stopCamera();
                break;
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
            case R.id.scan_barcode:
                mQRCodeView.changeToScanBarcodeStyle();
                mQRCodeView.startSpotAndShowRect();
                break;
            case R.id.scan_qrcode:
                mQRCodeView.changeToScanQRCodeStyle();
                mQRCodeView.startSpotAndShowRect();
                break;
        }
    }
}
