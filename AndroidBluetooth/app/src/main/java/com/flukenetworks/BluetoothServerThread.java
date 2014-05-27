package com.flukenetworks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by sam on 5/23/14.
 */
public class BluetoothServerThread extends Thread
{
    BluetoothAdapter adapter;
    BluetoothServerSocket ss;
    UUID uuid;

    public BluetoothServerThread(BluetoothAdapter adapter)
    {
        this.adapter = adapter;
        this.uuid = UUID.fromString(MainActivity.UUID);

        try
        {
            //ss = adapter.listenUsingRfcommWithServiceRecord(MainActivity.NAME, uuid);
            ss = adapter.listenUsingInsecureRfcommWithServiceRecord(MainActivity.NAME, uuid);
            MainActivity.console.append("Now listening (name: " + MainActivity.NAME + ", uuid: " + MainActivity.UUID + ")\n");
        }
        catch(Exception e)
        {
            MainActivity.console.append(e.getMessage());
        }
    }

    public void run()
    {
        BluetoothSocket s;

        while(true)
        {
            try
            {
                s = ss.accept();
                Log.d("BT", "Accepted connection from " + s.getRemoteDevice().getName() + " (" + s.getRemoteDevice().getAddress() + ")");
                BluetoothHandlerThread handlerThread = new BluetoothHandlerThread(adapter, s);
                ss.close();
                ss = null;
                handlerThread.start();
                break;
            }
            catch (Exception e)
            {
                MainActivity.console.append(e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
