package com.jhbros.backslash.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilesUtil {
    public static final Boolean showHidden = false;

    public static List<File> getSortedFiles(File f) {
        List<File> returnedFiles = new ArrayList<>();
        final List<File> files = new ArrayList<>();
        List<File> directories = new ArrayList<>();
        for(File file:f.listFiles()){
            if(!(!showHidden && file.getName().startsWith("."))){
                if(file.isDirectory()){
                    directories.add(file);
                }else {
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

    public static String convertSize(long bytes){
        String[] units=new String[]{"bytes","KB","MB","GB","TB"};
        double val=bytes;
        int i=0;
        while(val>1024){
            val/=1024;
            i++;
        }
        DecimalFormat format=new DecimalFormat();
        format.setMaximumFractionDigits(2);
        return String.valueOf(format.format(val)+" "+units[i]);
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

        static {
            // Audio formats to be recognised
            AUDIO_EXT.add("mp3");
            AUDIO_EXT.add("wav");
            AUDIO_EXT.add("aac");
            AUDIO_EXT.add("aiff");
            AUDIO_EXT.add("m4a");
            AUDIO_EXT.add("oga");
            AUDIO_EXT.add("wma");
            AUDIO_EXT.add("raw");

            // Video formats to be recognised
            VIDEO_EXT.add("webm");
            VIDEO_EXT.add("mkv");
            VIDEO_EXT.add("flv");
            VIDEO_EXT.add("avi");
            VIDEO_EXT.add("mov");
            VIDEO_EXT.add("wmv");
            VIDEO_EXT.add("mp4");
            VIDEO_EXT.add("mpg");
            VIDEO_EXT.add("mpeg");
            VIDEO_EXT.add("m4v");
            VIDEO_EXT.add("3gp");

            // Compressed formats to be recognised
            COMPRESSED_EXT.add("zip");
            COMPRESSED_EXT.add("tar");
            COMPRESSED_EXT.add("gz");
            COMPRESSED_EXT.add("7z");
            COMPRESSED_EXT.add("dmg");
            COMPRESSED_EXT.add("rar");
            COMPRESSED_EXT.add("jar");

            //Document formats to be recognised
            DOCUMENT_EXT.add("doc");
            DOCUMENT_EXT.add("ppt");
            DOCUMENT_EXT.add("docx");
            DOCUMENT_EXT.add("txt");

            //Encrypted formats to be recognised
            ENCRYPTED_EXT.add("bsenc");

            //Image formats to be recognised
            IMAGE_EXT.add("jpg");
            IMAGE_EXT.add("jpeg");
            IMAGE_EXT.add("png");
            IMAGE_EXT.add("gif");

            //Apk format to be recognised
            APK_EXT.add("apk");

        }

        public static FileType getFileType(File file) {
            if(file.isDirectory()) return FOLDER;
            String extension="";
            if(file.getName().contains(".")) {
             extension= file.getName().substring(file.getName().lastIndexOf('.')+1).toLowerCase();
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
