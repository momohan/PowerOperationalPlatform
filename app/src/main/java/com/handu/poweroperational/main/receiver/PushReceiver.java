package com.handu.poweroperational.main.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.utils.AppLogger;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class PushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                //String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                AppLogger.e("第三方回执接口调用：" + (result ? "成功" : "失败"));
                if (payload != null) {
                    String data = new String(payload);
                    AppLogger.e("receiver payload : " + data);
                    setNotification(data);
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                if (!TextUtils.isEmpty(cid)) {
                    AppLogger.e(cid);
                    PowerOperationalApplicationLike.pushCid = cid;
                    EventBus.getDefault().post(new BaseEvent(EventType.registerCid.getType(), cid));
                }
                break;
            case PushConsts.GET_SDKONLINESTATE:
                boolean online = bundle.getBoolean("onlineState");
                AppLogger.e("online = " + online);
                break;

            case PushConsts.SET_TAG_RESULT:
                String sn = bundle.getString("sn");
                String code = bundle.getString("code");
                String text = "设置标签失败, 未知异常";
                switch (Integer.valueOf(code)) {
                    case PushConsts.SETTAG_SUCCESS:
                        text = "设置标签成功";
                        break;
                    case PushConsts.SETTAG_ERROR_COUNT:
                        text = "设置标签失败, tag数量过大, 最大不能超过200个";
                        break;
                    case PushConsts.SETTAG_ERROR_FREQUENCY:
                        text = "设置标签失败, 频率过快, 两次间隔应大于1s";
                        break;
                    case PushConsts.SETTAG_ERROR_REPEAT:
                        text = "设置标签失败, 标签重复";
                        break;
                    case PushConsts.SETTAG_ERROR_UNBIND:
                        text = "设置标签失败, 服务未初始化成功";
                        break;
                    case PushConsts.SETTAG_ERROR_EXCEPTION:
                        text = "设置标签失败, 未知异常";
                        break;
                    case PushConsts.SETTAG_ERROR_NULL:
                        text = "设置标签失败, tag 为空";
                        break;
                    case PushConsts.SETTAG_NOTONLINE:
                        text = "还未登陆成功";
                        break;
                    case PushConsts.SETTAG_IN_BLACKLIST:
                        text = "该应用已经在黑名单中,请联系售后支持!";
                        break;
                    case PushConsts.SETTAG_NUM_EXCEED:
                        text = "已存 tag 超过限制";
                        break;
                    default:
                        break;
                }
                AppLogger.e("settag result sn = " + sn + ", code = " + code);
                AppLogger.e("settag result sn = " + text);
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                break;
            default:
                break;
        }
    }

    /**
     * 开启自定义通知栏
     *
     * @param data
     */
    private void setNotification(String data) {
        try {
            JSONObject object = new JSONObject(data);
            CharSequence WarningTitle = object.optString("title", "温馨提示");
            String content = "";
            int type = object.optInt("msgtype", 0);
            switch (type) {
                case 0:
                    content = object.optString("content", "您有新的工单，请及时处理");
                    EventBus.getDefault().post(new BaseEvent(EventType.newWorkOrderUpdate.getType()));
                    break;
                case 1:
                    content = object.optString("content", "您有新的设备告警，请及时处理");
                    EventBus.getDefault().post(new BaseEvent(EventType.alarmNumUpdate.getType()));
                    break;
            }
            NotificationManager mNotificationManager = (NotificationManager) PowerOperationalApplicationLike.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(PowerOperationalApplicationLike.getContext());
            int icon = R.mipmap.ic_app_icon;
            long when = System.currentTimeMillis();
            //设置展开后的通知类容
            //NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            //bigTextStyle.setBigContentTitle("工单号：201332021312312321");
            //bigTextStyle.bigText("工单内容：成都市高新区锦城大道666号奥克斯广场故障，请紧急抢修");
            //mBuilder.setStyle(bigTextStyle);
            mBuilder.setContentTitle(WarningTitle)//设置通知栏标题
                    .setContentText(content) //设置通知栏显示内容
                    .setTicker(WarningTitle) //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(when)//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知自动取消
                    .setOngoing(false)//true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,
                    .setNumber(1); //设置通知集合的数量
            // 用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            //设置消息优先级
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
            //向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置,
            //使用defaults属性，可以组合
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
            //设置震动效果requires VIBRATE permission
            //mBuilder.setVibrate(new long[]{0, 1000});
            mBuilder.setSmallIcon(icon)//设置通知小ICON
                    .setLights(0xff0000ff, 300, 0);
            //获取自定义铃声
            //mBuilder.setSound(Uri.parse("file:///sdcard/xx/xx.mp3"));
            //获取Android多媒体库内的铃声
            //mBuilder.setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "5"));
            //获取raw下的音乐文件
            //mBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + NotificationConstants.soundNewName));
            Notification notification = mBuilder.build();
            mNotificationManager.notify((int) (Math.random() * 10000 + (int) (Math.random() * 10000)), notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
