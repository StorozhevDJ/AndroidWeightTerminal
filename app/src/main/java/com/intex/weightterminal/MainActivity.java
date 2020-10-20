package com.intex.weightterminal;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.intex.weightterminal.exception.WeightTerminalException;
import com.intex.weightterminal.services.Bluetooth;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private static final int BT_NOT_CONNECTED = 0;
    private static final int BT_CONNECTING = 1;
    private static final int BT_CONNECTED = 2;
    private static final int BT_DISABLED = 3;
    private static final int BT_NOT_SUPPORTED = 4;
    private static final int BT_DEVICE_NOT_FOUND = 5;


    private Handler h;
    private Handler btConnectHandler;

    private Bluetooth bt = new Bluetooth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        h = testHandler();
        h.sendEmptyMessage(0);

        btConnectHandler = bluetoothConnectHandler();
        btConnectHandler.sendEmptyMessage(BT_NOT_CONNECTED);

        //Thread t = bluetoothConnectThread();
        //t.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_display) {
            Log.d(TAG, "Menu Setting clicked");
            Thread t = testThread();
            t.start();
            return true;
        }
        if (id == R.id.action_sensors_data) {
            Thread t = bluetoothConnectThread();
            t.start();
        }

        return super.onOptionsItemSelected(item);
    }


    private Handler testHandler() {
        return new Handler() {
            public void handleMessage(android.os.Message msg) {
                TextView textView = findViewById(R.id.textview_first);
                if (textView == null) return;
                if (msg == null) return;
                switch (msg.what) {
                    case 0:
                        textView.setText("Not connected");
                        break;
                    case 1:
                        textView.setText("Connecting");
                        break;
                    case 2:
                        textView.setText("Connected");
                        break;
                }
            }
        };
    }


    private Thread testThread() {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    // устанавливаем подключение
                    h.sendEmptyMessage(1);
                    TimeUnit.SECONDS.sleep(2);

                    // установлено
                    h.sendEmptyMessage(2);

                    // выполняется какая-то работа
                    TimeUnit.SECONDS.sleep(3);

                    // разрываем подключение
                    h.sendEmptyMessage(0);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private Handler bluetoothConnectHandler() {
        return new Handler() {
            public void handleMessage(@NonNull android.os.Message msg) {
                TextView textView = findViewById(R.id.textview_first);
                if (textView == null) return;
                if (msg == null) return;
                switch (msg.what) {
                    case BT_NOT_CONNECTED:
                        textView.setText(getString(R.string.bt_not_connected));
                        break;
                    case BT_CONNECTING:
                        textView.setText(getString(R.string.bt_connecting));
                        break;
                    case BT_CONNECTED:
                        textView.setText(getString(R.string.bt_connected));
                        break;
                    case BT_DISABLED:
                        textView.setText(getString(R.string.bt_disabled));
                        break;
                    case BT_NOT_SUPPORTED:
                        textView.setText(getString(R.string.bt_not_supported));
                        break;
                    case BT_DEVICE_NOT_FOUND:
                        textView.setText(getString(R.string.bt_device_not_found));
                        break;
                }
            }
        };
    }


    private Thread bluetoothConnectThread() {
        return new Thread(new Runnable() {
            public void run() {
                // Start connecting
                btConnectHandler.sendEmptyMessage(BT_CONNECTING);
                try {
                    bt.connect();
                    btConnectHandler.sendEmptyMessage(BT_CONNECTED);
                } catch (WeightTerminalException e) {
                    //Bluetooth NOT connected
                    switch (e.getError()) {
                        case BT_DISABLED:
                            btConnectHandler.sendEmptyMessage(BT_DISABLED);
                            break;
                        case BT_NOT_SUPPORTED:
                            btConnectHandler.sendEmptyMessage(BT_NOT_SUPPORTED);
                            break;
                        case BT_DEVICE_NOT_FOUND:
                            btConnectHandler.sendEmptyMessage(BT_DEVICE_NOT_FOUND);
                            break;
                    }
                }
            }
        });
    }
}