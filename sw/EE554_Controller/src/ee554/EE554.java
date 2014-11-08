/*
  MultiColorLamp - Example to use with Amarino
  Copyright (c) 2009 Bonifaz Kaufmann. 
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package ee554;

import java.io.File;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import at.abraxas.amarino.Amarino;
import ee554.controller.R;

public class EE554 extends Activity implements OnSeekBarChangeListener, OnClickListener {
	

	private static final String TAG = "EE554";
	
	private static final String DEVICE_ADDRESS_MICRO = "00:06:66:46:5D:94";
	
	//private static final String DEVICE_ADDRESS_PHONE = "40:98:4E:B6:FB:06";
	//private static final String DEVICE_ADDRESS_PHONE = "A8:26:D9:01:34:B1";	
	
	//private BluetoothChatService mChatService = null;
	
	SeekBar forwardbackward, leftright;
	ImageView image;
	Button buttonFlash, buttonMode1, buttonMode2, buttonMode3;

	BluetoothAdapter mBluetoothAdapter = null;
	BluetoothDevice mBluetoothDevice;
	
	int stateLeftRight, stateForwardBackward;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        forwardbackward = (SeekBar) findViewById(R.id.SeekBarForwardBackward);
        leftright = (SeekBar) findViewById(R.id.SeekBarLeftRight);
        image = (ImageView) findViewById(R.id.imageView1);
        buttonFlash = (Button) findViewById(R.id.flash);
        buttonMode1 = (Button) findViewById(R.id.mode1);
        buttonMode2 = (Button) findViewById(R.id.mode2);
        buttonMode3 = (Button) findViewById(R.id.mode3);
        
		//fb = (EditText) findViewById(R.id.stateFB);
		//lr = (EditText) findViewById(R.id.stateLR);
		
        // register listeners
		forwardbackward.setOnSeekBarChangeListener(this);
		leftright.setOnSeekBarChangeListener(this);
		buttonFlash.setOnClickListener(this);
		buttonMode1.setOnClickListener(this);
		buttonMode2.setOnClickListener(this);
		buttonMode3.setOnClickListener(this);

		image.setImageResource(R.drawable.btn_square_overlay_normal);

        // connect to Amarino
		Amarino.connect(this, DEVICE_ADDRESS_MICRO);
		
		// connect to phone
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS_PHONE);
       
    }
          
    public void onClick(View v)
    {
    	if (v == buttonFlash)
    	{
    		byte[] out = {0x01, 0x02};
    		//mChatService.write(out);
    		Log.d(TAG, "toggle flash" + out[0] + out[1]);
    	}
    	if (v == buttonMode1)
    	{
    		Amarino.sendDataToArduino(this, DEVICE_ADDRESS_MICRO, 'm', 1);
    		Log.d(TAG, "Mode 1");
    	}
    	if (v == buttonMode2)
    	{
    		Amarino.sendDataToArduino(this, DEVICE_ADDRESS_MICRO, 'm', 2);
    		Log.d(TAG, "Mode 2");
    	}
    	if (v == buttonMode3)
    	{
    		Amarino.sendDataToArduino(this, DEVICE_ADDRESS_MICRO, 'm', 3);
    		Log.d(TAG, "Mode 3");
    	}
    }
   
    
	@Override
	protected void onStart() {
		super.onStart();
		
		stateForwardBackward = 5;
		forwardbackward.setProgress(stateForwardBackward);

		stateLeftRight = 5;		
		leftright.setProgress(stateLeftRight);

        new Thread(){
        	public void run(){
        		try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {}
				Log.d(TAG, "init state");
				updateState();
        	}
        }.start();
        
        // Initialize the BluetoothChatService to perform bluetooth connections
        //mChatService = new BluetoothChatService(this);
        //mChatService.connect(mBluetoothDevice);
        
        File imgFile = new File ("/sdcard/image.jpg");
        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
    	image.setImageBitmap(bm);
        
        /*
        new Thread(){
        	File imgFile = new File ("/sdcard/image.jpg");
        	public void run(){
	        	Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	        	image.setImageBitmap(bm);
        	}
        }.start();
        */

     }


	@Override
	protected void onStop() {
		super.onStop();

		// stop Amarino's background service, we don't need it any more 
		Amarino.disconnect(this, DEVICE_ADDRESS_MICRO);
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        //if (mChatService != null) mChatService.stop();
        Log.d(TAG, "--- ON DESTROY ---");
    }
	

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		updateState();
	}

		

	private void updateState() {
		
		stateForwardBackward = forwardbackward.getProgress();
		stateLeftRight = leftright.getProgress();
		
		Log.d(TAG, "update state");
		Log.d(TAG, String.format("    lr: %d",stateLeftRight));
		Log.d(TAG, String.format("    fb: %d",stateForwardBackward));
		
		// Send left/right
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS_MICRO, 'a', stateLeftRight);
		
		// Send forward/backward
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS_MICRO, 'd', stateForwardBackward);
		
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	
}