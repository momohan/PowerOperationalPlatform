package com.handu.poweroperational.ui.AndroidTree.holder;

import android.content.Context;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.handu.poweroperational.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by 柳梦 on 2/12/15.
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {

    private TextView tvText;
    private TextView tvValue;
    private PrintView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvText = (TextView) view.findViewById(R.id.node_text);
        tvValue.setText(value.value);
        tvText.setText(value.text);

        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));
        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);
        view.findViewById(R.id.btn_addFolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeNode newFolder = new TreeNode(new IconTreeItem(R.string.ic_folder, "New Folder", "1"));
                getTreeView().addNode(node, newFolder);
            }
        });

        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTreeView().removeNode(node);
            }
        });

        //if My computer
        if (node.getLevel() == 1) {
            view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem {

        public int icon;
        public String text;
        public String value;
        public ArrayMap<String, Object> others;

        public IconTreeItem(int icon, String text, String value) {
            this.icon = icon;
            this.text = text;
            this.value = value;
        }

        public IconTreeItem(int icon, String text, String value, ArrayMap<String, Object> others) {
            this.icon = icon;
            this.text = text;
            this.value = value;
            this.others = others;
        }
    }
}
