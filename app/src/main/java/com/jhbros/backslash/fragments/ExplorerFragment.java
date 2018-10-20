package com.jhbros.backslash.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jhbros.backslash.R;
import com.jhbros.backslash.adapters.FilesListRecyclerViewAdapter;
import com.jhbros.backslash.interfaces.ListItemClickListener;
import com.jhbros.backslash.utils.FilesUtil;

import java.io.File;

public class ExplorerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.explorer_fragment,container,false);


        RecyclerView filesList=view.findViewById(R.id.files_list);
        filesList.setHasFixedSize(true);
        filesList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        final FilesListRecyclerViewAdapter adapter=new FilesListRecyclerViewAdapter(getContext(),FilesUtil.getSortedFiles(Environment.getExternalStorageDirectory()));
        adapter.setListItemClickListener(new ListItemClickListener() {
            @Override
            public void onClick(File f) {
                if(f.isDirectory())
                    adapter.setFiles(FilesUtil.getSortedFiles(f));
                adapter.notifyDataSetChanged();
            }
        });
        filesList.setAdapter(adapter);


        return view;
    }
}
