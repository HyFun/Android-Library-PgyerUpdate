package com.sample.update;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hyfun.lib.pgyer.Pgyer;
import com.hyfun.lib.pgyer.PgyerSimpleDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private PgyerSimpleDialog simpleDialog;

    /**
     * 简单的dialog
     * @param view
     */
    public void simple(View view) {
        if (simpleDialog == null){
            simpleDialog = new PgyerSimpleDialog.Builder(this)
                    .cancel(true)
                    .content("这是一个简单的dialog...")
                    .build();
        }
        simpleDialog.show();
    }


    public void simplenew(View view) {
    }

    public void check(View view) {
        Pgyer.checkUpdate(this, true);
    }

}
