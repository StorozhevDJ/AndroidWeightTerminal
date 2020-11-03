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
import com.intex.weightterminal.services.BluetoothDataType;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private static final int BT_NOT_CONNECTED = 0;
    private static final int BT_CONNECTING = 1;
    private static final int BT_CONNECTED = 2;
    private static final int BT_DISABLED = 3;
    private static final int BT_NOT_SUPPORTED = 4;
    private static final int BT_DEVICE_NOT_FOUND = 5;

    private FirstFragment firstFragment;
    private MeasuredDataFragment measuredDataFragment;
    private FragmentTransaction fTrans;

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
            return true;
        }
        if (id == R.id.action_sensors_data) {
            btFindDeviceTask = new BtFindDeviceTask(bt);
            btFindDeviceTask.execute();
        }

        return super.onOptionsItemSelected(item);
    }



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
            if (bt == null) return;
            if (bt.isConnected()) {
                //ToDo add code
            }

        }
    }


    /**
     * Task for request measured weight and print to display
     */
    class BtGetWeightTask extends AsyncTask<Void, WeightModel, Void> {

        private Bluetooth bt;

        private Timer mTimer;
        private BtGetWeightTimerTask btGetWeightTimerTask;

        public BtGetWeightTask(Bluetooth bt) {
            this.bt = bt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTimer = new Timer();
            btGetWeightTimerTask = new BtGetWeightTimerTask(bt);
            //mTimer.schedule(mMyTimerTask, 1000);// singleshot delay 1000 ms
            mTimer.schedule(btGetWeightTimerTask, 5000, 1000);
        }

        @Override
        protected Void doInBackground(Void... arg) {
            while (true) {
                if (bt.getReceivedDataType() == BluetoothDataType.MEASURE_DATA) {
                    WeightModel weightModel = bt.getData(BluetoothDataType.MEASURE_DATA);
                    bt.clearReceivedDataType();
                    publishProgress(weightModel);
                }
                if (isCancelled()) return null;
            }
        }


        @Override
        protected void onProgressUpdate(WeightModel... values) {
            super.onProgressUpdate(values);
            TextView tvWeight = findViewById(R.id.tvWeightTotalValue);
            if (tvWeight != null)
                tvWeight.setText(String.format("%.02f", values[0].getWeightTotal()));
            TextView tvVolume = findViewById(R.id.tvVolumeValue);
            if (tvWeight != null) tvVolume.setText(String.format("%f", values[0].getVolume()));
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //TextView tvInfo = findViewById(R.id.textview_first);
            //if (tvInfo != null) tvInfo.setText("End");
        }
    }

    /**
     * Timer task for sending request to get measured data
     */
    class BtGetWeightTimerTask extends TimerTask {

        private Bluetooth bt;

        /*private int i = 0;//for test
        private String weightString = new String();*/

        public BtGetWeightTimerTask(Bluetooth bt) {
            this.bt = bt;
            try {
                bt.connect();
            } catch (WeightTerminalException e) {
                Log.d(TAG, "BT Connect FAILED: " + e.getError().name());
            }
        }

        @Override
        public void run() {

            if (bt.isConnected()) bt.requestMeasuredData();

            /*weightString = String.format("%.2f", (float) i++/100);

            final WeightModel w = bt.getData(BluetoothDataType.MEASURE_DATA);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    TextView tvWeight = findViewById(R.id.tvWeightTotalValue);
                    if (tvWeight != null) tvWeight.setText(weightString);
                    TextView tvVolume = findViewById(R.id.tvVolumeValue);
                    if (tvWeight != null) tvVolume.setText(String.format("%d", w.getVolume()));
                }
            });*/
        }
    }

}