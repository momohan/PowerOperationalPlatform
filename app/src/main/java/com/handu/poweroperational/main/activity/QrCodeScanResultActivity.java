package com.handu.poweroperational.main.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QrCodeScanResultActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_result)
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scan_result);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(toolbar, "扫描结果", true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        String result = getIntent().getStringExtra(QRCodeScanActivity.SCAN_RESULT);
        if (!TextUtils.isEmpty(result)) {
            tvResult.setText(result);
        }
    }
}
