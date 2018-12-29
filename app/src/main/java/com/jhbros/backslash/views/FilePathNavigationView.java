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

package com.jhbros.backslash.views;

/*
 * Created by javed
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jhbros.backslash.R;
import com.jhbros.backslash.interfaces.FileNavigatorChangedListener;
import com.jhbros.backslash.interfaces.Observable;
import com.jhbros.backslash.interfaces.Observer;
import com.jhbros.backslash.utils.FilesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilePathNavigationView extends LinearLayout implements Observer {
    private Context context;
    private FileNavigatorChangedListener navigatorChangedListener;
    private LinearLayout layout;
    private HorizontalScrollView scrollView;
    private boolean inSearch = false;

    public FilePathNavigationView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public FilePathNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);

    }

    public FilePathNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);

    }

    public FilePathNavigationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void init(Context ctx, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.context = ctx;
        isInEditMode();
        TypedArray a = ctx.obtainStyledAttributes(attrs,
                R.styleable.FilePathNavigationView, defStyleAttr, defStyleRes);
        a.recycle();
        removeAllViews();
        scrollView = new HorizontalScrollView(context);
        scrollView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        addView(scrollView);
        layout = new LinearLayout(context);
        layout.setPadding(60, 20, 60, 20);
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.setOrientation(HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        scrollView.addView(layout);
    }

    private void setView(List<File> files) {
        if (layout == null || scrollView == null) {
            return;
        }
        layout.removeAllViews();
        int count = 0;
        for (final File file : files) {
            String a = FilesUtil.isRoot(file) ? "INTERNAL STORAGE" : file.getName().toUpperCase();
            TextView textView = getTextView(a);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (navigatorChangedListener != null) {
                        setValues(file);
                        navigatorChangedListener.onNavigationChanged(file);
                    }
                }
            });
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.path_separator);
            imageView.setPadding(20, 0, 10, 0);
            layout.addView(textView);
            if (++count != files.size()) {
                layout.addView(imageView);
                textView.setTextColor(getResources().getColor(R.color.off_white));
            } else {
                textView.setTextColor(getResources().getColor(R.color.white));
            }
        }
        scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    }

    private TextView getTextView(final String a) {
        TextView textView = new TextView(context);
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setText(a);

        return textView;
    }

    public void setValues(File folder) {
        inSearch = false;
        List<File> navigator = new ArrayList<>();
        while (folder != null && !FilesUtil.isRoot(folder)) {
            navigator.add(folder);
            folder = folder.getParentFile();
        }
        navigator.add(FilesUtil.getROOT());
        Collections.reverse(navigator);
        setView(navigator);
    }

    @Override
    public void onUpdate(Observable observable, File changedFolder) {
        if (!inSearch)
            this.setValues(changedFolder);
    }

    @Override
    public void onSelectionModeChanged(boolean mode, int noOfSelections) {
        //No need for any action
    }

    public void setNavigatorChangedListener(FileNavigatorChangedListener navigatorChangedListener) {
        this.navigatorChangedListener = navigatorChangedListener;
    }

    public void setSearchTerm(String searchTerm) {
        if (layout != null) {
            inSearch = true;
            layout.removeAllViews();
            layout.invalidate();
            TextView v = getTextView(searchTerm);
            v.setTextColor(getResources().getColor(R.color.white));
            layout.addView(v);
        }
    }
}
