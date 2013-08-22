package org.msgpack.androidtestApp;

import java.net.UnknownHostException;

import org.msgpack.rpc.Client;
import org.msgpack.rpc.loop.EventLoop;

import pl.micwi.testmsgpack.ServerConstants;
import pl.micwi.testmsgpack.TestServerIFace;
import pl.micwi.testmsgpack.data.DataParent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class HelloAndroidActivity extends Activity {



	private static String TAG = "msgpack-android-test-client";
	
	
	private static final String SERVER_HOST = "msgpack_test_host";
	
	private static final String PREFS_FILE = "msgpack_test_prefs";
	
	private TestServerIFace server;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //FIX for API level 8 bug, as in:
        //http://code.google.com/p/android/issues/detail?id=9431
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        
		Log.i(TAG, "onCreate");
        setContentView(R.layout.main);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	//init dialog template
		final EditText input = new EditText(this);

		//set with old host preference
		final SharedPreferences prefs = getSharedPreferences(PREFS_FILE,MODE_PRIVATE);
		final String oldHost = prefs.getString(SERVER_HOST, "");
		input.setText(oldHost);
		
		//show host dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setView(input).setMessage("IP?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String host = input.getText().toString().trim();
				
				if(!host.equals(oldHost)) {
					prefs.edit().putString(SERVER_HOST, host).commit();
				}
				
		    	new ConnectThread(host).start();
			}
		});

		builder.create().show();

    }
    
    
    private final class ConnectThread extends Thread {
    	
    	private String host;

		public ConnectThread(String host) {
    		this.host = host;
    	}
    	
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		    EventLoop loop = EventLoop.defaultEventLoop();
		    
		    Client cli;
			try {
				cli = new Client(host, ServerConstants.SERVER_PORT, loop);
		        server = cli.proxy(TestServerIFace.class);
		        System.out.println("Connected!");
			} catch (UnknownHostException e) {
				System.out.println((e.getLocalizedMessage()+"\n"+e.getStackTrace()));
			}
			
			DataParent data = new DataParent();
			
			data.setSomeString("BLAHPARENT");
			
			server.send(data);
			server.shutdown();
		}
	}

}

