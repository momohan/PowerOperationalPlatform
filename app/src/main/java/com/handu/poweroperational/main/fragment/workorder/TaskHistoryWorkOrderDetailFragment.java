package com.handu.poweroperational.main.fragment.workorder;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseFragment;
import com.handu.poweroperational.main.bean.constants.WorkOrderPriority;
import com.handu.poweroperational.main.bean.constants.WorkOrderState;
import com.handu.poweroperational.main.bean.constants.WorkOrderType;
import com.handu.poweroperational.main.bean.results.DataDictionaryResult;
import com.handu.poweroperational.main.bean.results.ImageResult;
import com.handu.poweroperational.main.bean.results.NodeTimeResult;
import com.handu.poweroperational.main.bean.results.WorkOrderResult;
import com.handu.poweroperational.request.RequestServer;
import com.handu.poweroperational.request.callback.JsonDialogCallback;
import com.handu.poweroperational.ui.RecyclerView.adapter.CommonRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.holder.BaseRecyclerViewHolder;
import com.handu.poweroperational.ui.widget.view.StepperIndicator;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.ServiceUrl;
import com.handu.poweroperational.utils.Tools;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.lzy.okhttputils.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskHistoryWorkOrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskHistoryWorkOrderDetailFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int GET_UPLOAD_IMAGE = 1;
    private static final int GET_UPLOAD_MATERIALS = 3;
    private static final int GET_TROUBLE_GENRE = 5;
    private static final int GET_NODE_TIME = 6;
    private static final int ERROR = -1;

    @Bind(R.id.tv_accept_time)
    TextView tvAcceptTime;
    @Bind(R.id.tv_arrived_time)
    TextView tvArrivedTime;
    @Bind(R.id.tv_finished_time)
    TextView tvFinishedTime;
    @Bind(R.id.si_step)
    StepperIndicator siStep;
    @Bind(R.id.tv_work_priority)
    TextView tvWorkPriority;
    @Bind(R.id.tv_work_client_name)
    TextView tvWorkClientName;
    @Bind(R.id.sp_work_trouble_genre)
    MaterialSpinner spWorkTroubleGenre;

    // TODO: Rename and change types of parameters
    private WorkOrderResult workOrderResult;
    int position;
    @Bind(R.id.ngv_image_before)
    NineGridView ngvImageBefore;
    @Bind(R.id.ngv_image_after)
    NineGridView ngvImageAfter;
    @Bind(R.id.et_handle_content)
    EditText etHandleContent;
    @Bind(R.id.recyclerView_materials)
    RecyclerView recyclerViewMaterials;
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
    private ArrayList<ImageInfo> imageBeforeInfo;
    private ArrayList<ImageInfo> imageAfterInfo;
    private CommonRecyclerViewAdapter<String> mMaterialsAdapter;
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
        getUploadImageList();
        getSelectMaterialsList();
        getWorkOrderTroubleGenre();
        getNodeTime();
    }

    @Override
    public void onDestroyView() {
        OkHttpUtils.getInstance().cancelTag(this);
        mHandler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public TaskHistoryWorkOrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TaskHistoryWorkOrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskHistoryWorkOrderDetailFragment newInstance(WorkOrderResult workOrderResult, int position) {
        TaskHistoryWorkOrderDetailFragment fragment = new TaskHistoryWorkOrderDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, workOrderResult);
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int findView() {
        return R.layout.fragment_task_history_work_order_detail;
    }

    @Override
    protected void initView() {
        troubleText = new ArrayList<>();
        troubleValue = new ArrayList<>();
        spWorkTroubleGenre.setEnabled(false);
    }

    @Override
    protected void initData() {
        imageBeforeInfo = new ArrayList<>();
        imageAfterInfo = new ArrayList<>();
        //加载工单信息
        siStep.setCurrentStep(3);
        WorkOrderPriority priority = WorkOrderPriority.getPriority(workOrderResult.getPriority());
        if (priority != null)
            tvWorkPriority.setText(priority.getColorName());
        int state = workOrderResult.getCurrentState();
        WorkOrderState orderState = WorkOrderState.getState(state);
        if (orderState != null) {
            tvWorkState.setText(orderState.getColorName());
        }
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
        etHandleContent.setText(workOrderResult.getFinishContent() == null ? "无" : workOrderResult.getFinishContent());
        //初始化物资加载
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

    /**
     * 获取已上传图片
     */
    private void getUploadImageList() {
        Map<String, String> map = new HashMap<>();
        map.put("DetailId", workOrderResult.getDetailId());
        RequestServer.post(getActivity(), ServiceUrl.GetImageList, map, new JsonDialogCallback<List<ImageResult>>(getActivity(), new TypeToken<List<ImageResult>>() {
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

    /**
     * 获取已选择物资
     */
    private void getSelectMaterialsList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add("物资" + (i + 1));
        }
        mMaterialsAdapter.fillList(list);
    }

    //获取问题类型
    private void getWorkOrderTroubleGenre() {

        RequestServer.post(getActivity(), ServiceUrl.GetDictionaryData, "QuestionType",
                new JsonDialogCallback<List<DataDictionaryResult>>(getActivity(), new TypeToken<List<DataDictionaryResult>>() {
                }.getType(), false) {

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
        RequestServer.post(getActivity(), ServiceUrl.GetWorkOrderNodeTime, workOrderResult.getDetailId(),
                new JsonDialogCallback<NodeTimeResult>(getActivity(), NodeTimeResult.class, false) {

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
            final TaskHistoryWorkOrderDetailFragment fragment = (TaskHistoryWorkOrderDetailFragment) reference.get();
            if (fragment != null) {
                int what = msg.what;
                List<ImageInfo> list;
                switch (what) {
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
                    case GET_TROUBLE_GENRE:
                        List<DataDictionaryResult> dataDictionaryResults = (List<DataDictionaryResult>) msg.obj;
                        if (dataDictionaryResults != null && dataDictionaryResults.size() > 0) {
                            for (DataDictionaryResult result : dataDictionaryResults) {
                                fragment.troubleText.add(result.getItemName());
                                fragment.troubleValue.add(result.getItemDetailId());
                            }
                            if (fragment.spWorkTroubleGenre != null) {
                                fragment.spWorkTroubleGenre.setItems(fragment.troubleText);
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
                        Tools.toastError(e.getMessage());
                        AppLogger.e(e.getMessage());
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
}
