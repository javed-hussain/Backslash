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

package com.jhbros.backslash.fragments;

/*
 * Created by javed
 */


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.jhbros.backslash.R;
import com.jhbros.backslash.adapters.FilesListRecyclerViewAdapter;
import com.jhbros.backslash.interfaces.ListItemClickListener;
import com.jhbros.backslash.interfaces.Observable;
import com.jhbros.backslash.interfaces.Observer;
import com.jhbros.backslash.models.FileItem;
import com.jhbros.backslash.utils.FileOpener;
import com.jhbros.backslash.utils.FilesUtil;
import com.jhbros.backslash.views.CreateNewDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;

public class ExplorerFragment extends Fragment implements Observable {
    private FilesListRecyclerViewAdapter adapter;
    private List<Observer> observers = new ArrayList<>();
    private File currentFolder = FilesUtil.getROOT();
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView filesList;
    private FABsMenu menu;
    private TitleFAB newFolder, newFile, newConnection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explorer_fragment, container, false);

        menu = view.findViewById(R.id.fabs_menu);
        newFolder = view.findViewById(R.id.menu_new_folder);
        newFile = view.findViewById(R.id.menu_new_file);
        newConnection = view.findViewById(R.id.menu_new_cloud);

        refreshLayout = view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setFiles(FilesUtil.getSortedFiles(currentFolder));
            }
        });
        filesList = view.findViewById(R.id.files_list);
        filesList.setHasFixedSize(true);
        filesList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new FilesListRecyclerViewAdapter(getContext(), FilesUtil.getSortedFiles(Environment.getExternalStorageDirectory()));
        adapter.setListItemClickListener(new ListItemClickListener() {
            @Override
            public void onClick(File f) {
                if (f.isDirectory()) {
                    currentFolder = f;
                    adapter.setFiles(FilesUtil.getSortedFiles(f));
                    notifyObservers();
                    runLayoutAnimation(filesList);
                } else {
                    FileOpener.openFile(f, getContext());
                }
            }

            @Override
            public void onClick(boolean mode, int noOfSelections) {
                notifyObservers(mode, noOfSelections);
            }
        });
        filesList.setAdapter(adapter);
        menu.attachToRecyclerView(filesList);
        runLayoutAnimation(filesList);
        setupMenus();
        return view;
    }

    private void setupMenus() {
        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(false);
            }
        });
        newFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(true);
            }
        });
        newConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "This menu will create a new Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDialog(final boolean isFile) {
        menu.collapse();
        final CreateNewDialog dialog = new CreateNewDialog(getContext(), isFile);
        dialog.show();
        dialog.attachCreateListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(currentFolder.getAbsolutePath(), dialog.getText());
                Log.d("File Created : ", f.getAbsolutePath());
                if (!isFile) {
                    f.mkdirs();
                } else {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        Log.d("Could not create file: ", f.getAbsolutePath());
                    }
                }
                dialog.dismiss();
                setFiles(FilesUtil.getSortedFiles(currentFolder));
            }
        });
        dialog.attachCancelListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setFiles(FilesUtil.getSortedFiles(currentFolder));
            }
        });
    }

    public void navigateTo(File f) {
        this.currentFolder = f;
        this.adapter.setFiles(FilesUtil.getSortedFiles(f));
        runLayoutAnimation(filesList);
    }

    @Override
    public void subscribeObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer ob : observers) {
            ob.onUpdate(this, this.currentFolder);
        }
    }

    @Override
    public void notifyObservers(boolean isSelectionMode, int noOfSelections) {
        for (Observer ob : observers) {
            ob.onSelectionModeChanged(isSelectionMode, noOfSelections);
        }
    }

    @Override
    public void unsubscribeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void setFiles(List<FileItem> files) {
        setProcessing(true);
        this.adapter.setFiles(files);
        notifyObservers();
        runLayoutAnimation(filesList);
        setProcessing(false);
    }

    public void setProcessing(boolean isProcessing) {
        refreshLayout.setRefreshing(isProcessing);
    }


    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_anim_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public FilesListRecyclerViewAdapter getAdapter() {
        return adapter;
    }
}
