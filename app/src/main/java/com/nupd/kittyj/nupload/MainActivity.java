package com.nupd.kittyj.nupload;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
        Manifest.permission.INTERNET);

        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    handleRecievedImage(intent);
                }
            }
        }
    }

    private void handleRecievedImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            Log.v("Image Uri", imageUri.toString());
            String type = getMimeType(imageUri);
            Log.v("Type: ", type);


        }
    }

    private String getMimeType(Uri uri) {
        String mimeType = null;

        if (uri != null) {
            ContentResolver contentResolver = getContentResolver();
            mimeType = contentResolver.getType(uri);
        }

        return mimeType;
    }

    private File createFileFromUri(Uri uri) {
        File file = null;

        if (uri != null) {
            file = new File(uri.getPath());
        }

        return file;
    }
}
