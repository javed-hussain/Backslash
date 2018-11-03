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

package com.jhbros.backslash.utils;

/*
 * Created by javed
 */


import android.content.Context;
import android.os.Environment;

import com.jhbros.backslash.R;
import com.jhbros.backslash.exceptions.FileFormatsException;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilesUtil {
    private static final Boolean showHidden = false;

    private static File ROOT = Environment.getExternalStorageDirectory();

    public static File getROOT() {
        return ROOT;
    }

    public static void setROOT(File ROOT) {
        FilesUtil.ROOT = ROOT;
    }

    public static List<File> getSortedFiles(File f) {
        List<File> returnedFiles = new ArrayList<>();
        final List<File> files = new ArrayList<>();
        List<File> directories = new ArrayList<>();
        for (File file : f.listFiles()) {
            if (!(!showHidden && file.getName().startsWith("."))) {
                if (file.isDirectory()) {
                    directories.add(file);
                } else {
                    files.add(file);
                }
            }
        }
        Collections.sort(directories, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
        Collections.sort(files);
        returnedFiles.addAll(directories);
        returnedFiles.addAll(files);
        return returnedFiles;

    }

    public static String convertSize(long bytes) {
        String[] units = new String[]{"bytes", "KB", "MB", "GB", "TB"};
        double val = bytes;
        int i = 0;
        while (val > 1024) {
            val /= 1024;
            i++;
        }
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);
        return String.valueOf(format.format(val) + " " + units[i]);
    }

    public static List<File> searchFile(String searchTerm, File root) {
        List<File> files = new ArrayList<>();
        for (File f : root.listFiles()) {
            if (f.getName().toUpperCase().matches("(.*)" + searchTerm.toUpperCase() + "(.*)")) {
                files.add(f);
            }
            if (f.isDirectory()) {
                files.addAll(searchFile(searchTerm, f));
            }
        }
        return files;
    }

    public static boolean isRoot(File f) {
        return f.getAbsolutePath().equalsIgnoreCase(ROOT.getAbsolutePath());
    }

    public enum FileType {
        AUDIO, VIDEO, COMPRESSED, DOCUMENT, ENCRYPTED, IMAGE, APK, OTHER, FOLDER;

        private static final List<String> AUDIO_EXT = new ArrayList<>();
        private static final List<String> VIDEO_EXT = new ArrayList<>();
        private static final List<String> COMPRESSED_EXT = new ArrayList<>();
        private static final List<String> DOCUMENT_EXT = new ArrayList<>();
        private static final List<String> ENCRYPTED_EXT = new ArrayList<>();
        private static final List<String> IMAGE_EXT = new ArrayList<>();
        private static final List<String> APK_EXT = new ArrayList<>();
        private static boolean isValuesSet = false;

        public static void setValuesFromResources(Context ctx) {
            AUDIO_EXT.addAll(Arrays.asList(ctx.getResources().getStringArray(R.array.audioFormats)));
            VIDEO_EXT.addAll(Arrays.asList(ctx.getResources().getStringArray(R.array.videoFormats)));
            DOCUMENT_EXT.addAll(Arrays.asList(ctx.getResources().getStringArray(R.array.documentFormats)));
            IMAGE_EXT.addAll(Arrays.asList(ctx.getResources().getStringArray(R.array.imageFormats)));
            APK_EXT.addAll(Arrays.asList(ctx.getResources().getStringArray(R.array.apkFormats)));
            ENCRYPTED_EXT.addAll(Arrays.asList(ctx.getResources().getStringArray(R.array.encryptedFormats)));
            COMPRESSED_EXT.addAll(Arrays.asList(ctx.getResources().getStringArray(R.array.compressedFormats)));
            isValuesSet = true;
        }

        public static FileType getFileType(File file) throws FileFormatsException {
            if (!isValuesSet) throw new FileFormatsException("File formats are not set currently");
            if (file.isDirectory()) return FOLDER;
            String extension = "";
            if (file.getName().contains(".")) {
                extension = file.getName().substring(file.getName().lastIndexOf('.') + 1).toLowerCase();
            }
            if (AUDIO_EXT.contains(extension)) return AUDIO;
            if (VIDEO_EXT.contains(extension)) return VIDEO;
            if (COMPRESSED_EXT.contains(extension)) return COMPRESSED;
            if (DOCUMENT_EXT.contains(extension)) return DOCUMENT;
            if (IMAGE_EXT.contains(extension)) return IMAGE;
            if (ENCRYPTED_EXT.contains(extension)) return ENCRYPTED;
            if (APK_EXT.contains(extension)) return APK;
            return OTHER;
        }
    }
}
