/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.BluetoothChat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChat extends Activity implements SensorEventListener {
	// Debugging
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// Layout Views
	private TextView mTitle;
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;

	// Globals
	Preview preview;
	ToggleButton lightBtn;
	Button cameraBtn;
	Button compassBtn;
	Button accelBtn;
	Button gpsBtn;
	TextView postText;
	SensorManager mSensorManager = null;
	LocationManager locationManager = null;
	float acc_X, acc_Y, acc_Z, com_X, com_Y, com_Z;
	int camera_flag = 0, compass_flag = 0, accel_flag = 0, gps_flag = 0, camera_init = 0, light_flag = 0;
	Thread camera_thread = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	public void buttonActions() {
		lightBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (lightBtn.isChecked()) {
					lightBtn.setChecked(true);
					Camera.Parameters camParam = preview.camera.getParameters();
					camParam.setFlashMode("torch");
					preview.camera.setParameters(camParam);
					light_flag = 1;
				} else {
					Camera.Parameters camParam = preview.camera.getParameters();
					camParam.setFlashMode("off");
					preview.camera.setParameters(camParam);
					light_flag = 0;
				}
			}
		});
		cameraBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				camera_flag = (camera_flag == 0) ? 1 : 0;
/*				
				if (camera_flag == 1 && camera_thread == null && camera_init == 0) {
					camera_thread = new Thread() {
					    @Override
					    public void run() {
					    	while (true) {
						        preview.camera.autoFocus(new AutoFocusCallback() {
									 @Override 
									 public void onAutoFocus(boolean success, Camera camera) { 
									 Camera.Parameters camParam = camera.getParameters();
									 camParam.setZoom(0);
									 camParam.setFocusMode(Parameters.FOCUS_MODE_AUTO);
									 camera.setParameters(camParam); 
									 }
								 });
								 preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback); 
								 try { Thread.sleep(1000); }
								 catch (Exception e) {}
					    	}
					    }
					};
					camera_thread.start();
					camera_init = 1;
				}
*/				
/*				
				if (camera_flag == 0) {// && camera_thread != null) {
					Thread temp_thread = camera_thread;
				    camera_thread = null;
				    temp_thread.interrupt();
				}	
*/
				preview.camera.autoFocus(new AutoFocusCallback() {
					 @Override 
					 public void onAutoFocus(boolean success, Camera camera) { 
					 Camera.Parameters camParam = camera.getParameters();
					 camParam.setZoom(0);
					 camParam.setPictureSize(150, 150);
					 camParam.setFocusMode(Parameters.FOCUS_MODE_AUTO);
					 camera.setParameters(camParam); 
					 }
				 });
				 preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback); 
			}
		});
		compassBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				compass_flag = 1;
				accel_flag = 0;
				gps_flag = 0;
			}
		});
		accelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				accel_flag = 1;
				compass_flag = 0;
				gps_flag = 0;
			}
		});
		gpsBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				gps_flag = 1;
				compass_flag = 0;
				accel_flag = 0;
				LocationListener ll = new mylocationlistener();
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, ll);
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0, ll);
			}
		});
	}

	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			byte[] dataStream = new byte[150000];
			int counter = 0;
			try {
				/*
				data [0] = 0x01;
				data [1] = 0x02;
				data [2] = 0x03;
				data [3] = 0x04;
				data [4] = 0x05;
				*/
				// add start bytes 0x0012
				dataStream[counter++] = 0x00;
				dataStream[counter++] = 0x12;

				// for each data byte
				for (int i = 0; i < data.length; i++) {
					dataStream[counter++] = (byte) 0xfe; // add 0xFF
					dataStream[counter++] = data[i]; // add data byte
				}

				// add end bytes 0x0023
				dataStream[counter++] = 0x00;
				dataStream[counter++] = 0x23;
				
				
				// send data
				sendMessage(dataStream);
				
				// Write to SD Card
				// outStream = new FileOutputStream(String.format("/sdcard/RC-image.jpg"));
				// outStream.write(data);
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

	// Called when shutter is opened
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	class mylocationlistener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				double pLong = location.getLongitude();
				double pLat = location.getLatitude();
				if (gps_flag == 1) {
					postText.setText("Lat, Long:  " + Double.toString(pLat)
							+ ",  " + Double.toString(pLong));
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				acc_X = event.values[0];
				acc_Y = event.values[1];
				acc_Z = event.values[2];
				if (accel_flag == 1) {
					postText.setText("Accel (x, y, z):  " + acc_X + ", "
							+ acc_Y + ", " + acc_Z);
				}
				break;
			case Sensor.TYPE_ORIENTATION:
				com_X = event.values[0];
				com_Y = event.values[1];
				com_Z = event.values[2];
				if (compass_flag == 1) {
					postText.setText("Orientation (degrees):  " + com_X);
				}
				break;

			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				mSensorManager.SENSOR_DELAY_UI); // SENSOR_DELAY_FASTEST
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				mSensorManager.SENSOR_DELAY_UI);
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		// Initialize the compose field with a listener for the return key
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		// Initialize the send button with a listener that for click events
		mSendButton = (Button) findViewById(R.id.button_send);
		mSendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				TextView view = (TextView) findViewById(R.id.edit_text_out);
				String message = view.getText().toString();
				//sendMessage(message);
			}
		});

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");

		// Setup camera and sensors
		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		lightBtn = (ToggleButton) findViewById(R.id.light_button);
		cameraBtn = (Button) findViewById(R.id.camera_button);
		compassBtn = (Button) findViewById(R.id.compass_button);
		accelBtn = (Button) findViewById(R.id.accel_button);
		gpsBtn = (Button) findViewById(R.id.gps_button);
		postText = (TextView) findViewById(R.id.data_text);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		buttonActions();
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
		locationManager.removeUpdates((LocationListener) this);
		preview.camera.release();
		if (mChatService != null) mChatService.stop();
	}

	@Override
	public void onStop() {
		super.onStop();
		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		mSensorManager.unregisterListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
		locationManager.removeUpdates((LocationListener) this);
		preview.camera.release();
		if (mChatService != null) mChatService.stop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			mOutEditText.setText(mOutStringBuffer);
		}
	}
*/	
	private void sendMessage(byte[] message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		//if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			//byte[] send = message.getBytes();
			mChatService.write(message);

			// Reset out string buffer to zero and clear the edit text field
			//mOutStringBuffer.setLength(0);
			//mOutEditText.setText(mOutStringBuffer);
		//}
	}

	// The action listener for the EditText widget, to listen for the return key
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				//sendMessage(message);
			}
			if (D)
				Log.i(TAG, "END onEditorAction");
			return true;
		}
	};

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				
				//Toggle flash light
				if(readBuf[0] == 0x02 && readBuf[1] == 0x01) {
					if (light_flag == 0) {
						Camera.Parameters camParam = preview.camera.getParameters();
						camParam.setFlashMode("torch");
						preview.camera.setParameters(camParam);
						light_flag = 1;
					} else {
						Camera.Parameters camParam = preview.camera.getParameters();
						camParam.setFlashMode("off");
						preview.camera.setParameters(camParam);
						light_flag = 0;
					}
				}
				
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
						+ readMessage);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				// Attempt to connect to the device
				mChatService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			// Launch the DeviceListActivity to see devices and do scan
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

}