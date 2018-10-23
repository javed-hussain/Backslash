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

package com.jhbros.backslash.activities;

/*
 * Created by javed
 */


import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jhbros.backslash.R;
import com.jhbros.backslash.adapters.MultiWindowExplorerAdapter;
import com.jhbros.backslash.fragments.ExplorerFragment;
import com.jhbros.backslash.interfaces.FileNavigatorChangedListener;
import com.jhbros.backslash.interfaces.Observable;
import com.jhbros.backslash.interfaces.Observer;
import com.jhbros.backslash.utils.FilesUtil;
import com.jhbros.backslash.views.FilePathNavigationView;

import java.io.File;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements Observer {
    private DrawerLayout drawerLayout;
    private FilePathNavigationView pathNavigationView;
    private ViewPager pager;
    private File currentFolder = FilesUtil.getROOT();

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeView();
    }

    private void initializeView() {
        setupToolbarAndDrawer();
        setupPagerAndBreadCrumb();

    }

    private void setupPagerAndBreadCrumb() {
        pathNavigationView = findViewById(R.id.path_navigator);
        pathNavigationView.setValues(FilesUtil.getROOT());

        pager = findViewById(R.id.pager);
        final MultiWindowExplorerAdapter adapter = new MultiWindowExplorerAdapter(this, getSupportFragmentManager(), 3, pathNavigationView);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(0);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager, true);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                // Not Needed to Override
            }

            @Override
            public void onPageSelected(int i) {
                ((ExplorerFragment) adapter.getItem(i)).notifyObservers();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                // Not Needed to Override
            }
        });

        pathNavigationView.setNavigatorChangedListener(new FileNavigatorChangedListener() {
            @Override
            public void onNavigationChanged(File f) {
                currentFolder = f;
                ((ExplorerFragment) adapter.getItem(pager.getCurrentItem())).navigateTo(f);
            }
        });
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle("5 Folders, 15 Files");
        toolbar.setSubtitleTextAppearance(this, R.style.subtitle);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
        navigationView.setItemIconTintList(null);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        setupSearch(menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearch(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.file_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        LinearLayout searchBar = searchView.findViewById(androidx.appcompat.R.id.search_bar);
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(getResources().getColor(R.color.white));
        editText.setHintTextColor(getResources().getColor(R.color.off_white));
        editText.setBackgroundResource(R.drawable.searchbar_edittext_background);
        searchView.setQueryHint("Search here...");
        searchBar.setLayoutTransition(new LayoutTransition());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ((ExplorerFragment) ((MultiWindowExplorerAdapter) pager.getAdapter()).getItem(pager.getCurrentItem())).setFiles(FilesUtil.searchFile(s, FilesUtil.getROOT()));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (FilesUtil.isRoot(this.currentFolder)) {
            Log.d(TAG, "Inside ROOT");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.app_name)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
            Log.d(TAG, "Not Inside ROOT");
            this.currentFolder = currentFolder.getParentFile();
            this.pathNavigationView.setValues(this.currentFolder);
            ((MultiWindowExplorerAdapter) pager.getAdapter()).navigateTo(this.pager.getCurrentItem(), this.currentFolder);
        }
    }

    @Override
    public void onUpdate(Observable observable, File changedFolder) {
        this.currentFolder = changedFolder;
    }
}
