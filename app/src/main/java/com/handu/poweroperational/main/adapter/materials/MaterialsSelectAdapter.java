package com.handu.poweroperational.main.adapter.materials;

import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.activity.materials.MaterialsSelectActivity;
import com.handu.poweroperational.main.bean.MaterialsModel;

import java.text.NumberFormat;


/**
 * 柳梦
 * 已选择物资适配器
 */
public class MaterialsSelectAdapter extends RecyclerView.Adapter<MaterialsSelectAdapter.ViewHolder> {

    private MaterialsSelectActivity activity;
    private ArrayMap<String, MaterialsModel> dataList;
    private NumberFormat nf;
    private LayoutInflater mInflater;

    public MaterialsSelectAdapter(MaterialsSelectActivity activity, ArrayMap<String, MaterialsModel> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_materials_select_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MaterialsModel item = dataList.valueAt(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MaterialsModel item;
        private TextView tvCount, tvAdd, tvMinus, tvName;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCount = (TextView) itemView.findViewById(R.id.count);
            tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            tvAdd = (TextView) itemView.findViewById(R.id.tvAdd);
            tvMinus.setOnClickListener(this);
            tvAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvAdd:
                    activity.add(item);
                    break;
                case R.id.tvMinus:
                    activity.remove(item);
                    break;
            }
        }

        void bindData(MaterialsModel item) {
            this.item = item;
            tvName.setText(item.text);
            tvCount.setText(String.valueOf(item.count));
        }
    }
}
