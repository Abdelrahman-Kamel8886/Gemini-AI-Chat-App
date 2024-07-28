package com.abdok.geminichat.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UriTypeConverter {

    public static Uri fromString(String value) {
        return value == null ? null : Uri.parse(value);
    }

    public static String uriToString(Uri uri) {
        return uri == null ? null : uri.toString();
    }

    public static Bitmap getBitmapFromUri(Uri uri, ContentResolver contentResolver) throws IOException {

        InputStream inputStream = null;
        Bitmap bitmap = null;

        try {
            inputStream = contentResolver.openInputStream(uri);
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return bitmap;
    }

    public static String saveImageToStorage(Bitmap bitmap, Context context) {
        String fileName = "image_" + System.currentTimeMillis() + ".png";
        File directory = context.getFilesDir();
        File file = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
    public static Bitmap loadImageFromStorage(String path) {
        try {
            File file = new File(path);
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
