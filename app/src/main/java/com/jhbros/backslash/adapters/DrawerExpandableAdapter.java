package com.jhbros.backslash.adapters;

/*
 * Created by javed on 11/4/2018
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jhbros.backslash.R;
import com.jhbros.backslash.models.DrawerMenuModel;
import com.jhbros.backslash.utils.FilesUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class DrawerExpandableAdapter extends BaseExpandableListAdapter {

    private List<File> storages;
    private Context ctx;

    public DrawerExpandableAdapter(Context ctx) {
        this.ctx = ctx;
        this.storages = FilesUtil.getAllStorageLocations(ctx, ctx.getPackageName());
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return storages.size();
    }

    @Override
    public DrawerMenuModel getGroup(int groupPosition) {
        return new DrawerMenuModel("Header - " + groupPosition, true, true);
    }

    @Override
    public DrawerMenuModel getChild(int groupPosition, int childPosition) {
        return new DrawerMenuModel("Child - " + groupPosition, false, false);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.list_group_header, null);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.list_group_child, null);
        TextView storageName = convertView.findViewById(R.id.storage_name);
        TextView storageStats = convertView.findViewById(R.id.storage_stats);
        TextView percentUsage = convertView.findViewById(R.id.percent_usage);
        ProgressBar percentProgress = convertView.findViewById(R.id.usage_progress);
        HashMap<String, String> map = FilesUtil.getStats(storages.get(childPosition));
        storageName.setText(FilesUtil.getStorageName(storages.get(childPosition)));
        storageStats.setText(map.get("AVAILABLE") + " free of " + map.get("TOTAL"));
        percentUsage.setText(map.get("PERCENT") + "%");
        percentProgress.setProgress(Integer.parseInt(map.get("PERCENT")));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
