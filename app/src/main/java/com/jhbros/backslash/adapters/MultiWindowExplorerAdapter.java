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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jhbros.backslash.fragments.ExplorerFragment;
import com.jhbros.backslash.interfaces.OnFolderLocationChangeListner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MultiWindowExplorerAdapter extends FragmentPagerAdapter {
    private final Context ctxt;
    private int pageCount;
    private List<ExplorerFragment> fragments = new ArrayList<>();

    public MultiWindowExplorerAdapter(Context context, FragmentManager mgr, int windows) {
        super(mgr);
        this.ctxt = context;
        this.pageCount = windows;
        for (int i = 0; i < windows; i++) {
            fragments.add(new ExplorerFragment());
        }
    }

    @Override
    public int getCount() {
        return (pageCount);
    }

    @Override
    public Fragment getItem(int position) {
        return (fragments.get(position));
    }

    @Override
    public String getPageTitle(int position) {
        return null;
    }

    void setPageCount(int pageCount) {
        if (pageCount > this.pageCount) {
            for (int i = this.pageCount; i < pageCount; i++) {
                fragments.add(new ExplorerFragment());
            }
        } else {
            for (int i = pageCount - 1; i >= this.pageCount; i++) {
                fragments.remove(i);
            }
        }
        this.pageCount = pageCount;
    }

    public void setOnFolderLocationChangeListener(OnFolderLocationChangeListner listener) {
        for (ExplorerFragment f : this.fragments) {
            f.setOnFolderLocationChangeListener(listener);
        }
    }

    public void navigateTo(int position, File f) {
        fragments.get(position).navigateTo(f);
    }


}
