package com.flukenetworks;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;

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
    Handler handler;
    public static final String format = "SSS";

    public BluetoothHandlerThread(BluetoothAdapter adapter, BluetoothSocket s, Handler handler)
    {
        this.handler = handler;
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

    public double[] calculateStats(double data[])
    {
        double sum = 0;
        double min = data[0], max = data[0];

        for(int i = 0; i < data.length; i++)
        {
            sum += data[i];

            if(data[i] > 0 && data[i] < min)
            {
                min = data[i];
            }

            if(data[i] > max)
            {
                max = data[i];
            }
        }

        sum /= data.length;

        double stddev = 0;
        for(int i = 0; i < data.length; i++)
        {
            stddev += Math.pow((data[i] - sum), 2);
        }


        System.out.println("Mean over " + data.length + " samples: " + sum + " Mbps!");
        System.out.println("Std. Dev.: " + Math.sqrt((stddev / data.length)));
        System.out.println("MIN: " + min);
        System.out.println("MAX: " + max);

        /*Message msg = new Message();
        Bundle b = new Bundle();
        b.putDouble("LEN", data.length);
        b.putDouble("MEAN", sum);
        b.putDouble("STDDEV", stddev);
        b.putDouble("MIN", min);
        b.putDouble("MAX", max);
        msg.setData(b);
        handler.sendMessage(msg);*/


        double retval[] = {sum, Math.sqrt((stddev / data.length)), min, max};
        return retval;
    }

    public void run()
    {
        int count = 1;
        int len;
        double data[] = new double[50];
        byte buf[] = new byte[1024 * 1000 * 1];
        while(true)
        {
            try
            {
                len = 0;
                in.readInt();
                long start = System.currentTimeMillis();

                while(len < buf.length) {
                    len += in.read(buf);
                }

                double throughput = ((len)/((System.currentTimeMillis()-start)/1000.00)) * 8 / 1000.00 / 1000.00;
                if(count < data.length)
                {
                    Log.d("BT", "Test #" + count + "    " + throughput + " Mbps\n");
                    data[count++] = throughput;
                }
                else
                {
                    double retval[] = calculateStats(data);
                    out.writeDouble(retval[0]);
                    out.writeDouble(retval[1]);
                    out.writeDouble(retval[2]);
                    out.writeDouble(retval[3]);

                    System.exit(0);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                calculateStats(data);
                System.exit(0);
            }
        }
    }
}
