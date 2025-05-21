package com.redemastery.oldapi.pojav;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.redemastery.launcher.R;

public class MissingStorageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_test_no_sdcard);
    }
}