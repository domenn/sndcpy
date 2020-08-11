package com.rom1v.sndcpynet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.BreakIterator;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE_PERMISSION_AUDIO = 1;
    private static final int REQUEST_CODE_START_CAPTURE = 2;
    private static final String SAVED_TEXT_KEY = "ip_KEYport";
    private static final String SAVED_R_TEXT_KEY = "reserveVIEV";
    private EditText textItemIp;
    private EditText textItemIpReserve;
    private boolean doesUpdates = false;

    // TextView textBoxId = findViewById(R.id.text_box_ip);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textItemIp = ((EditText) findViewById(R.id.text_box_ip));
        textItemIpReserve = ((EditText) findViewById(R.id.text_box_ip_reserve));
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        textItemIp.setText(pref.getString(SAVED_TEXT_KEY, "server:15004"));
        textItemIpReserve.setText(pref.getString(SAVED_R_TEXT_KEY, "server:15004"));

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(doesUpdates)
                {
                    pref.edit().putString(SAVED_TEXT_KEY, textItemIp.getText().toString()).commit();
                    pref.edit().putString(SAVED_R_TEXT_KEY, textItemIpReserve.getText().toString()).commit();
                }
            }
        };

        textItemIp.addTextChangedListener(tw);
        textItemIpReserve.addTextChangedListener(tw);
        doesUpdates = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        doesUpdates = false;
        outState.putString(SAVED_TEXT_KEY, textItemIp.getText().toString());
        outState.putString(SAVED_R_TEXT_KEY, textItemIpReserve.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        doesUpdates = false;
        textItemIp.setText(savedInstanceState.getString(SAVED_TEXT_KEY));
        textItemIpReserve.setText(savedInstanceState.getString(SAVED_R_TEXT_KEY));
        doesUpdates = true;
    }

    void recording() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.RECORD_AUDIO};
            requestPermissions(permissions, REQUEST_CODE_PERMISSION_AUDIO);
        }

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_CODE_START_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_START_CAPTURE && resultCode == Activity.RESULT_OK) {
            String ip = textItemIp.getText().toString();
            data.putExtra("ip", ip);
            RecordService.start(this, data);
        }
        //     finish();
    }

    public void button_click(View view) {
        recording();
    }

    public void button_click1(View view) {
        Intent intent = RecordService.createStopIntent(this);
        stopService(intent);
    }

    public void button_click2(View view) {
        doesUpdates = false;
        String one = textItemIp.getText().toString();
        textItemIp.setText(textItemIpReserve.getText());
        doesUpdates = true;
        textItemIpReserve.setText(one);
    }
}
