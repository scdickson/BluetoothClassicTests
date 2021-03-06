import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.bluetooth.*;
import javax.microedition.io.*;
import com.intel.bluetooth.BluetoothConnectionAccess;

public class BT 
{
	public native void wscan();
	static DiscoveryAgent agent;
	static DiscoveryListener listener;
	static ServiceRecord service;
	static int num_samples = 50;

	public static void main(String args[]) throws IOException 
	{
		final Object inquiryCompletedEvent = new Object();
		agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
		
		listener = new DiscoveryListener()
		{
			@Override
			public void deviceDiscovered(RemoteDevice arg0, DeviceClass arg1) {
				try
				{
					System.err.println("Found device: " + arg0.getFriendlyName(false) + " (" + arg0.getBluetoothAddress() + ")");
					if(arg0.getBluetoothAddress().equals("AC220B622390"))
					{
						UUID[] UUIDs = {new UUID("749838C96A994D61A6CC5A2B0F833C15", false)};
						int[] attr = null;
						System.err.println("Found it! Searching for services now...");
						agent.searchServices(attr, UUIDs, arg0, listener);
					}
				}
				catch(Exception e)
				{
					try
					{
						System.err.println("Found device: [unknown device] (" + arg0.getBluetoothAddress() + ")");
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}

			@Override
			public void inquiryCompleted(int arg0) {
				System.err.println("Inquiry finished.");
			}

			@Override
			public void serviceSearchCompleted(int arg0, int arg1) {
				
				
				StreamConnection conn = null;
				try
				{
					//conn = (StreamConnection) Connector.open(service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
					conn = (StreamConnection) Connector.open(service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, true));
					DataOutputStream dos = new DataOutputStream(conn.openOutputStream());
					DataInputStream dis = new DataInputStream(conn.openInputStream());
					
					Random r = new Random(System.currentTimeMillis());
					byte buf[] = new byte[1024 * 1000];

					for(int i = 0; i < num_samples; i++)
					{
						System.out.println("Write #" + i);
						r.nextBytes(buf);
						dos.writeInt(1);
						dos.write(buf);
					}

					double mean = dis.readDouble();
					double stddev = dis.readDouble();
					double min = dis.readDouble();
					double max = dis.readDouble();

					System.out.println("Mean over " + num_samples + " samples: " + mean + " Mbps!");
					System.out.println("Std. Dev.: " + stddev);
					System.out.println("MIN: " + min);
					System.out.println("MAX: " + max);

					dos.close();
					dis.close();
					conn.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				synchronized(inquiryCompletedEvent){
					inquiryCompletedEvent.notifyAll();
				}
			}

			@Override
			public void servicesDiscovered(int arg0, ServiceRecord[] arg1) 
			{
				service = arg1[0];
				System.err.println("Discovered Service: " + arg1[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, true));
			}
			
		};
		
		synchronized(inquiryCompletedEvent)
		{
			try
			{
				boolean started = agent.startInquiry(DiscoveryAgent.GIAC, listener);
				
				if(started)
				{
					System.err.println("Inquiry...");
					inquiryCompletedEvent.wait();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
