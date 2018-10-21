/***************************************************************************************************
 * Copyright (c) 2018.
 *
 * This file is a part of Backslash File Manager
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************************************/

package com.jhbros.backslash.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhbros.backslash.R;
import com.jhbros.backslash.interfaces.ListItemClickListener;
import com.jhbros.backslash.utils.FilesUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FilesListRecyclerViewAdapter extends RecyclerView.Adapter<FilesListRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<File> files;
    private ListItemClickListener listItemClickListener;

    public FilesListRecyclerViewAdapter(Context context, List<File> files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView view = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.files_list_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final File f = files.get(i);
        setIcon(f, viewHolder);
        viewHolder.name.setText(f.getName());
        if (f.isDirectory()) {
            viewHolder.size.setText("Directory");
        } else {
            viewHolder.size.setText(FilesUtil.convertSize(f.length()));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
        viewHolder.lastModified.setText(dateFormat.format(new Date(f.lastModified())));
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemClickListener != null) listItemClickListener.onClick(f);
            }
        });
    }

    private void setIcon(File f, ViewHolder viewHolder) {
        switch (FilesUtil.FileType.getFileType(f)) {
            case APK:
                viewHolder.icon.setImageResource(R.drawable.android);
                break;
            case AUDIO:
                viewHolder.icon.setImageResource(R.drawable.music);
                viewHolder.icon.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
                break;
            case COMPRESSED:
                viewHolder.icon.setImageResource(R.drawable.compressed);
                break;
            case IMAGE:
                viewHolder.icon.setImageResource(R.drawable.image);
                break;
            case VIDEO:
                viewHolder.icon.setImageResource(R.drawable.video);
                break;
            case DOCUMENT:
                viewHolder.icon.setImageResource(R.drawable.document);
                break;
            case ENCRYPTED:
                viewHolder.icon.setImageResource(R.drawable.encrypted);
                break;
            case FOLDER:
                viewHolder.icon.setImageResource(R.drawable.folder);
                break;
            case OTHER:
                viewHolder.icon.setImageResource(R.drawable.file);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void setListItemClickListener(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView lastModified;
        private TextView size;
        private View view;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            lastModified = itemView.findViewById(R.id.last_modified);
            size = itemView.findViewById(R.id.size);
            view = itemView;
        }
    }


    public void setFiles(List<File> files) {
        this.files = files;
    }
}
