package net.sonma.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import net.sonma.sdk.android.SDK;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {

    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void print(View view) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                final JSONObject result = SDK.getInstance().print(123456789, "{\"message\":\"print test\"}", 10086L, null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, String.valueOf(result), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
