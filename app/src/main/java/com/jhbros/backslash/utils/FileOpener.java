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
