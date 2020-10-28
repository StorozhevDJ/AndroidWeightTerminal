package com.intex.weightterminal.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.intex.weightterminal.exception.ErrorCode;
import com.intex.weightterminal.exception.WeightTerminalException;
import com.intex.weightterminal.models.SettingsModel;
import com.intex.weightterminal.models.WeightModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Bluetooth {

    private static final String TAG = "Bluetooth";
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
    private BluetoothDevice bluetoothDevice;

    // BluetoothSocket- интерфейс для Bluetooth socket, аналогичный TCP сокетам.
    // Это точка соединения, позволяющая обмениваться данными с удаленным устройством через InputStream и OutputStream.
    private BluetoothSocket bluetoothSocket;

    private InputStream mmInStream;
    private OutputStream mmOutStream;


    private static volatile Bluetooth instance;

    public static Bluetooth getInstance() {
        Bluetooth localInstance = instance;
        if (localInstance == null) {
            synchronized (Bluetooth.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Bluetooth();
                    Log.d(TAG, "create new instance");
                }
            }
        }
        Log.d(TAG, "return instance");
        return localInstance;
    }


    public boolean isConnected() {
        if (bluetoothSocket == null) return false;
        return bluetoothSocket.isConnected();
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }


    /**
     * Send this String data to the remote device
     */
    private void write(String message) {
        Log.d(TAG, "...Данные для отправки: " + message + "...");
        byte[] msgBuffer = message.getBytes();
        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) {
            Log.d(TAG, "...Ошибка отправки данных: " + e.getMessage() + "...");
        }
    }


    /**
     * Find paired BlueTooth device
     */
    public void findDevice() throws WeightTerminalException {
        bluetoothSocket = null;
        bluetoothDevice = null;
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Bluetooth not supported");
            throw new WeightTerminalException(ErrorCode.BT_NOT_SUPPORTED);
        }
        // Is Bluetooth turn off? Request to user to enable.
        if (!mBluetoothAdapter.isEnabled()) {
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
                        bluetoothDevice = device;
                        bluetoothSocket = bs;
                        mmInStream = bs.getInputStream();
                        mmOutStream = bs.getOutputStream();
                        Log.d(TAG, device.getAddress() + " " + device.getName() + " - connected");
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


    public WeightModel[] getWeight() throws WeightTerminalException {

        return null;
    }


    public SettingsModel getSettings() throws WeightTerminalException {

        return null;
    }

    public void saveSettings(SettingsModel settingsModel) throws WeightTerminalException {

        return;
    }
}
