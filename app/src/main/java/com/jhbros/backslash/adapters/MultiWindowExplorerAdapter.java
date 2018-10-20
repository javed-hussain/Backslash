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
