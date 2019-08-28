package com.vadikfedchukgmail.imagerecognizer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.vadikfedchukgmail.imagerecognizer.R;

import java.io.File;

import static androidx.core.content.FileProvider.getUriForFile;

public class ChooseActionActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 100;
    private static final int REQUEST_CAMERA = 200;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickOpenGallery(View view) {
        Intent takeImageIntent = new Intent(Intent.ACTION_PICK);
        takeImageIntent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        takeImageIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(takeImageIntent, REQUEST_GALLERY);
    }

    public void onClickOpenCamera(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            } else {
                takePhoto();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, getString(R.string.error_permission_camera),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void takePhoto() {
        String fileName = System.currentTimeMillis() + ".jpg";
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = getCacheImagePath(fileName);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePhotoIntent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    startRecognizeImageActivity(imageUri);
                } else {
                    Toast.makeText(this, getString(R.string.error_pick_image), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    startRecognizeImageActivity(imageUri);
                } else {
                    Toast.makeText(this, getString(R.string.error_take_photo), Toast.LENGTH_SHORT).show();
                }
        }
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(this, getPackageName() + ".provider", image);
    }

    private void startRecognizeImageActivity(Uri uri) {
        Intent intent = new Intent(this, RecognizeImageActivity.class);
        intent.putExtra("imageUri", uri);
        startActivity(intent);
    }
}
