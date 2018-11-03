package com.jhbros.backslash.models;

/*
 * Created by javed on 11/4/2018
 */

import java.io.File;

public class FileItem implements Comparable<FileItem> {
    private File file;
    private boolean isSelected = false;

    public FileItem(File file) {
        this.file = file;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int compareTo(FileItem o) {
        return this.file.compareTo(o.getFile());
    }
}
