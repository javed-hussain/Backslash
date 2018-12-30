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
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jhbros.backslash.R;
import com.jhbros.backslash.adapters.DrawerExpandableAdapter;
import com.jhbros.backslash.adapters.MultiWindowExplorerAdapter;
import com.jhbros.backslash.fragments.ExplorerFragment;
import com.jhbros.backslash.interfaces.FileNavigatorChangedListener;
import com.jhbros.backslash.interfaces.Observable;
import com.jhbros.backslash.interfaces.Observer;
import com.jhbros.backslash.models.FileItem;
import com.jhbros.backslash.utils.FilesUtil;
import com.jhbros.backslash.views.FilePathNavigationView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
    private ExplorerFragment currentFragment;
    private SearchView searchView;
    private int noOfSelections = 0;
    private MenuMode menuMode = MenuMode.CORE;
    private boolean isCut = false;

    private List<FileItem> filesToCopy = new ArrayList<>();

//    private static final String TAG = MainActivity.class.getName();

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
        currentFragment = ((ExplorerFragment) adapter.getItem(0));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                // Not Needed to Override
            }

            @Override
            public void onPageSelected(int i) {
                currentFragment = ((ExplorerFragment) adapter.getItem(i));
                currentFragment.notifyObservers();
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
        if (getSupportActionBar() == null) {
            Toolbar toolbar = findViewById(R.id.main_toolbar);
            toolbar.setSubtitleTextAppearance(this, R.style.subtitle);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle("5 Folders, 15 Files");

        setHomeIcon(R.drawable.menu);

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
        ExpandableListView menuList = navigationView.findViewById(R.id.menu_list);
        menuList.setAdapter(new DrawerExpandableAdapter(getApplicationContext()));
    }

    private void setHomeIcon(@DrawableRes int id) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(id);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (menuMode) {
            case CORE:
                getMenuInflater().inflate(R.menu.main_menu, menu);
                if (menu instanceof MenuBuilder) {
                    ((MenuBuilder) menu).setOptionalIconsVisible(true);
                }
                setupSearch(menu);
                break;
            case FILE_OPERATIONS:
                getMenuInflater().inflate(R.menu.file_operation_menu, menu);
                break;
            case PASTE_ENABLED:
                getMenuInflater().inflate(R.menu.paste_menu, menu);
                break;
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearch(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.file_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        LinearLayout searchBar = searchView.findViewById(androidx.appcompat.R.id.search_bar);
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(getResources().getColor(R.color.white));
        editText.setHintTextColor(getResources().getColor(R.color.off_white));
        editText.setBackgroundResource(R.drawable.searchbar_edittext_background);
        searchView.setQueryHint("Search here...");
        searchBar.setLayoutTransition(new LayoutTransition());

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathNavigationView.setSearchTerm("Search Results");
                setHomeIcon(R.drawable.back);
                currentFragment.setFiles(new ArrayList<FileItem>());
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                pathNavigationView.setValues(currentFolder);
                setHomeIcon(R.drawable.menu);
                currentFragment.setFiles(FilesUtil.getSortedFiles(currentFolder));
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                currentFragment.setProcessing(true);
                pathNavigationView.setSearchTerm(String.format("Search results for - \"%s\"", s));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final List<FileItem> files = FilesUtil.searchFile(s, FilesUtil.getROOT());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentFragment.setFiles(files);
                                currentFragment.setProcessing(false);
                            }
                        });
                    }
                }).start();
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
                if (!menuMode.equals(MenuMode.CORE)) {
                    menuMode = MenuMode.CORE;
                    filesToCopy.clear();
                    onSelectionModeChanged(false, 0);
                    refresh();
                } else if (!searchView.isIconified()) {
                    onBackPressed();
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            case R.id.cut:
                handleCut();
                break;
            case R.id.copy:
                handleCopy();
                break;
            case R.id.delete:
                handleDelete();
                break;
            case R.id.paste:
                handlePaste();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handlePaste() {
        Log.d("Total Files to copy : ", "" + filesToCopy.size());
        for (FileItem item : filesToCopy) {
            try {
                Log.d("Copying file: ", item.getFile().getAbsolutePath());
                if (!item.getFile().isDirectory())
                    FileUtils.copyFile(item.getFile(), new File(currentFolder, item.getFile().getName()));
                else
                    FileUtils.copyDirectory(item.getFile(), new File(currentFolder, item.getFile().getName()));

                if (isCut) FilesUtil.cleanupDirectoriesAndFile(item.getFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isCut = false;
        menuMode = MenuMode.CORE;
        invalidateOptionsMenu();
        setupToolbarAndDrawer();
        refresh();
    }

    private void handleCopy() {
        menuMode = MenuMode.PASTE_ENABLED;
        List<FileItem> files = currentFragment.getAdapter().getFiles();
        for (int i = 0; i < files.size(); i++) {
            FileItem f = files.get(i);
            Log.d("Details", f.toString());
            if (f.isSelected()) {
                filesToCopy.add(f);
            }
        }
        currentFragment.setFiles(FilesUtil.getSortedFiles(currentFolder));
        invalidateOptionsMenu();
    }

    private void handleDelete() {
        List<FileItem> removals = new ArrayList<>();
        List<FileItem> files = currentFragment.getAdapter().getFiles();
        for (int i = 0; i < files.size(); i++) {
            FileItem f = files.get(i);
            Log.d("Details", f.toString());
            if (f.isSelected()) {
                try {
                    FilesUtil.cleanupDirectoriesAndFile(f.getFile());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                removals.add(f);
            }
        }
        for (FileItem a : removals) files.remove(a);
        currentFragment.setFiles(files);
    }


    private void handleCut() {
        isCut = true;
        handleCopy();
    }

    @Override
    public void onBackPressed() {
        if (noOfSelections > 0) {
            onSelectionModeChanged(false, 0);
            refresh();
            return;
        }
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        if (FilesUtil.isRoot(this.currentFolder)) {
//            Log.d(TAG, "Inside ROOT");
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
//            Log.d(TAG, "Not Inside ROOT");
            this.currentFolder = currentFolder.getParentFile();
            this.pathNavigationView.setValues(this.currentFolder);
            refresh();
        }
    }

    private void refresh() {
        MultiWindowExplorerAdapter adapter = (MultiWindowExplorerAdapter) pager.getAdapter();
        if (adapter != null)
            adapter.navigateTo(this.pager.getCurrentItem(), this.currentFolder);
    }


    @Override
    public void onUpdate(Observable observable, File changedFolder) {
        this.currentFolder = changedFolder;
    }

    @Override
    public void onSelectionModeChanged(boolean mode, int noOfSelections) {
        if (noOfSelections != this.noOfSelections) {
            if (noOfSelections > 0) {
                this.noOfSelections = noOfSelections;
                setHomeIcon(R.drawable.back);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(noOfSelections + " Selected");
                    getSupportActionBar().setSubtitle("");
                }
                menuMode = MenuMode.FILE_OPERATIONS;

            } else {
                this.noOfSelections = 0;
                if (!menuMode.equals(MenuMode.PASTE_ENABLED)) {
                    menuMode = MenuMode.CORE;
                    setupToolbarAndDrawer();
                }
            }
            invalidateOptionsMenu();
        }
    }

    private enum MenuMode {
        CORE, FILE_OPERATIONS, PASTE_ENABLED
    }
}