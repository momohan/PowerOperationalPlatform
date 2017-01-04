package com.handu.poweroperational.main.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.ui.TableLayout.adapters.BaseTableAdapter;


/**
 * @创建人 柳梦
 * @时间 2016/9/28 15:20
 * @说明 表格适配器
 */
public abstract class MatrixTableAdapter<T> extends BaseTableAdapter {

    private final static int WIDTH_DIP = 100;//默认宽度
    private final static int HEIGHT_DIP = 50;//默认高度
    private final Context context;
    private T[][] table;
    private String headers[];
    private int widths[];
    private int width;
    private int height;
    private float density;

    protected MatrixTableAdapter(Context context, String headers[]) {
        this(context, headers, null);
    }

    public MatrixTableAdapter(Context context, String headers[], T[][] table) {
        this.context = context;
        this.headers = headers;
        if (table != null) {
            this.table = table;
        }
        Resources r = context.getResources();
        density = context.getResources().getDisplayMetrics().density;
        width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH_DIP, r.getDisplayMetrics()));
        height = Math.round(HEIGHT_DIP * density);
    }

    //设置数据源并刷新
    public void setBodyData(T[][] table) {
        this.table = table;
        notifyDataSetChanged();
    }

    //清除原有数据并刷新
    public void clearAll() {
        this.table = null;
        notifyDataSetChanged();
    }

    //设置高度dp
    public void setHeight(int height) {
        this.height = Math.round(height * density);
    }

    //设置宽度dp
    public void setWidths(int widths[]) {
        if (widths.length != headers.length) {
            return;
        }
        this.widths = widths;
    }

    //设置宽度dp
    public void setWidth(int width) {
        widths = null;
        Resources r = context.getResources();
        this.width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, r.getDisplayMetrics()));
    }

    @Override
    public int getRowCount() {
        if (table == null)
            return 0;
        else
            return table.length;
    }

    @Override
    public int getColumnCount() {
        return headers.length - 1;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {

        switch (getItemViewType(row, column)) {
            case 0:
                convertView = getFirstHeader(convertView, parent);
                break;
            case 1:
                convertView = getHeader(column, convertView, parent);
                break;
            case 2:
                convertView = getFirstBody(row, column, convertView, parent);
                break;
            case 3:
                convertView = getBody(row, column, convertView, parent);
                break;
            default:
                throw new RuntimeException("wtf?");
        }
        return convertView;
    }

    @Override
    public int getHeight(int row) {
        return height;
    }

    @Override
    public int getWidth(int column) {
        if (widths != null) {
            width = Math.round(widths[column + 1] * density);
        }
        return width;
    }

    @Override
    public int getItemViewType(int row, int column) {
        final int itemViewType;
        if (row == -1 && column == -1) {
            itemViewType = 0;
        } else if (row == -1) {
            itemViewType = 1;
        } else if (column == -1) {
            itemViewType = 2;
        } else {
            itemViewType = 3;
        }
        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    private View getFirstHeader(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_header_first, parent, false);
        }
        TextView tv = ((TextView) convertView.findViewById(android.R.id.text1));
        tv.setText(headers[0]);
        return convertView;
    }

    private View getHeader(int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_header, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(headers[column + 1]);
        return convertView;
    }

    private View getFirstBody(final int row, final int column, View convertView, ViewGroup parent) {
        TableViewHolder viewHolder = TableViewHolder.get(context, convertView, parent, R.layout.item_table_body_first);
        convert(viewHolder, table[row][column + 1], row + 1, column + 2);
        return viewHolder.getConvertView();
    }

    private View getBody(final int row, int column, View convertView, ViewGroup parent) {
        TableViewHolder viewHolder = TableViewHolder.get(context, convertView, parent, R.layout.item_table_body);
        convert(viewHolder, table[row][column + 1], row + 1, column + 2);
        return viewHolder.getConvertView();
    }

    public abstract void convert(TableViewHolder viewHolder, T item, int row, int column);
}
