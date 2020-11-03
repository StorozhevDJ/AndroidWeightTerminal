package com.intex.weightterminal.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.google.gson.Gson;
import com.intex.weightterminal.dto.BluetoothIndicatorSettingsResponse;
import com.intex.weightterminal.dto.BluetoothWeightMeasureResponse;
import com.intex.weightterminal.exception.ErrorCode;
import com.intex.weightterminal.exception.WeightTerminalException;
import com.intex.weightterminal.mapper.MeasureDataMapper;

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
    private static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // BluetoothDevice - класс, ассоциирующийся с удаленным Bluetooth устройством.
    // Экземпляр этого класса используется для соединения через BluetoothSocket
    // или для запроса информации об удаленном устройстве (имя, адресс, класс, состояние).
    private static BluetoothDevice bluetoothDevice;

    // BluetoothSocket- интерфейс для Bluetooth socket, аналогичный TCP сокетам.
    // Это точка соединения, позволяющая обмениваться данными с удаленным устройством через InputStream и OutputStream.
    private BluetoothSocket bluetoothSocket;

    private InputStream mmInStream;
    private OutputStream mmOutStream;

    private String receivedJson;
    private BluetoothDataType bluetoothDataType = BluetoothDataType.NO_DATA;


    private static volatile Bluetooth instance;

    public Bluetooth() {
    }

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


    /**
     * Check the Bluetooth connection status of the device
     *
     * @return true if the device is connected
     */
    public boolean isConnected() {
        if (bluetoothSocket == null) return false;
        return bluetoothSocket.isConnected();
    }


    /**
     * Send this String data to the remote device
     */
    private void write(String message) {
        Log.d(TAG, "Sending data via bluetooth: " + message + "...");
        byte[] msgBuffer = message.getBytes();
        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) {
            Log.d(TAG, "Failed: " + e.getMessage() + "...");
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
                        if (bs == null) continue;
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


    /**
     * Connect to the finded paired device (open socket) via SPP
     *
     * @throws WeightTerminalException
     */
    public void connect() throws WeightTerminalException {
        try {
            Log.e(TAG, "socket Opening for SPP");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            Log.e(TAG, "socket Open successful");
        } catch (IOException e) {
            throw new WeightTerminalException(ErrorCode.BT_SOCKET_NOT_OPEN);
        }
        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        mBluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "Connecting...");
        try {
            bluetoothSocket.connect();
            Log.d(TAG, "Connected successful");
        } catch (IOException e) {
            try {
                bluetoothSocket.close();
            } catch (IOException e2) {
                throw new WeightTerminalException(ErrorCode.BT_CONNECT_FAILED);
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "Bluetooth device connected");
    }


    /**
     * Disconnect bluetooth device (close socket)
     *
     * @throws WeightTerminalException
     */
    public void disconnect() throws WeightTerminalException {
        try {
            bluetoothSocket.close();
            Log.e(TAG, "socket closed successful");
        } catch (IOException e) {
            Log.e(TAG, "socket close exception " + e.getMessage());
            throw new WeightTerminalException(ErrorCode.BT_SOCKET_NOT_CLOSED);
        }
    }


    /**
     * Bluetooth receive data task
     */
    public void receiveTask() {
        byte[] buffer = new byte[1024]; // buffer store for the stream
        int bytesRead;                  // bytes count returned from read()
        StringBuffer content = new StringBuffer();

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytesRead = mmInStream.read(buffer);    // Getting bytes count and receive message
                content.append(new String(buffer, 0, bytesRead, "utf-8"));
                if (buffer[bytesRead - 1] == 0) break;
            } catch (IOException e) {
                return;
            }
        }
        synchronized (receivedJson) {
            receivedJson = content.toString();
            setReceivedDataType();
        }
    }


    private void setReceivedDataType() {
        synchronized (receivedJson) {
            BluetoothWeightMeasureResponse resp = new Gson().fromJson(receivedJson, BluetoothWeightMeasureResponse.class);
            if (BluetoothDataType.MEASURE_DATA.name().equals(resp.getDataType())) {
                bluetoothDataType = BluetoothDataType.MEASURE_DATA;
            }
            if (BluetoothDataType.SETTINGS_DATA.name().equals(resp.getDataType())) {
                bluetoothDataType = BluetoothDataType.SETTINGS_DATA;
            }
        }
        bluetoothDataType = BluetoothDataType.NO_DATA;
    }


    public BluetoothDataType getReceivedDataType() {
        return bluetoothDataType;
    }


    public void clearReceivedDataType() {
        bluetoothDataType = BluetoothDataType.NO_DATA;
    }


    /**
     * Parse received JSON string to model data
     *
     * @param dataType - Type received data
     * @param <Type>   - Type returned data
     * @return received data
     */
    public <Type> Type getData(BluetoothDataType dataType) {
        synchronized (receivedJson) {
            switch (dataType) {
                case MEASURE_DATA:
                    return (Type) MeasureDataMapper.convertToEntity(new Gson().fromJson(receivedJson, BluetoothWeightMeasureResponse.class));
                case SETTINGS_DATA:
                    return (Type) new Gson().fromJson(receivedJson, BluetoothIndicatorSettingsResponse.class);
            }
        }
        return null;
    }


    /**
     * Send to bluetooth device the request measured data
     */
    public void requestMeasuredData() {
        write("cmd=" + BluetoothDataType.MEASURE_DATA.name() + "\r\n");
        bluetoothDataType = BluetoothDataType.NO_DATA;
    }
}
