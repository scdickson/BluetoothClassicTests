package com.flukenetworks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.bluetooth.*;
import android.widget.TextView;


public class MainActivity extends Activity
{
    BluetoothAdapter adapter;
    BluetoothServerThread serverThread;
    public static TextView console;
    Context context;

    public final Handler handler = new Handler(){
        public void handleMessage(Message msg)
        {
            double len = msg.getData().getDouble("LEN");
            double mean = msg.getData().getDouble("MEAN");
            double stddev = msg.getData().getDouble("STDDEV");
            double min = msg.getData().getDouble("MIN");
            double max = msg.getData().getDouble("MAX");


            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Results for " + len + " Samples:");
            alertDialog.setMessage("Mean Throughput: " + mean + " Mbps\nStd. Dev.: " + Math.sqrt((stddev / len)) + "\nMin: " + min + "\nMax: " + max);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
// here you can add functions
                    alertDialog.cancel();
                }
            });
            alertDialog.show();
        }
    };

    public static final String UUID = "749838C9-6A99-4D61-A6CC-5A2B0F833C15";
    public static final String NAME = "SCD_SERVER";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        console = (TextView) findViewById(R.id.console);
        context = this;

        adapter = BluetoothAdapter.getDefaultAdapter();

        if(adapter != null)
        {
            if(adapter.isEnabled())
            {
                console.append("Bluetooth is enabled (MAC: " + adapter.getAddress() + ")\n");
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,0);
                startActivity(discoverableIntent);
                serverThread = new BluetoothServerThread(adapter, handler);
                serverThread.start();
            }
            else
            {
                console.append("Bluetooth is disabled\n");
            }
        }
        else
        {
            console.append("Bluetooth is not supported\n");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public static void updateConsole(String line)
    {
        console.append(line + "\n");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
