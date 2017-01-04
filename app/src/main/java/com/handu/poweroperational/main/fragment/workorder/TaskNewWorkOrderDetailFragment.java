package com.handu.poweroperational.main.fragment.workorder;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.gson.reflect.TypeToken;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.base.BaseFragment;
import com.handu.poweroperational.callback.JsonDialogCallback;
import com.handu.poweroperational.callback.StringDialogCallback;
import com.handu.poweroperational.main.activity.SelectActivity;
import com.handu.poweroperational.main.activity.materials.MaterialsSelectActivity;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.main.bean.constants.WorkOrderPriority;
import com.handu.poweroperational.main.bean.constants.WorkOrderState;
import com.handu.poweroperational.main.bean.constants.WorkOrderType;
import com.handu.poweroperational.main.bean.results.DataDictionaryResult;
import com.handu.poweroperational.main.bean.results.ImageResult;
import com.handu.poweroperational.main.bean.results.NodeTimeResult;
import com.handu.poweroperational.main.bean.results.WorkOrderResult;
import com.handu.poweroperational.request.OkHttpRequest;
import com.handu.poweroperational.ui.RecyclerView.adapter.BaseRecyclerViewHolder;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.handu.poweroperational.ui.StepperIndicator;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.BitmapUtils;
import com.handu.poweroperational.utils.PreferencesUtils;
import com.handu.poweroperational.utils.ServiceUrl;
import com.handu.poweroperational.utils.Tools;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.BaseRequest;
import com.transitionseverywhere.ChangeText;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 工作任务工单详情
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskNewWorkOrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskNewWorkOrderDetailFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private final int SELECT_BEFORE_IMAGE = 0;
    private final int SELECT_AFTER_IMAGE = 1;
    private static final int UP_WORK_ORDER_STATE = 0;
    private static final int GET_UPLOAD_IMAGE = 1;
    private static final int GET_UPLOAD_MATERIALS = 3;
    private static final int UPLOAD_IMAGE_AFTER = 2;
    private static final int UPLOAD_IMAGE_BEFORE = 4;
    private static final int GET_TROUBLE_GENRE = 5;
    private static final int GET_NODE_TIME = 6;
    private static final int ERROR = -1;

    @Bind(R.id.layout_detail_handle)
    LinearLayout layoutDetailHandle;
    @Bind(R.id.layout_detail_materials)
    LinearLayout layoutDetailMaterials;
    @Bind(R.id.layout_detail_locale_affirm)
    LinearLayout layoutDetailLocaleAffirm;
    @Bind(R.id.tv_is_accept)
    TextView tvIsAccept;
    @Bind(R.id.bt_action)
    Button btAction;
    @Bind(R.id.bt_refuse)
    Button btRefuse;
    @Bind(R.id.ngv_image_before)
    NineGridView ngvImageBefore;
    @Bind(R.id.ngv_image_after)
    NineGridView ngvImageAfter;
    @Bind(R.id.et_handle_content)
    EditText etHandleContent;
    @Bind(R.id.rl_content)
    RelativeLayout rlContent;
    @Bind(R.id.si_step)
    StepperIndicator siStep;
    @Bind(R.id.tv_accept_time)
    TextView tvAcceptTime;
    @Bind(R.id.tv_arrived_time)
    TextView tvArrivedTime;
    @Bind(R.id.tv_finished_time)
    TextView tvFinishedTime;
    @Bind(R.id.recyclerView_materials)
    RecyclerView recyclerViewMaterials;
    @Bind(R.id.tv_work_priority)
    TextView tvWorkPriority;
    @Bind(R.id.tv_work_client_name)
    TextView tvWorkClientName;
    @Bind(R.id.et_xz_ry)
    TextInputEditText etXzRy;
    @Bind(R.id.sp_work_trouble_genre)
    MaterialSpinner spWorkTroubleGenre;
    private WorkOrderResult workOrderResult;
    private int position;
    //故障类型
    private String troubleGenreId = "";
    //协助人id
    private String xzrId = "";
    @Bind(R.id.tv_work_num)
    TextView tvWorkNum;
    @Bind(R.id.tv_work_type)
    TextView tvWorkType;
    @Bind(R.id.tv_work_content)
    TextView tvWorkContent;
    @Bind(R.id.tv_work_address)
    TextView tvWorkAddress;
    @Bind(R.id.tv_work_start_time)
    TextView tvWorkStartTime;
    @Bind(R.id.tv_work_fzr)
    TextView tvWorkFzr;
    @Bind(R.id.tv_work_xzry)
    TextView tvWorkXzr;
    @Bind(R.id.tv_work_state)
    TextView tvWorkState;
    private CommonRecyclerViewAdapter<String> mMaterialsAdapter;

    private ArrayList<ImageInfo> imageBeforeInfo;
    private ArrayList<ImageInfo> imageAfterInfo;
    private List<String> troubleText;
    private List<String> troubleValue;
    private MsgHandler mHandler = new MsgHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            workOrderResult = getArguments().getParcelable(ARG_PARAM1);
            position = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        setViewVisibility();
        getWorkOrderTroubleGenre();
        if (workOrderResult.getCurrentState() == WorkOrderState.unFinish.getState()
                || workOrderResult.getCurrentState() == WorkOrderState.finished.getState()) {
            getUploadImageList();
        }
        if (workOrderResult.getCurrentState() == WorkOrderState.unArrive.getState()
                || workOrderResult.getCurrentState() == WorkOrderState.unFinish.getState()
                || workOrderResult.getCurrentState() == WorkOrderState.finished.getState()) {
            getSelectMaterialsList();
        }
    }

    @Override
    public void onDestroyView() {
        OkHttpUtils.getInstance().cancelTag(this);
        mHandler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public TaskNewWorkOrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TaskNewWorkOrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskNewWorkOrderDetailFragment newInstance(WorkOrderResult workOrderResult, int position) {
        TaskNewWorkOrderDetailFragment fragment = new TaskNewWorkOrderDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, workOrderResult);
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int findView() {
        return R.layout.fragment_task_work_order_detail;
    }

    @Override
    protected void initView() {
        imageBeforeInfo = new ArrayList<>();
        imageAfterInfo = new ArrayList<>();
        troubleText = new ArrayList<>();
        troubleValue = new ArrayList<>();
        spWorkTroubleGenre.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long i, Object item) {
                troubleGenreId = troubleValue.get(position);
            }
        });
    }

    @Override
    protected void initData() {
        //加载工单信息
        WorkOrderPriority priority = WorkOrderPriority.getPriority(workOrderResult.getPriority());
        if (priority != null)
            tvWorkPriority.setText(priority.getColorName());
        WorkOrderType type = WorkOrderType.getType(workOrderResult.getType());
        if (type != null)
            tvWorkType.setText(type.getColorName());
        tvWorkClientName.setText(workOrderResult.getClientName() == null ? "无" : workOrderResult.getClientName());
        tvWorkNum.setText(workOrderResult.getNumber() == null ? System.currentTimeMillis() + "" : workOrderResult.getNumber());
        tvWorkContent.setText(workOrderResult.getContent() == null ? "无" : workOrderResult.getContent());
        tvWorkAddress.setText(workOrderResult.getAddress() == null ? "无" : workOrderResult.getAddress());
        tvWorkStartTime.setText(workOrderResult.getCreateDate() == null ? "无" : workOrderResult.getCreateDate());
        tvWorkFzr.setText(workOrderResult.getResponseUserName() == null ? "无" : workOrderResult.getResponseUserName());
        tvWorkXzr.setText(workOrderResult.getParticipantNames() == null ? "无" : workOrderResult.getParticipantNames());
        etXzRy.setText(workOrderResult.getParticipantNames() == null ? "" : workOrderResult.getParticipantNames());
        etHandleContent.setText(workOrderResult.getFinishContent() == null ? "" : workOrderResult.getFinishContent());
        //初始化工器具物资加载
        mMaterialsAdapter = new CommonRecyclerViewAdapter<String>(mContext, R.layout.item_materials_list) {
            @Override
            public void convert(BaseRecyclerViewHolder helper, String item, int position) {
                TextView tv = helper.getView(R.id.tv_materials_name);
                tv.setText(item);
            }
        };
        SlideInBottomAnimationAdapter alphaAdapter = new SlideInBottomAnimationAdapter(mMaterialsAdapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator(0.5f));
        alphaAdapter.setDuration(1000);
        alphaAdapter.setFirstOnly(false);
        recyclerViewMaterials.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewMaterials.setAdapter(alphaAdapter);
    }

    //设置相关控件的显示或隐藏
    private void setViewVisibility() {
        getNodeTime();
        TransitionSet transitionSet = new TransitionSet();
        Slide slide = new Slide(Gravity.END);
        slide.setDuration(1000);
        transitionSet.addTransition(slide);
        ChangeText changeText = new ChangeText();
        changeText.setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN).setDuration(1000);
        transitionSet.addTransition(changeText);
        TransitionManager.beginDelayedTransition(rlContent, transitionSet);
        int state = workOrderResult.getCurrentState();
        WorkOrderState orderState = WorkOrderState.getState(state);
        if (orderState != null) {
            tvWorkState.setText(orderState.getColorName());
            switch (orderState) {
                case unAccept:
                    siStep.setCurrentStep(0);
                    btAction.setText(getResources().getText(R.string.action_accept));
                    btRefuse.setVisibility(View.VISIBLE);
                    break;
                case unArrive:
                    siStep.setCurrentStep(1);
                    btAction.setText(getResources().getText(R.string.action_arrive));
                    btRefuse.setVisibility(View.GONE);
                    layoutDetailMaterials.setVisibility(View.VISIBLE);
                    break;
                case unFinish:
                    siStep.setCurrentStep(2);
                    layoutDetailLocaleAffirm.setVisibility(View.VISIBLE);
                    layoutDetailMaterials.setVisibility(View.VISIBLE);
                    layoutDetailHandle.setVisibility(View.VISIBLE);
                    btAction.setText(getResources().getText(R.string.action_finish));
                    break;
                case finished:
                    siStep.setCurrentStep(3);
                    btAction.setText(getResources().getText(R.string.action_has_finished));
                    etHandleContent.setEnabled(false);
                    layoutDetailLocaleAffirm.setVisibility(View.VISIBLE);
                    layoutDetailMaterials.setVisibility(View.VISIBLE);
                    layoutDetailHandle.setVisibility(View.VISIBLE);
                    btAction.setVisibility(View.GONE);
                    break;
                case refuse:
                    siStep.setCurrentStep(1);
                    tvIsAccept.setText(getResources().getText(R.string.is_refuse));
                    btAction.setVisibility(View.GONE);
                    btRefuse.setVisibility(View.GONE);
                    break;
            }
        }
    }

    //设置节点时间
    private void setNodeTime(NodeTimeResult result) {
        if (workOrderResult.getCurrentState() == WorkOrderState.refuse.getState()) {
            if (!TextUtils.isEmpty(result.getAlreadyRefuse())) {
                tvAcceptTime.setText(result.getAlreadyRefuse());
            }
        } else {
            if (!TextUtils.isEmpty(result.getAlreadyReceive())) {
                tvAcceptTime.setText(result.getAlreadyReceive());
            }
            if (!TextUtils.isEmpty(result.getAlreadyArrive())) {
                tvArrivedTime.setText(result.getAlreadyArrive());
            }
            if (!TextUtils.isEmpty(result.getAlreadyFinish())) {
                tvFinishedTime.setText(result.getAlreadyFinish());
            }
        }
    }

    @OnClick({R.id.ibt_image_before_add, R.id.ibt_image_after_add, R.id.bt_action,
            R.id.bt_refuse, R.id.ibt_materials_add, R.id.et_xz_ry})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibt_image_before_add:
                gotoActivityForResult(ImageGridActivity.class, null, SELECT_BEFORE_IMAGE);
                break;
            case R.id.ibt_image_after_add:
                gotoActivityForResult(ImageGridActivity.class, null, SELECT_AFTER_IMAGE);
                break;
            case R.id.bt_action:
                actionSubmit();
                break;
            case R.id.bt_refuse:
                actionRefuse();
                break;
            case R.id.ibt_materials_add:
                Bundle bundle = new Bundle();
                bundle.putParcelable("workOrderResult", workOrderResult);
                gotoActivityForResult(MaterialsSelectActivity.class, bundle, MaterialsSelectActivity.REQUEST_CODE);
                break;
            case R.id.et_xz_ry:
                Map<String, String> map = new HashMap<>();
                map.put("OrganizeId", PreferencesUtils.get(mContext, AppConstant.organizeId, "") + "");
                SelectActivity.runSelectActivityForResult(getActivity(),
                        "协助人员", map, ServiceUrl.GetUserTreeJson, false, "id", "text", "ChildNodes");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null) {
                if (requestCode == SELECT_BEFORE_IMAGE) {
                    List<ImageItem> imageItems = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    //要上传的原图文件信息
                    List<ImageInfo> imageInfoList = new ArrayList<>();
                    //要上传的压缩图文件信息
                    List<String> uploadList = new ArrayList<>();
                    if (imageItems != null && imageItems.size() > 0) {
                        for (int i = 0; i < imageItems.size(); i++) {
                            //获取压缩后的图片地址
                            String uploadUrl = BitmapUtils.getCompressImagePath(mContext, imageItems.get(i).path, null);
                            uploadList.add(uploadUrl);
                            //获取选择的图片原图地址
                            String url = "file://" + imageItems.get(i).path;
                            ImageInfo info = new ImageInfo();
                            info.setThumbnailUrl(url);
                            info.setBigImageUrl(url);
                            imageInfoList.add(info);
                        }
                    }
                    uploadImages(imageInfoList, uploadList, SELECT_BEFORE_IMAGE);
                } else if (requestCode == SELECT_AFTER_IMAGE) {
                    List<ImageItem> imageItems = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    //要上传的原图文件信息
                    List<ImageInfo> imageInfoList = new ArrayList<>();
                    //要上传的压缩图文件信息
                    List<String> uploadList = new ArrayList<>();
                    if (imageItems != null && imageItems.size() > 0) {
                        for (int i = 0; i < imageItems.size(); i++) {
                            //获取压缩后的图片地址
                            String uploadUrl = BitmapUtils.getCompressImagePath(mContext, imageItems.get(i).path, null);
                            uploadList.add(uploadUrl);
                            //获取选择的图片原图地址
                            String url = "file://" + imageItems.get(i).path;
                            ImageInfo info = new ImageInfo();
                            info.setThumbnailUrl(url);
                            info.setBigImageUrl(url);
                            imageInfoList.add(info);
                        }
                    }
                    uploadImages(imageInfoList, uploadList, SELECT_AFTER_IMAGE);
                }
            }
        } else if (requestCode == MaterialsSelectActivity.REQUEST_CODE) {
            if (data != null) {
                String result = data.getStringExtra(MaterialsSelectActivity.MATERIALS_RESULT);
                try {
                    JSONArray array = new JSONArray(result);
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String name = object.optString("text");
                        String count = object.optString("count", "1");
                        list.add(name + "*" + (Integer.parseInt(count)));
                    }
                    mMaterialsAdapter.appendList(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == SelectActivity.REQUEST_CODE) {
            if (data != null) {
                String text = data.getStringExtra(SelectActivity.TEXT);
                String value = data.getStringExtra(SelectActivity.VALUE);
                if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(value)) {
                    etXzRy.setText(text);
                    xzrId = value;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //拒绝操作
    private void actionRefuse() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_new_work_order_detail_refuse, null);
        final EditText et = (EditText) view.findViewById(R.id.et_refuse_reason);
        AlertView alertView = new AlertView(getResources().getString(R.string.sweet_warn), getResources().getString(R.string.hint_refuse_content), getResources().getString(R.string.bt_cancel), null, new String[]{"确认"},
                mContext, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position != AlertView.CANCELPOSITION) {
                    String str = et.getText().toString();
                    if (!TextUtils.isEmpty(str)) {
                        int nextState = WorkOrderState.refuse.getState();
                        Map<String, String> map = new HashMap<>();
                        map.put("DetailId", workOrderResult.getDetailId());
                        map.put("CurrentState", nextState + "");
                        map.put("ModifyUserName", PreferencesUtils.get(mContext, AppConstant.username, "") + "");
                        map.put("ModifyUserId", PreferencesUtils.get(mContext, AppConstant.userId, "") + "");
                        map.put("Type", workOrderResult.getType() + "");
                        map.put("Description", str);
                        submit(map, nextState);
                    } else {
                        Tools.showToast(getResources().getString(R.string.refuse_reason_is_null));
                    }
                }
            }
        });
        alertView.addExtView(view);
        alertView.show();
    }

    //提交操作
    private void actionSubmit() {
        int state = workOrderResult.getCurrentState();
        WorkOrderState orderState = WorkOrderState.getState(state);
        Map<String, String> map = new HashMap<>();
        map.put("DetailId", workOrderResult.getDetailId());
        map.put("ModifyUserName", PreferencesUtils.get(mContext, AppConstant.username, "") + "");
        map.put("ModifyUserId", PreferencesUtils.get(mContext, AppConstant.userId, "") + "");
        map.put("Type", workOrderResult.getType() + "");
        int nextState;
        if (orderState != null) {
            switch (orderState) {
                case unAccept:
                    nextState = WorkOrderState.unArrive.getState();
                    map.put("CurrentState", nextState + "");
                    submit(map, nextState);
                    break;
                case unArrive:
                    nextState = WorkOrderState.unFinish.getState();
                    map.put("CurrentState", nextState + "");
                    submit(map, nextState);
                    break;
                case unFinish:
                    String str = etHandleContent.getText().toString();
                    if (TextUtils.isEmpty(str)) {
                        etHandleContent.setError(getResources().getString(R.string.handle_content_is_null));
                        etHandleContent.requestFocus();
                    } else {
                        nextState = WorkOrderState.finished.getState();
                        map.put("CurrentState", nextState + "");
                        map.put("FinishContent", str);
                        map.put("FaultType", troubleGenreId);
                        String xzrName = etXzRy.getText().toString();
                        if (!TextUtils.isEmpty(xzrId) && !TextUtils.isEmpty(xzrName)) {
                            map.put("ParticipantIds", xzrId);
                            map.put("ParticipantNames", xzrName);
                        }
                        submit(map, nextState);
                    }
                    break;
            }
        }
    }

    /**
     * 上传图片
     *
     * @param imageInfoList 要上传的原文件地址列表
     * @param uploadList    上传的文件列表
     * @param type          上传类型(前，后)
     */
    //上传图片
    private void uploadImages(final List<ImageInfo> imageInfoList, List<String> uploadList, int type) {
        String fileName = "files";
        List<File> files = new ArrayList<>();
        for (String url : uploadList) {
            files.add(new File(url));
        }
        Map<String, String> map = new HashMap<>();
        map.put("DetailId", workOrderResult.getDetailId());
        final int imgType;
        switch (type) {
            case SELECT_BEFORE_IMAGE:
                imgType = 0;
                map.put("imgType", imgType + "");
                OkHttpRequest.upload(getActivity(), ServiceUrl.UploadImg, map, fileName, files,
                        new StringDialogCallback(getActivity(), getString(R.string.is_uploading)) {

                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                Message message = new Message();
                                message.obj = imageInfoList;
                                message.what = UPLOAD_IMAGE_BEFORE;
                                mHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                error(e);
                            }
                        });
                break;
            case SELECT_AFTER_IMAGE:
                imgType = 1;
                map.put("imgType", imgType + "");
                OkHttpRequest.upload(getActivity(), ServiceUrl.UploadImg, map, fileName, files,
                        new StringDialogCallback(getActivity(), getString(R.string.is_uploading)) {

                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                Message message = new Message();
                                message.obj = imageInfoList;
                                message.what = UPLOAD_IMAGE_AFTER;
                                mHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                error(e);
                            }
                        });
                break;
        }
    }

    /**
     * @param map       参数
     * @param nextState 改变后下一个节点的状态
     */
    //提交
    private void submit(Map map, final int nextState) {
        OkHttpRequest.post(getActivity(), ServiceUrl.UpWorkOrderState, map,
                new JsonDialogCallback<String>(getActivity(), String.class) {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Message message = new Message();
                        message.what = UP_WORK_ORDER_STATE;
                        message.obj = nextState;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        error(e);
                    }
                });
    }

    //获取已选择物资工器具
    private void getUploadImageList() {
        Map<String, String> map = new HashMap<>();
        map.put("DetailId", workOrderResult.getDetailId());
        OkHttpRequest.post(getActivity(), ServiceUrl.GetImageList, map, new JsonDialogCallback<List<ImageResult>>(getActivity(), new TypeToken<List<ImageResult>>() {
        }.getType(), getString(R.string.request_upload_image)) {

            @Override
            public void onSuccess(List<ImageResult> imageResults, Call call, Response response) {
                Message message = new Message();
                message.what = GET_UPLOAD_IMAGE;
                message.obj = imageResults;
                mHandler.sendMessage(message);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                error(e);
            }
        });
    }

    //获取已选择物资工器具
    private void getSelectMaterialsList() {
        List<String> list = new ArrayList<>();
        mMaterialsAdapter.fillList(list);
    }

    //获取问题类型
    private void getWorkOrderTroubleGenre() {

        OkHttpRequest.post(getActivity(), ServiceUrl.GetDictionaryData, "QuestionType",
                new JsonDialogCallback<List<DataDictionaryResult>>(getActivity(), new TypeToken<List<DataDictionaryResult>>() {
                }.getType()) {

                    @Override
                    public void onBefore(BaseRequest request) {

                    }

                    @Override
                    public void onSuccess(List<DataDictionaryResult> dataDictionaryResults, Call call, Response response) {
                        Message msg = new Message();
                        msg.obj = dataDictionaryResults;
                        msg.what = GET_TROUBLE_GENRE;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        error(e);
                    }
                });
    }

    //获取节点时间
    private void getNodeTime() {
        OkHttpRequest.post(getActivity(), ServiceUrl.GetWorkOrderNodeTime, workOrderResult.getDetailId(),
                new JsonDialogCallback<NodeTimeResult>(getActivity(), NodeTimeResult.class) {
                    @Override
                    public void onBefore(BaseRequest request) {

                    }

                    @Override
                    public void onSuccess(NodeTimeResult nodeTimeResult, Call call, Response response) {
                        Message msg = new Message();
                        msg.what = GET_NODE_TIME;
                        msg.obj = nodeTimeResult;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        error(e);
                    }
                });
    }

    //错误的时候调用
    private void error(Exception e) {
        Message message = new Message();
        message.obj = e;
        message.what = ERROR;
        mHandler.sendMessage(message);
    }

    //弱引用避免内存泄漏
    private static class MsgHandler extends Handler {

        private WeakReference<Fragment> reference;

        MsgHandler(Fragment context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final TaskNewWorkOrderDetailFragment fragment = (TaskNewWorkOrderDetailFragment) reference.get();
            if (fragment != null) {
                int what = msg.what;
                List<ImageInfo> list;
                switch (what) {
                    case UP_WORK_ORDER_STATE:
                        int state = (int) msg.obj;
                        WorkOrderState orderState = WorkOrderState.getState(state);
                        if (orderState != null) {
                            switch (orderState) {
                                case finished:
                                    String xzrName = fragment.etXzRy.getText().toString();
                                    String content = fragment.etHandleContent.getText().toString();
                                    String faultType = fragment.troubleGenreId;
                                    if (!TextUtils.isEmpty(xzrName)) {
                                        fragment.workOrderResult.setParticipantNames(xzrName);
                                        fragment.tvWorkXzr.setText(xzrName);
                                    }
                                    if (!TextUtils.isEmpty(content))
                                        fragment.workOrderResult.setFinishContent(content);
                                    if (!TextUtils.isEmpty(xzrName))
                                        fragment.workOrderResult.setFaultType(faultType);
                                    break;
                            }
                            fragment.workOrderResult.setCurrentState(state);
                            fragment.setViewVisibility();
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", fragment.position);
                            bundle.putParcelable("workOrderResult", fragment.workOrderResult);
                            EventBus.getDefault().post(new BaseEvent(EventType.newWorkOrderUpdate.getType(), bundle));
                        }
                        break;
                    case GET_UPLOAD_IMAGE:
                        List<ImageResult> imageResults = (List<ImageResult>) msg.obj;
                        if (imageResults != null && imageResults.size() > 0) {
                            for (ImageResult imageResult : imageResults) {
                                ImageInfo info = new ImageInfo();
                                info.setThumbnailUrl(ServiceUrl.BaseIp + "/" + imageResult.getThumbnailPath());
                                info.setBigImageUrl(ServiceUrl.BaseIp + "/" + imageResult.getImgPath());
                                if (imageResult.getType() == 0) {
                                    fragment.imageBeforeInfo.add(info);
                                    list = new ArrayList<>();
                                    list.addAll(fragment.imageBeforeInfo);
                                    NineGridViewClickAdapter adapter = new NineGridViewClickAdapter(fragment.mContext, list);
                                    fragment.ngvImageBefore.setAdapter(adapter);
                                } else if (imageResult.getType() == 1) {
                                    fragment.imageAfterInfo.add(info);
                                    list = new ArrayList<>();
                                    list.addAll(fragment.imageAfterInfo);
                                    NineGridViewClickAdapter afterAdapter = new NineGridViewClickAdapter(fragment.mContext, list);
                                    fragment.ngvImageAfter.setAdapter(afterAdapter);
                                }
                            }
                        }
                        break;
                    case GET_UPLOAD_MATERIALS:
                        break;
                    case UPLOAD_IMAGE_BEFORE:
                        List<ImageInfo> imageInfoBefore = (List<ImageInfo>) msg.obj;
                        fragment.imageBeforeInfo.addAll(imageInfoBefore);
                        list = new ArrayList<>();
                        list.addAll(fragment.imageBeforeInfo);
                        NineGridViewClickAdapter adapter = new NineGridViewClickAdapter(fragment.mContext, list);
                        fragment.ngvImageBefore.setAdapter(adapter);
                        break;
                    case UPLOAD_IMAGE_AFTER:
                        List<ImageInfo> imageInfoAfter = (List<ImageInfo>) msg.obj;
                        fragment.imageAfterInfo.addAll(imageInfoAfter);
                        list = new ArrayList<>();
                        list.addAll(fragment.imageAfterInfo);
                        NineGridViewClickAdapter afterAdapter = new NineGridViewClickAdapter(fragment.mContext, list);
                        fragment.ngvImageAfter.setAdapter(afterAdapter);
                        break;
                    case GET_TROUBLE_GENRE:
                        List<DataDictionaryResult> dataDictionaryResults = (List<DataDictionaryResult>) msg.obj;
                        if (dataDictionaryResults != null && dataDictionaryResults.size() > 0) {
                            for (DataDictionaryResult result : dataDictionaryResults) {
                                fragment.troubleText.add(result.getItemName());
                                fragment.troubleValue.add(result.getItemDetailId());
                            }
                            if (fragment.spWorkTroubleGenre != null) {
                                fragment.spWorkTroubleGenre.setItems(fragment.troubleText);
                                fragment.troubleGenreId = fragment.troubleValue.get(0);
                                String faultType = fragment.workOrderResult.getFaultType();
                                if (!TextUtils.isEmpty(faultType)) {
                                    for (int i = 0; i < fragment.troubleValue.size(); i++) {
                                        if (faultType.equals(fragment.troubleValue.get(i))) {
                                            fragment.spWorkTroubleGenre.setSelectedIndex(i);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case GET_NODE_TIME:
                        NodeTimeResult result = (NodeTimeResult) msg.obj;
                        if (result != null) {
                            fragment.setNodeTime(result);
                        }
                        break;
                    case ERROR:
                        Exception e = (Exception) msg.obj;
                        Tools.showToast(e.getMessage());
                        AppLogger.e(e.getMessage());
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
}
