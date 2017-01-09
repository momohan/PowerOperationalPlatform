package com.handu.poweroperational.main.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.main.bean.FileModel;
import com.handu.poweroperational.ui.progress.NumberProgressBar;
import com.handu.poweroperational.utils.ApkUtils;
import com.handu.poweroperational.utils.AppCacheUtils;
import com.handu.poweroperational.utils.Tools;
import com.lzy.okhttpserver.download.DownloadInfo;
import com.lzy.okhttpserver.download.DownloadManager;
import com.lzy.okhttpserver.download.DownloadService;
import com.lzy.okhttpserver.listener.DownloadListener;
import com.lzy.okhttpserver.task.ExecutorWithListener;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.GetRequest;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DownLoadActivity extends BaseActivity implements ExecutorWithListener.OnAllTaskEndListener {

    private static final int REQUEST_READ_STORAGE = 0;
    private List<FileModel> fileModels;
    //默认标题
    private static String title = "下载中心";
    //默认下载路径
    private static String fileDir = "/sdcard/HanDu_App/App_download/";
    private List<DownloadInfo> allTask;
    private DownLoadAdapter adapter;
    private DownloadManager downloadManager;
    private boolean autoInstall;
    private boolean restartDownload;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.targetFolder)
    TextView targetFolder;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.removeAll)
    Button removeAll;
    @Bind(R.id.pauseAll)
    Button pauseAll;
    @Bind(R.id.stopAll)
    Button stopAll;
    @Bind(R.id.startAll)
    Button startAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
        //记得移除，否者会回调多次
        if (downloadManager != null)
            downloadManager.getThreadPool().getExecutor().removeOnAllTaskEndListener(this);
    }

    /**
     * @param context         对象
     * @param title           标题
     * @param fileDir         文件存放路径
     * @param models          下载对象
     * @param restartDownload 是否重新下载
     * @param autoInstall     如果是Apk，是否自动安装
     */
    public static void runActivity(Context context, String title, String fileDir,
                                   List<FileModel> models, boolean restartDownload, boolean autoInstall) {
        Intent intent = new Intent(context, DownLoadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("fileDir", fileDir);
        bundle.putBoolean("restartDownload", restartDownload);
        bundle.putBoolean("autoInstall", autoInstall);
        bundle.putSerializable("models", (Serializable) models);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        Intent intent = this.getIntent();
        title = intent.getStringExtra("title") == null ? title : intent.getStringExtra("title");
        fileModels = (List<FileModel>) intent.getSerializableExtra("models");
        fileDir = intent.getStringExtra("fileDir") == null ? fileDir : intent.getStringExtra("fileDir");
        targetFolder.append(fileDir);
        autoInstall = intent.getBooleanExtra("autoInstall", false);
        restartDownload = intent.getBooleanExtra("restartDownload", false);
        initToolBar(toolbar, title, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, false, null, null);
    }

    @Override
    protected void initData() {
        downloadManager = DownloadService.getDownloadManager();
        downloadManager.setTargetFolder(fileDir);
        downloadManager.getThreadPool().getExecutor().addOnAllTaskEndListener(this);
        if (restartDownload) downloadManager.removeAllTask();
        allTask = downloadManager.getAllTask();
        adapter = new DownLoadAdapter();
        listView.setAdapter(adapter);
        populateDownload();
    }


    private void populateDownload() {
        if (!mayRequestStorage()) {
            return;
        }
        if (!Tools.isNetworkAvailable()) return;
        for (FileModel model : fileModels) {
            GetRequest request = OkHttpUtils.get(model.getUrl());
            downloadManager.addTask(model.getName(), model.getUrl(), request, null);
            AppCacheUtils.getInstance(getApplicationContext()).put(model.getUrl(), model);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * @创建人 柳梦
     * @时间 2016/9/20 9:39
     * @说明 动态申请内存卡读写敏感权限
     */
    private boolean mayRequestStorage() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) &&
                shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(toolbar, R.string.permission_storage, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{
                                    READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.length == permissions.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Tools.showToast(getResources().getString(R.string.permission_error));
                        return;
                    }
                }
                populateDownload();
            } else {
                Tools.showToast(getResources().getString(R.string.permission_error));
            }
        }
    }

    @Override
    public void onAllTaskEnd() {
        for (DownloadInfo downloadInfo : allTask) {
            if (downloadInfo.getState() != DownloadManager.FINISH) {
                Tools.showToast("所有下载线程结束，部分下载未完成");
                return;
            }
        }
    }

    @OnClick({R.id.removeAll, R.id.pauseAll, R.id.stopAll, R.id.startAll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.removeAll:
                downloadManager.removeAllTask();
                adapter.notifyDataSetChanged();  //移除的时候需要调用
                break;
            case R.id.pauseAll:
                downloadManager.pauseAllTask();
                break;
            case R.id.stopAll:
                downloadManager.stopAllTask();
                break;
            case R.id.startAll:
                downloadManager.startAllTask();
                break;
        }
    }

    private class DownLoadAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return allTask.size();
        }

        @Override
        public DownloadInfo getItem(int position) {
            return allTask.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            DownloadInfo downloadInfo = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_download_manager, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //对于非进度更新的ui放在这里，对于实时更新的进度ui，放在holder中
            FileModel apk = (FileModel) AppCacheUtils.getInstance(mContext).getObject(downloadInfo.getUrl());
            if (apk != null) {
                Glide.with(mContext).load(apk.getIconUrl()).centerCrop().error(R.mipmap.ic_launcher).into(holder.icon);
                holder.name.setText(apk.getName());
            } else {
                holder.name.setText(downloadInfo.getFileName());
            }
            holder.refresh(downloadInfo);
            holder.download.setOnClickListener(holder);
            holder.remove.setOnClickListener(holder);
            holder.restart.setOnClickListener(holder);
            DownloadListener downloadListener = new MyDownloadListener();
            downloadListener.setUserTag(holder);
            downloadInfo.setListener(downloadListener);
            return convertView;
        }
    }

    private class ViewHolder implements View.OnClickListener {
        private DownloadInfo downloadInfo;
        private ImageView icon;
        private TextView name;
        private TextView downloadSize;
        private TextView tvProgress;
        private TextView netSpeed;
        private NumberProgressBar pbProgress;
        private Button download;
        private Button remove;
        private Button restart;

        public ViewHolder(View convertView) {
            icon = (ImageView) convertView.findViewById(R.id.icon);
            name = (TextView) convertView.findViewById(R.id.text);
            downloadSize = (TextView) convertView.findViewById(R.id.downloadSize);
            tvProgress = (TextView) convertView.findViewById(R.id.tvProgress);
            netSpeed = (TextView) convertView.findViewById(R.id.netSpeed);
            pbProgress = (NumberProgressBar) convertView.findViewById(R.id.pbProgress);
            download = (Button) convertView.findViewById(R.id.start);
            remove = (Button) convertView.findViewById(R.id.remove);
            restart = (Button) convertView.findViewById(R.id.restart);
        }

        public void refresh(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
            refresh();
        }

        //对于实时更新的进度ui，放在这里，例如进度的显示，而图片加载等，不要放在这，会不停的重复回调
        //也会导致内存泄漏
        private void refresh() {
            String downloadLength = Formatter.formatFileSize(mContext, downloadInfo.getDownloadLength());
            String totalLength = Formatter.formatFileSize(mContext, downloadInfo.getTotalLength());
            downloadSize.setText(downloadLength + "/" + totalLength);
            if (downloadInfo.getState() == DownloadManager.NONE) {
                netSpeed.setText("停止");
                download.setText("下载");
            } else if (downloadInfo.getState() == DownloadManager.PAUSE) {
                netSpeed.setText("暂停中");
                download.setText("继续");
            } else if (downloadInfo.getState() == DownloadManager.ERROR) {
                netSpeed.setText("下载出错");
                download.setText("出错");
            } else if (downloadInfo.getState() == DownloadManager.WAITING) {
                netSpeed.setText("等待中");
                download.setText("等待");
            } else if (downloadInfo.getState() == DownloadManager.FINISH) {
                final File file = new File(downloadInfo.getTargetPath());
                if (autoInstall) {
                    download.setText("打开");
                    autoInstall = false;
                    ApkUtils.install(mContext, file);
                } else {
                    download.setText("打开");
                }
                netSpeed.setText("下载完成");
            } else if (downloadInfo.getState() == DownloadManager.DOWNLOADING) {
                String networkSpeed = Formatter.formatFileSize(mContext, downloadInfo.getNetworkSpeed());
                netSpeed.setText(networkSpeed + "/s");
                download.setText("暂停");
            }
            tvProgress.setText((Math.round(downloadInfo.getProgress() * 10000) * 1.0f / 100) + "%");
            pbProgress.setMax((int) downloadInfo.getTotalLength());
            pbProgress.setProgress((int) downloadInfo.getDownloadLength());
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == download.getId()) {
                switch (downloadInfo.getState()) {
                    case DownloadManager.PAUSE:
                    case DownloadManager.NONE:
                    case DownloadManager.ERROR:
                        downloadManager.addTask(downloadInfo.getUrl(), downloadInfo.getRequest(), downloadInfo.getListener());
                        break;
                    case DownloadManager.DOWNLOADING:
                        downloadManager.pauseTask(downloadInfo.getUrl());
                        break;
                    case DownloadManager.FINISH:
                        ApkUtils.install(mContext, new File(downloadInfo.getTargetPath()));
                        break;
                }
                refresh();
            } else if (v.getId() == remove.getId()) {
                downloadManager.removeTask(downloadInfo.getUrl());
                adapter.notifyDataSetChanged();
            } else if (v.getId() == restart.getId()) {
                downloadManager.restartTask(downloadInfo.getUrl());
            }
        }
    }

    private class MyDownloadListener extends DownloadListener {

        @Override
        public void onProgress(DownloadInfo downloadInfo) {
            if (getUserTag() == null) return;
            ViewHolder holder = (ViewHolder) getUserTag();
            holder.refresh();  //这里不能使用传递进来的 DownloadInfo，否者会出现条目错乱的问题
        }

        @Override
        public void onFinish(DownloadInfo downloadInfo) {
            Tools.showToast(downloadInfo.getFileName() + "：下载完成");
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
            if (errorMsg != null) Tools.showToast(errorMsg);
        }
    }
}
