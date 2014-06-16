package com.flukenetworks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
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
    Handler handler;

    public BluetoothServerThread(BluetoothAdapter adapter, Handler handler)
    {
        this.handler =  handler;
        this.adapter = adapter;
        this.uuid = UUID.fromString(MainActivity.UUID);

        try
        {
            ss = adapter.listenUsingInsecureRfcommWithServiceRecord(MainActivity.NAME, uuid);
            //ss = adapter.listenUsingRfcommWithServiceRecord(MainActivity.NAME, uuid);
            MainActivity.console.append("Now listening (name: " + MainActivity.NAME + ", uuid: " + MainActivity.UUID + ")\n");
        }
        catch(Exception e)
        {
            e.printStackTrace();
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
                BluetoothHandlerThread handlerThread = new BluetoothHandlerThread(adapter, s, handler);
                ss.close();
                ss = null;
                handlerThread.start();
                break;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}