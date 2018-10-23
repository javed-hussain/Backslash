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


import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jhbros.backslash.R;
import com.jhbros.backslash.adapters.FilesListRecyclerViewAdapter;
import com.jhbros.backslash.interfaces.ListItemClickListener;
import com.jhbros.backslash.interfaces.Observable;
import com.jhbros.backslash.interfaces.Observer;
import com.jhbros.backslash.utils.FileOpener;
import com.jhbros.backslash.utils.FilesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExplorerFragment extends Fragment implements Observable {
    private FilesListRecyclerViewAdapter adapter;
    private List<Observer> observers = new ArrayList<>();
    private File currentFolder = FilesUtil.getROOT();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explorer_fragment, container, false);

        RecyclerView filesList = view.findViewById(R.id.files_list);
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
                } else {
                    FileOpener.openFile(f, getContext());
                }
                adapter.notifyDataSetChanged();
            }
        });
        filesList.setAdapter(adapter);
        return view;
    }

    public void navigateTo(File f) {
        this.currentFolder = f;
        this.adapter.setFiles(FilesUtil.getSortedFiles(f));
        adapter.notifyDataSetChanged();
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
    public void unsubscribeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void setFiles(List<File> files) {
        this.adapter.setFiles(files);
        this.adapter.notifyDataSetChanged();
    }
}
