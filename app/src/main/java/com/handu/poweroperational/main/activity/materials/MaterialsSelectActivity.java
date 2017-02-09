package com.handu.poweroperational.main.activity.materials;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.main.activity.QRCodeScanActivity;
import com.handu.poweroperational.main.activity.QRCodeScanResultActivity;
import com.handu.poweroperational.main.adapter.materials.MaterialsSelectAdapter;
import com.handu.poweroperational.main.application.PowerOperationalApplication;
import com.handu.poweroperational.main.bean.MaterialsModel;
import com.handu.poweroperational.main.bean.results.WorkOrderResult;
import com.handu.poweroperational.request.RequestServer;
import com.handu.poweroperational.request.callback.StringDialogCallback;
import com.handu.poweroperational.ui.AndroidTree.TreeNodeUtils;
import com.handu.poweroperational.ui.AndroidTree.holder.IconTreeItemHolder;
import com.handu.poweroperational.utils.AnimationUtil;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.ServiceUrl;
import com.handu.poweroperational.utils.Tools;
import com.lzy.okhttputils.request.BaseRequest;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import ezy.ui.layout.LoadingLayout;
import okhttp3.Call;
import okhttp3.Response;

public class MaterialsSelectActivity extends BaseActivity {

    public static final int REQUEST_CODE = 100;
    public static final String MATERIALS_RESULT = "result";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.work_order_qr_code)
    ImageView workOrderQrCode;
    @Bind(R.id.tv_work_order_num)
    TextView tvWorkOrderNum;
    @Bind(R.id.qr_scan)
    ImageView qrScan;
    @Bind(R.id.ll_container)
    LinearLayout llContainer;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.tv_tips)
    TextView tvTips;
    @Bind(R.id.tv_submit)
    TextView tvSubmit;
    @Bind(R.id.loading_view)
    LoadingLayout loadingView;
    private WorkOrderResult workOrderResult;
    @Bind(R.id.bottomSheetLayout)
    BottomSheetLayout bottomSheetLayout;
    private RecyclerView rvSelected;
    private MaterialsSelectAdapter materialsSelectAdapter;
    private View bottomSheet;
    private ArrayMap<String, MaterialsModel> selectedList;
    private CreateQRCodeTask task;
    private Bitmap QRCodeBitmap;
    private MsgHandler handler = new MsgHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materials_select);
        ButterKnife.bind(this);
        initView();
        initData();
        getMaterials();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        task.cancel(true);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void initView() {
        initToolBar(toolbar, getString(R.string.title_tools_select), true, v -> finish());
        initLoadingView();
    }

    private void initLoadingView() {
        loadingView.showLoading();
        loadingView.setErrorText(getString(R.string.action_retry));
        loadingView.setRetryListener(v -> getMaterials());
    }

    @Override
    protected void initData() {
        workOrderResult = getIntent().getParcelableExtra("workOrderResult");
        tvWorkOrderNum.setText("工单编号：" + workOrderResult.getNumber());
        selectedList = new ArrayMap<>();
        task = new CreateQRCodeTask(this);
        task.execute(workOrderResult);
    }

    @OnClick({R.id.qr_scan, R.id.tv_submit, R.id.ll_bottom, R.id.work_order_qr_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qr_scan:
                gotoActivityForResult(QRCodeScanActivity.class, null, QRCodeScanActivity.REQUEST_CODE);
                break;
            case R.id.tv_submit:
                submit();
                break;
            case R.id.ll_bottom:
                showBottomSheet();
                break;
            case R.id.work_order_qr_code:
                if (QRCodeBitmap != null)
                    showBigQrCode(QRCodeBitmap);
                break;
        }
    }

    //提交所选物资
    private void submit() {
        List<MaterialsModel> models = new ArrayList<>();
        for (int i = 0; i < selectedList.size(); i++) {
            MaterialsModel model = selectedList.valueAt(i);
            models.add(model);
        }
        Gson gson = new Gson();
        String result = gson.toJson(models);
        AppLogger.e(result);
        Bundle bundle = new Bundle();
        bundle.putString(MATERIALS_RESULT, result);
        gotoActivitySetResult(bundle, REQUEST_CODE);
    }

    //显示大的二维码
    private void showBigQrCode(Bitmap bitmap) {
        ImageView imageView = new ImageView(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        lp.setMargins(0, 0, 0, 60);
        imageView.setLayoutParams(lp);
        imageView.setImageBitmap(bitmap);
        AlertView alertView = new AlertView("工单二维码", null, getResources().getString(R.string.action_close), null, null, mContext, AlertView.Style.Alert, null);
        alertView.addExtView(imageView);
        alertView.show();
    }

    //创建底部弹出窗
    private View createBottomSheetView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet, (ViewGroup) getWindow().getDecorView(), false);
        rvSelected = (RecyclerView) view.findViewById(R.id.selectRecyclerView);
        rvSelected.setLayoutManager(new LinearLayoutManager(this));
        TextView clear = (TextView) view.findViewById(R.id.clear);
        clear.setOnClickListener(v -> clearCart());
        materialsSelectAdapter = new MaterialsSelectAdapter(this, selectedList);
        rvSelected.setAdapter(materialsSelectAdapter);
        return view;
    }

    //显示底部弹出窗
    private void showBottomSheet() {
        if (bottomSheet == null) {
            bottomSheet = createBottomSheetView();
        }
        if (bottomSheetLayout.isSheetShowing()) {
            bottomSheetLayout.dismissSheet();
        } else {
            if (selectedList.size() != 0) {
                bottomSheetLayout.showWithSheetView(bottomSheet);
            }
        }
    }

    //添加物资
    public void add(MaterialsModel item) {
        MaterialsModel temp = selectedList.get(item.id);
        if (temp == null) {
            item.count = 1;
            selectedList.put(item.id, item);
        } else {
            temp.count++;
        }
        update();
    }

    //移除物资
    public void remove(MaterialsModel item) {
        MaterialsModel temp = selectedList.get(item.id);
        if (temp != null) {
            if (temp.count < 2) {
                selectedList.remove(item.id);
            } else {
                item.count--;
            }
        }
        update();
    }

    //刷新布局、选择数量等
    private void update() {

        int size = selectedList.size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            MaterialsModel item = selectedList.valueAt(i);
            count += item.count;
        }
        tvCount.setText(String.valueOf(count));
        if (count < 1) {
            if (tvCount.getVisibility() == View.VISIBLE) {
                tvCount.setAnimation(AnimationUtil.getHiddenAnimation());
                tvCount.setVisibility(View.GONE);
            }
        } else {
            if (tvCount.getVisibility() == View.GONE) {
                tvCount.setAnimation(AnimationUtil.getShowAnimation());
                tvCount.setVisibility(View.VISIBLE);
            }
        }
        if (count > 0) {
            if (tvSubmit.getVisibility() == View.GONE) {
                tvSubmit.setAnimation(AnimationUtil.getShowAnimation());
                tvSubmit.setVisibility(View.VISIBLE);
            }
        } else {
            if (tvSubmit.getVisibility() == View.VISIBLE) {
                tvSubmit.setAnimation(AnimationUtil.getHiddenAnimation());
                tvSubmit.setVisibility(View.GONE);
            }
        }
        tvTips.setText("已选工器具" + count + "件");
        if (materialsSelectAdapter != null) {
            materialsSelectAdapter.notifyDataSetChanged();
        }
        if (bottomSheetLayout.isSheetShowing() && selectedList.size() < 1) {
            bottomSheetLayout.dismissSheet();
        }
    }

    //清空所有已选物资
    public void clearCart() {
        selectedList.clear();
        update();
    }

    //弱引用避免内存泄漏
    static class CreateQRCodeTask extends AsyncTask<WorkOrderResult, Void, Bitmap> {

        private WeakReference<Activity> reference;

        CreateQRCodeTask(Activity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        protected Bitmap doInBackground(WorkOrderResult... params) {
            WorkOrderResult result = params[0];
            return QRCodeEncoder.syncEncodeQRCode(result.getNumber(), BGAQRCodeUtil.dp2px(PowerOperationalApplication.getContext(), 150), Color.parseColor("#050505"));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            MaterialsSelectActivity activity = (MaterialsSelectActivity) reference.get();
            if (activity != null) {
                if (bitmap != null) {
                    activity.QRCodeBitmap = bitmap;
                    activity.workOrderQrCode.setImageBitmap(bitmap);
                } else {
                    Tools.showToast(activity.getString(R.string.create_work_order_qrcode_failure));
                }
            }
        }
    }

    private void getMaterials() {
        if (!Tools.isNetworkAvailable()) {
            loadingView.setErrorText(getString(R.string.network_not_available));
            loadingView.showError();
            return;
        }
        loadingView.showLoading();
        RequestServer.post(this, ServiceUrl.GetM_CategoryTree, "", new StringDialogCallback(this) {

            @Override
            public void onBefore(BaseRequest request) {
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                AppLogger.e(s);
                Message message = new Message();
                message.what = 1;
                message.obj = s;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Message message = new Message();
                message.what = -1;
                message.obj = e;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QRCodeScanActivity.REQUEST_CODE) {
            if (data != null) {
                String result = data.getStringExtra(QRCodeScanActivity.SCAN_RESULT);
                try {
                    JSONObject o = new JSONObject(result);
                    String id = o.getString(QRCodeScanActivity.SCAN_RESULT_ID);
                    String name = o.getString(QRCodeScanActivity.SCAN_RESULT_TEXT);
                    MaterialsModel model = new MaterialsModel(id, name);
                    add(model);
                } catch (JSONException e) {
                    e.printStackTrace();
                    gotoActivity(QRCodeScanResultActivity.class, data.getExtras(), false);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private static class MsgHandler extends Handler {

        private WeakReference<Activity> reference;

        MsgHandler(Activity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MaterialsSelectActivity activity = (MaterialsSelectActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case -1:
                        Exception exception = (Exception) msg.obj;
                        activity.loadingView.setErrorText(exception.getMessage());
                        activity.loadingView.showError();
                        break;
                    case 1:
                        String s = (String) msg.obj;
                        try {
                            JSONObject o = new JSONObject(s);
                            boolean success = o.getBoolean("success");
                            String msg_ = o.getString("msg");
                            if (success) {
                                String data = o.getString("data");
                                TreeNode root = TreeNode.root();
                                JSONArray array = new JSONArray(data);
                                root = TreeNodeUtils.getTreeNode(activity, array, root, R.string.ic_post_instagram, "text", "id", "ChildNodes", new String[]{"Sort"});
                                AndroidTreeView tView = new AndroidTreeView(activity, root);
                                tView.setDefaultAnimation(true);
                                tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
                                tView.setUse2dScroll(true);
                                tView.setDefaultNodeClickListener((node, value) -> {
                                    if (node.size() == 0) {
                                        IconTreeItemHolder.IconTreeItem iconTreeItem = (IconTreeItemHolder.IconTreeItem) value;
                                        if (iconTreeItem.others.get("Sort") != null
                                                && iconTreeItem.others.get("Sort").toString().equals("detail")) {
                                            MaterialsModel model = new MaterialsModel(iconTreeItem.value, iconTreeItem.text);
                                            activity.add(model);
                                        } else {
                                            Tools.showToast("请选择正确的工器具");
                                        }
                                    }
                                });
                                activity.llContainer.addView(tView.getView());
                                activity.loadingView.showContent();
                            } else {
                                activity.loadingView.setErrorText(msg_);
                                activity.loadingView.showError();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity.loadingView.setErrorText(activity.getString(R.string.query_data_is_null));
                            activity.loadingView.showError();
                        }
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
}
