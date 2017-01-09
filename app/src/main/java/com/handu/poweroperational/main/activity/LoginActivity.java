package com.handu.poweroperational.main.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.johnkil.print.PrintButton;
import com.github.johnkil.print.PrintView;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.request.callback.JsonDialogCallback;
import com.handu.poweroperational.db.DBConstants;
import com.handu.poweroperational.db.model.User;
import com.handu.poweroperational.main.bean.results.LoginResult;
import com.handu.poweroperational.request.RequestServer;
import com.handu.poweroperational.utils.AESUtils;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.PreferencesUtils;
import com.handu.poweroperational.utils.ServiceUrl;
import com.handu.poweroperational.utils.Tools;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.BaseRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.ibt_select_user)
    PrintView ibtSelectUser;
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private PopupWindow popView;
    private String username;
    private String password;
    private MsgHandler msgHandler = new MsgHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        initData();
        initLoginUserName();
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(this);
        msgHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void initView() {
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                attemptLogin();
                return true;
            }
        });
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        ibtSelectUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popView != null) {
                    if (!popView.isShowing()) {
                        popView.showAsDropDown(mUserNameView);
                    } else {
                        popView.dismiss();
                    }
                } else {
                    // 如果有已经登录过账号
                    CursorList<User> users = Query.all(User.class).get();
                    if (users.size() > 0) {
                        initPopView(users);
                        if (!popView.isShowing()) {
                            popView.showAsDropDown(mUserNameView);
                        } else {
                            popView.dismiss();
                        }
                    } else {
                        Tools.showToast(getString(R.string.no_user_info));
                    }
                }
            }
        });
        mUserNameView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mPasswordView.setText("");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initLoginUserName() {
        String lastUserName = PreferencesUtils.get(mContext, AppConstant.username, "") + "";
        String lastPassWord = PreferencesUtils.get(mContext, AppConstant.password, "") + "";
        if (!TextUtils.isEmpty(lastUserName) && !TextUtils.isEmpty(lastPassWord)) {
            mUserNameView.setText(lastUserName);
            mUserNameView.setSelection(lastUserName.length());
            mPasswordView.setText(lastPassWord);
            attemptLogin();
        } else {
            CursorList<User> users = Query.all(User.class).get();
            if (users.size() > 0) {
                String tempName = users.get(users.size() - 1).username;
                String tempPwd = users.get(users.size() - 1).password;
                if (!TextUtils.isEmpty(tempName) && !TextUtils.isEmpty(tempPwd)) {
                    mUserNameView.setText(tempName);
                    mUserNameView.setSelection(tempName.length());
                    mPasswordView.setText(tempPwd);
                }
            }
        }
    }

    private void initPopView(CursorList<User> users) {

        List<HashMap<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("text", users.get(i).username);
            map.put("drawable", R.mipmap.ic_launcher);
            list.add(map);
        }
        MyAdapter dropDownAdapter = new MyAdapter(this, list, R.layout.dropdown_login_user_item,
                new String[]{"text", "drawable"}, new int[]{R.id.tv_user, R.id.ibt_delete});
        ListView listView = new ListView(this);
        listView.setDivider(ContextCompat.getDrawable(mContext, R.color.black));
        listView.setAdapter(dropDownAdapter);
        popView = new PopupWindow(listView, mUserNameView.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popView.setFocusable(true);
        popView.setOutsideTouchable(true);
    }

    @Override
    protected void initData() {
    }

    private void attemptLogin() {

        mUserNameView.setError(null);
        mPasswordView.setError(null);
        username = mUserNameView.getText().toString();
        password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
//            login(username, password);
            onlineLogin(username, password);
        }
    }

    private void onlineLogin(String username, String password) {
        PreferencesUtils.put(mContext, username, username);
        PreferencesUtils.put(mContext, AppConstant.realName, username);
        PreferencesUtils.put(mContext, password, password);
        User user = Query.one(User.class, "select " + User.ID + " from " + DBConstants.USER_TABLE_NAME + " where " + User.USERNAME + " =? ", username).get();
        if (user != null) user.delete();//先删除
        user = new User();
        user.username = username;
        user.password = password;
        user.save();//再保存
        gotoActivity(MainActivity.class, true);
    }

    private void login(final String username, final String password) {

        Map<String, String> map = new HashMap<>();
        map.put("RealName", username);
        map.put("Password", AESUtils.MD5(password));
        RequestServer.post(this, ServiceUrl.Login, map, new JsonDialogCallback<LoginResult>(this, LoginResult.class) {

            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
            }

            @Override
            public void onSuccess(LoginResult loginResult, Call call, Response response) {
                Message message = new Message();
                message.what = 0;
                message.obj = loginResult;
                msgHandler.sendMessage(message);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Message message = new Message();
                message.what = -1;
                message.obj = e;
                msgHandler.sendMessage(message);
            }
        });
    }

    private class MyAdapter extends SimpleAdapter {

        private List<HashMap<String, Object>> data;

        public MyAdapter(Context context, List<HashMap<String, Object>> data,
                         int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.dropdown_login_user_item, null);
                holder.btn = (PrintButton) convertView
                        .findViewById(R.id.ibt_delete);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_user);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(data.get(position).get("text").toString());
            holder.tv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    CursorList<User> users = Query.all(User.class).get();
                    mUserNameView.setText(users.get(position).username);
                    mPasswordView.setText(users.get(position).password);
                    popView.dismiss();
                }
            });
            holder.btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mUserNameView.setText("");
                    mPasswordView.setText("");
                    CursorList<User> users = Query.all(User.class).get();
                    if (users.size() > 0) {
                        User user = users.get(position);
                        user.delete();
                    }
                    users = Query.all(User.class).get();
                    if (users.size() > 0) {
                        popView.dismiss();
                        initPopView(users);
                        popView.showAsDropDown(mUserNameView);
                    } else {
                        popView.dismiss();
                        popView = null;
                    }
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        private TextView tv;
        private PrintButton btn;
    }

    static class MsgHandler extends Handler {

        private WeakReference reference;

        MsgHandler(Activity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            LoginActivity activity = (LoginActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        LoginResult loginResult = (LoginResult) msg.obj;
                        if (loginResult != null) {
                            PreferencesUtils.put(activity.mContext, AppConstant.userId, loginResult.getUserId());
                            PreferencesUtils.put(activity.mContext, AppConstant.realName, loginResult.getRealName());
                            PreferencesUtils.put(activity.mContext, AppConstant.username, activity.username);
                            PreferencesUtils.put(activity.mContext, AppConstant.password, activity.password);
                            PreferencesUtils.put(activity.mContext, AppConstant.organizeId, loginResult.getOrganizeId());
                            PreferencesUtils.put(activity.mContext, AppConstant.departmentId, loginResult.getDepartmentId());
                            User user = Query.one(User.class, "select " + User.ID + " from " + DBConstants.USER_TABLE_NAME + " where " + User.USERNAME + " =? ", activity.username).get();
                            if (user != null) user.delete();//先删除
                            user = new User();
                            user.username = activity.username;
                            user.password = activity.password;
                            user.save();//再保存
                            activity.gotoActivity(MainActivity.class, true);
                        } else {
                            Tools.showToast(activity.getString(R.string.login_failure));
                        }
                        break;

                    case -1:
                        Exception e = (Exception) msg.obj;
                        AppLogger.e(e.getMessage());
                        Tools.showToast(e.getMessage());
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
}
