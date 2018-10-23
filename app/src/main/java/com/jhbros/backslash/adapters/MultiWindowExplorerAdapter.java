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

/*
 * Created by javed
 */


import com.jhbros.backslash.activities.MainActivity;
import com.jhbros.backslash.fragments.ExplorerFragment;
import com.jhbros.backslash.views.FilePathNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MultiWindowExplorerAdapter extends FragmentPagerAdapter {
    private int pageCount;
    private List<ExplorerFragment> fragments = new ArrayList<>();

    public MultiWindowExplorerAdapter(MainActivity context, FragmentManager mgr, int windows, FilePathNavigationView pathNavigationView) {
        super(mgr);
        this.pageCount = windows;
        for (int i = 0; i < windows; i++) {
            ExplorerFragment fragment = new ExplorerFragment();
            fragment.subscribeObserver(pathNavigationView);
            fragment.subscribeObserver(context);
            fragments.add(fragment);
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

    void addPage(int i) {
        fragments.add(i, new ExplorerFragment());
        this.pageCount++;
    }

    void removePage(int i) {
        fragments.remove(i);
        this.pageCount--;
        notifyDataSetChanged();
    }

    public void navigateTo(int position, File f) {
        fragments.get(position).navigateTo(f);
    }


}
