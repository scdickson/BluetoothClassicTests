package com.flukenetworks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sam on 5/23/14.
 */
public class BluetoothHandlerThread extends Thread
{
    BluetoothAdapter adapter;
    BluetoothSocket s;
    DataInputStream in;
    DataOutputStream out;
    Calendar calendar;
    public static final String format = "SSS";

    public BluetoothHandlerThread(BluetoothAdapter adapter, BluetoothSocket s)
    {
        this.adapter = adapter;
        this.s = s;
        this.calendar = Calendar.getInstance();

        try
        {
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
        }
        catch(Exception e)
        {
            MainActivity.console.append(e.getMessage());
        }
    }

    public void run()
    {
        int count = 1;
        while(true)
        {
            try
            {
                //String data = in.readUTF();
                byte data[] = new byte[2048];
                in.read(data);
                //Log.d("BT", count++ + "-> Read " + data.length + " bytes: " + System.currentTimeMillis());//+ data);
                Log.d("BT", System.currentTimeMillis() + "");
                //MainActivity.updateConsole("Read " + read + " bytes: " + String.valueOf(buf));

            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
