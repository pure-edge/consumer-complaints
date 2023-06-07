package com.example.logingps.utils;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class UriConverter {
    public static String getFileName(Uri uri) {
        String path = uri.getPath();
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        return fileName;
    }

    public static List<String> getFileNames(List<Uri> uris) {
        List<String> fileNames = new ArrayList<>();
        for (Uri uri : uris) {
            String path = uri.getPath();
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            fileNames.add(fileName);
        }
        return fileNames;
    }
}
