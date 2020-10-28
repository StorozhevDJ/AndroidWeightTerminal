package com.intex.weightterminal;

import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.intex.weightterminal.exception.WeightTerminalException;
import com.intex.weightterminal.fragments.FirstFragment;
import com.intex.weightterminal.fragments.MeasuredDataFragment;
import com.intex.weightterminal.models.WeightModel;
import com.intex.weightterminal.services.Bluetooth;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private static final int BT_NOT_CONNECTED = 0;
    private static final int BT_CONNECTING = 1;
    private static final int BT_CONNECTED = 2;
    private static final int BT_DISABLED = 3;
    private static final int BT_NOT_SUPPORTED = 4;
    private static final int BT_DEVICE_NOT_FOUND = 5;


    //private Handler h;
    //private Handler btConnectHandler;

    private FirstFragment firstFragment;
    private MeasuredDataFragment measuredDataFragment;
    private FragmentTransaction fTrans;

    private Timer mTimer;

    private Bluetooth bt = Bluetooth.getInstance();
    private static BtFindDeviceTask btFindDeviceTask;
    private static BtGetWeightTask btGetWeightTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firstFragment = new FirstFragment();
        measuredDataFragment = new MeasuredDataFragment();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //h = testHandler();
        //h.sendEmptyMessage(0);

        //btConnectHandler = bluetoothConnectHandler();
        //btConnectHandler.sendEmptyMessage(BT_NOT_CONNECTED);

        //Thread t = bluetoothConnectThread();
        //t.start();

        btFindDeviceTask = new BtFindDeviceTask(bt);
        btFindDeviceTask.execute();

        btGetWeightTask = new BtGetWeightTask(bt);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }


    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState) {
        super.onRestoreInstanceState(saveInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
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
        fTrans = getFragmentManager().beginTransaction();
        Log.d(TAG, "onOptionsItemSelected " + id);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_display) {
            Log.d(TAG, "Menu Setting clicked");
            //Thread t = testThread();
            //t.start();
            //FragmentTransaction tran = getFragmentManager().beginTransaction();
            //tran.replace(R.id.FirstFragment, R.id.MeasuredDataFragment/*new MeasuredDataFragment()*/);
            //NavHostFragment.findNavController(firstFragment).navigate(R.id.action_FirstFragment_to_SecondFragment);
            //Fragment yoursFragment=new MeasuredDataFragment();
            // fTrans.replace(R.id.nav_host_fragment, yoursFragment);
            return true;
        }
        if (id == R.id.action_sensors_data) {
            /*Thread t = bluetoothConnectThread();
            t.start();*/
            btFindDeviceTask = new BtFindDeviceTask(bt);
            btFindDeviceTask.execute();
        }

        return super.onOptionsItemSelected(item);
    }


    /*private Handler testHandler() {
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
    }*/


    /*private Handler bluetoothConnectHandler() {
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
    }*/


    class BtFindDeviceTask extends AsyncTask<Void, Integer, Void> {

        private final String TAG = this.getClass().getSimpleName();

        private final static int REQUEST_ENABLE_BT = 1;

        private Bluetooth bt;

        public BtFindDeviceTask(Bluetooth bt) {
            this.bt = bt;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute");
            super.onPreExecute();
            TextView tvInfo = findViewById(R.id.tvConnectStatus);
            if (tvInfo != null) tvInfo.setText(getString(R.string.bt_connecting));
        }

        @Override
        protected Void doInBackground(Void... arg) {
            Log.d(TAG, "doInBackground");
            try {
                bt.findDevice();
                publishProgress(BT_CONNECTED);
                Log.d(TAG, "BT Connected");
            } catch (WeightTerminalException e) {
                Log.d(TAG, "BT Not connected with err. code " + e.getError().name());
                //Bluetooth NOT connected
                switch (e.getError()) {
                    case BT_DISABLED:
                        publishProgress(BT_DISABLED);
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        break;
                    case BT_NOT_SUPPORTED:
                        publishProgress(BT_NOT_SUPPORTED);
                        break;
                    case BT_DEVICE_NOT_FOUND:
                        publishProgress(BT_DEVICE_NOT_FOUND);
                        break;
                }
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "onProgressUpdate " + values[0]);
            super.onProgressUpdate(values);
            TextView tvInfo = findViewById(R.id.tvConnectStatus);
            Button button = findViewById(R.id.button_settings);
            if (tvInfo == null) return;
            switch (values[0]) {
                case BT_NOT_CONNECTED:
                    tvInfo.setText(getString(R.string.bt_not_connected));
                    button.setEnabled(false);
                    break;
                case BT_CONNECTING:
                    tvInfo.setText(getString(R.string.bt_connecting));
                    break;
                case BT_CONNECTED:
                    tvInfo.setText(getString(R.string.bt_connected));
                    button.setEnabled(true);
                    break;
                case BT_DISABLED:
                    tvInfo.setText(getString(R.string.bt_disabled));
                    button.setEnabled(false);
                    break;
                case BT_NOT_SUPPORTED:
                    tvInfo.setText(getString(R.string.bt_not_supported));
                    button.setEnabled(false);
                    break;
                case BT_DEVICE_NOT_FOUND:
                    tvInfo.setText(getString(R.string.bt_device_not_found));
                    button.setEnabled(true);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "onPreExecute");
            super.onPostExecute(result);
            /*TextView tvInfo = findViewById(R.id.tvConnectStatus);
            if (tvInfo != null) tvInfo.setText("End");*/
            if (bt == null) return;
            if (bt.isConnected()) {

            }
        }
    }


    class BtGetWeightTask extends AsyncTask<Void, WeightModel[], Void> {

        private Bluetooth bt;

        public BtGetWeightTask(Bluetooth bt) {
            this.bt = bt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView tvInfo = findViewById(R.id.textview_first);
            if (tvInfo != null) tvInfo.setText(getString(R.string.bt_connecting));
        }

        @Override
        protected Void doInBackground(Void... arg) {
            try {
                WeightModel[] weightModel = bt.getWeight();
                publishProgress(weightModel);
            } catch (WeightTerminalException e) {
                Log.e(TAG, "Get weight error: " + e.getError().name());
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(WeightModel[]... values) {
            super.onProgressUpdate(values);
            TextView tvInfo = findViewById(R.id.textview_first);
            if (tvInfo == null) return;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            /*TextView tvInfo = findViewById(R.id.textview_first);
            if (tvInfo != null) tvInfo.setText("End");*/

        }
    }


}