package com.vadikfedchukgmail.imagerecognizer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

public class ResizeImage {


    //  создание объекта Bitmap из Uri, если фото было сделано с измененной ориентацией,
    //  метод getExifAngle определяет насколько нужно повернуть картинку
    public static Bitmap resizeSelectedImage(Context context, Uri uri, Integer mImageMaxWidth, Integer mImageMaxHeight) {
        Bitmap resizedBitmap = null;
        try {
            resizedBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(getExifAngle(context, uri));
        resizedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(),
                matrix, true);
        if (resizedBitmap != null) {

            int targetWidth = mImageMaxWidth;
            int maxHeight = mImageMaxHeight;

            float scaleFactor =
                    Math.max(
                            (float) resizedBitmap.getWidth() / (float) targetWidth,
                            (float) resizedBitmap.getHeight() / (float) maxHeight);
            resizedBitmap =
                    Bitmap.createScaledBitmap(
                            resizedBitmap,
                            (int) (resizedBitmap.getWidth() / scaleFactor),
                            (int) (resizedBitmap.getHeight() / scaleFactor),
                            true);
        }
        return resizedBitmap;
    }

    @Nullable
    private static ExifInterface getExifInterface(Context context, Uri uri) {
        try {
            String path = uri.toString();
            if (path.startsWith("file://")) {
                return new ExifInterface(path);
            }
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (path.startsWith("content://")) {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    return new ExifInterface(inputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static float getExifAngle(Context context, Uri selectedImageUri) {
        try {
            ExifInterface exifInterface = getExifInterface(context, selectedImageUri);
            if (exifInterface == null) {
                return -1f;
            }

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90f;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180f;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270f;
                case ExifInterface.ORIENTATION_NORMAL:
                    return 0f;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    return -1f;
                default:
                    return -1f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1f;
        }
    }
}

