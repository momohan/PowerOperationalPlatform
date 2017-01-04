package com.handu.poweroperational.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.widget.base.TopBaseDialog;
import com.handu.poweroperational.R;
import com.handu.poweroperational.utils.Tools;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 柳梦 on 2016/12/15.
 */

public class MainTopMenuDialog extends TopBaseDialog<MainTopMenuDialog> implements View.OnClickListener {

    @Bind(R.id.ll_wechat_friend_circle)
    LinearLayout llWechatFriendCircle;
    @Bind(R.id.ll_wechat_friend)
    LinearLayout llWechatFriend;
    @Bind(R.id.ll_sms)
    LinearLayout llSms;
    @Bind(R.id.ll_qq)
    LinearLayout llQq;

    public MainTopMenuDialog(Context context, View animateView) {
        super(context, animateView);
    }

    public MainTopMenuDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        showAnim(new FlipVerticalSwingEnter());
        dismissAnim(null);
        View inflate = View.inflate(mContext, R.layout.dialog_main_top_menu, null);
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        llQq.setOnClickListener(this);
        llWechatFriendCircle.setOnClickListener(this);
        llWechatFriend.setOnClickListener(this);
        llSms.setOnClickListener(this);
    }


    @OnClick({R.id.ll_wechat_friend_circle, R.id.ll_wechat_friend, R.id.ll_sms, R.id.ll_qq})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_wechat_friend_circle:
                Tools.showToast("运维");
                break;
            case R.id.ll_wechat_friend:
                Tools.showToast("商城");
                break;
            case R.id.ll_sms:
                Tools.showToast("楼宇");
                break;
            case R.id.ll_qq:
                Tools.showToast("物资");
                break;
        }
        dismiss();
    }
}
