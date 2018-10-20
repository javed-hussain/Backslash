package com.jhbros.backslash.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.jhbros.backslash.activities.MainActivity;
import com.jhbros.backslash.fragments.ExplorerFragment;

public class MultiWindowExplorerAdapter extends FragmentPagerAdapter {
    private final Context ctxt;
    private int pageCount = 3;

    public MultiWindowExplorerAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt = ctxt;
    }

    @Override
    public int getCount() {
        return (pageCount);
    }

    @Override
    public Fragment getItem(int position) {
        return (new ExplorerFragment());
    }

    @Override
    public String getPageTitle(int position) {
        return null;
    }

    void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
