package com.nupd.kittyj.nupload;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

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
            Log.v("Image Uri", imageUri.getPath());

            Log.v("Type: ", getMimeType(imageUri));

            postFile(imageUri);
            finish();
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
        String realPath = getRealPathFromURI(uri);
        File file = null;

        if (uri != null) {
            file = new File(realPath);
        }

        return file;
    }

    private String getRealPathFromURI(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }

    public String parseNupResponse(String response) {
        Pattern pattern = Pattern.compile("(https://nup.pw/[a-zA-Z0-9]+.[a-zA-Z]+)");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public void postFile(Uri uri) {
        File file = createFileFromUri(uri);
        RequestParams requestParams = new RequestParams();

        try {
            requestParams.put("filename", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        NupClient.post("/", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("FAIL: ", "Hejhej");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.v("SUCCESS: ", responseString);
                parseNupResponse(responseString);
                Toast.makeText(MainActivity.this, parseNupResponse(responseString), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "Pls, no permission = no upload!", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }
}
