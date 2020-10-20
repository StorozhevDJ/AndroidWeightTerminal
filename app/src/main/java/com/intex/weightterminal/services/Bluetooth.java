package com.intex.weightterminal.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.intex.weightterminal.exception.ErrorCode;
import com.intex.weightterminal.exception.WeightTerminalException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Bluetooth {

    private final String TAG = this.getClass().getSimpleName();
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final static int REQUEST_ENABLE_BT = 1;

    // BluetoothAdapter - отвечает за работу с установленным в телефоне Bluetooth модулем.
    // Экземпляр этого класса есть в любой программе, использующей bluetooth.
    // В состав этого класса входят методы, позволяющие производить поиск доступных устройств,
    // запрашивать список подключенных устройств, создавать экземпляр класса BluetoothDevice
    // на основании известного MAC адреса и создавать BluetoothServerSocket
    // для ожидания запроса на соединение от других устройств.
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // BluetoothDevice - класс, ассоциирующийся с удаленным Bluetooth устройством.
    // Экземпляр этого класса используется для соединения через BluetoothSocket
    // или для запроса информации об удаленном устройстве (имя, адресс, класс, состояние).
    private BluetoothDevice device;

    // BluetoothSocket- интерфейс для Bluetooth socket, аналогичный TCP сокетам.
    // Это точка соединения, позволяющая обмениваться данными с удаленным устройством через InputStream и OutputStream.
    private BluetoothSocket bluetoothSocket;

    private boolean connected = false;


    /**
     * Connect to paired BlueTooth device
     */
    public void connect() throws WeightTerminalException {
        boolean connected = false;
        connected = false;
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Bluetooth not supported");
            throw new WeightTerminalException(ErrorCode.BT_NOT_SUPPORTED);
        }
        // Is Bluetooth turn off? Request to user to enable.
        if (!mBluetoothAdapter.isEnabled()) {
            //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.d(TAG, "Bluetooth disabled");
            throw new WeightTerminalException(ErrorCode.BT_DISABLED);
        }
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            try {
                try {
                    Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    try {
                        BluetoothSocket bs = (BluetoothSocket) m.invoke(device, 1);
                        if (!"WEIGHT-TERMINAL".equals(device.getName())) continue;
                        bs.connect();
                        connected = false;
                        Log.d(TAG, device.getAddress() + " " + device.getName() + " - connected");
                        connected = true;
                        return;
                    } catch (IOException e) {
                        Log.e(TAG, "IOException: " + e.getLocalizedMessage());
                        Log.d(TAG, device.getName() + " - not connected");
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "IllegalArgumentException: " + e.getLocalizedMessage());
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "IllegalAccessException: " + e.getLocalizedMessage());
                } catch (InvocationTargetException e) {
                    Log.e(TAG, "InvocationTargetException: " + e.getLocalizedMessage());
                }
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException: " + e.getLocalizedMessage());
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException: " + e.getLocalizedMessage());
            }
        }
        throw new WeightTerminalException(ErrorCode.BT_DEVICE_NOT_FOUND);
    }


    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }


}
