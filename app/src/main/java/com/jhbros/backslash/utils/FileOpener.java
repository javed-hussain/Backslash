package com.jhbros.backslash.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

public class FileOpener {
    public static void openFile(File f, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        intent.setDataAndType(Uri.fromFile(f), mimeTypeMap.getMimeTypeFromExtension(f.getName().substring(f.getName().lastIndexOf(".") + 1)));
        context.startActivity(intent);
    }
}
