package com.handu.poweroperational.ui.AndroidTree;

import android.content.Context;
import android.util.ArrayMap;

import com.handu.poweroperational.ui.AndroidTree.holder.IconTreeItemHolder;
import com.handu.poweroperational.ui.AndroidTree.holder.SelectableHeaderHolder;
import com.unnamed.b.atv.model.TreeNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：柳梦 2016/9/19 13:35
 * 邮箱：mobyq@qq.com
 * 说明: 节点解析工具类
 */
public class TreeNodeUtils {

    /**
     * @param mContext  对象
     * @param array     数据集合
     * @param root      原始节点
     * @param icon      图片id
     * @param text      节点名称
     * @param value     节点值
     * @param childName 子节点集合名称
     * @return
     */
    public static TreeNode getTreeNode(Context mContext, JSONArray array, TreeNode root, int icon, String text, String value, String childName) {
        return getTreeNode(mContext, array, root, icon, text, value, childName, null);
    }

    /**
     * @param mContext  对象
     * @param array     数据集合
     * @param root      原始节点
     * @param icon      图片id
     * @param text      节点名称
     * @param value     节点值
     * @param childName 子节点集合名称
     * @param other     其他字段
     * @return
     */
    public static TreeNode getTreeNode(Context mContext, JSONArray array, TreeNode root, int icon, String text, String value, String childName, String[] other) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String text_ = object.optString(text);
                String value_ = object.optString(value);
                TreeNode node;
                if (other != null) {
                    ArrayMap<String, Object> map = new ArrayMap<>();
                    for (String s : other) {
                        Object o = object.opt(s);
                        if (o != null) {
                            String result = o.toString();
                            map.put(s, result);
                        } else {
                            map.put(s, "");
                        }
                    }
                    node = new TreeNode(new IconTreeItemHolder.IconTreeItem(icon, text_, value_, map))
                            .setViewHolder(new SelectableHeaderHolder(mContext));
                } else {
                    node = new TreeNode(new IconTreeItemHolder.IconTreeItem(icon, text_, value_))
                            .setViewHolder(new SelectableHeaderHolder(mContext));
                }
                root.addChild(node);
                if (!object.isNull(childName)) {
                    JSONArray a = object.getJSONArray(childName);
                    if (a.length() > 0)
                        getTreeNode(mContext, a, node, icon, text, value, childName, other);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    /**
     * @param mContext  对象
     * @param object    数据对象
     * @param root      原始节点
     * @param icon      图片id
     * @param text      节点名称
     * @param value     节点值
     * @param childName 子节点集合名称
     * @return
     */

    public static TreeNode getTreeNode(Context mContext, JSONObject object, TreeNode root, int icon, String text, String value, String childName) {

        return getTreeNode(mContext, object, root, icon, text, value, childName, null);
    }

    /**
     * @param mContext  对象
     * @param object    数据对象
     * @param root      原始节点
     * @param icon      图片id
     * @param text      节点名称
     * @param value     节点值
     * @param childName 子节点集合名称
     * @param other     其他字段
     * @return
     */
    public static TreeNode getTreeNode(Context mContext, JSONObject object, TreeNode root, int icon, String text, String value, String childName, String[] other) {

        String text_ = object.optString(text);
        String value_ = object.optString(value);
        TreeNode node;
        if (other != null) {
            ArrayMap<String, Object> map = new ArrayMap<>();
            for (String s : other) {
                Object o = object.opt(s);
                if (o != null) {
                    String result = o.toString();
                    map.put(s, result);
                } else {
                    map.put(s, "");
                }
            }
            node = new TreeNode(
                    new IconTreeItemHolder.IconTreeItem(icon, text_, value_, map))
                    .setViewHolder(new SelectableHeaderHolder(mContext));
        } else {
            node = new TreeNode(
                    new IconTreeItemHolder.IconTreeItem(icon, text_, value_))
                    .setViewHolder(new SelectableHeaderHolder(mContext));
        }
        if (!object.isNull(childName)) {
            JSONArray array = object.optJSONArray(childName);
            if (array.length() > 0)
                getTreeNode(mContext, array, node, icon, text, value, childName, other);
        }
        root.addChild(node);
        return root;
    }
}
