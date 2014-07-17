package com.example.psychicstepdetector;

import java.io.IOException;

import com.example.psychicstepdetector.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StepDetectorActivity extends Activity implements SensorEventListener{

	/** Called when the activity is first created. */
	private TextView ServerText;
	private TextView PortText;
	private Button StartButton;
	private Button StopButton;
	private boolean isStarted=false;
	private float orientation = 0;
	private final float angle = -110;
	private boolean isReturned = true;
	private int myStep = 0;
	private Thread sendSteps;
	
	private TextView Steps;
	
	private SensorManager mSensorManager;
	private Sensor mOrientationSensor;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_step_detector);
	    ServerText = (TextView)findViewById(R.id.serverIp);
	    PortText = (TextView)findViewById(R.id.serverPort);
	    StartButton = (Button)findViewById(R.id.start_button);
	    StopButton = (Button)findViewById(R.id.stop_button);
	    StartButton.setOnClickListener(mClickListener);
	    StopButton.setOnClickListener(mClickListener);
	    Steps = (TextView) findViewById(R.id.myStep);
	    
	    
	    Steps.setText(String.valueOf(myStep));
	    ServerText.setText("192.168.206.205");
	    PortText.setText("8002");
	    /*
    	 * Create and register for sensors
    	 */
	    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

	    sendSteps = new Thread (new Runnable() {
			
			@Override
			public void run() {
				Client c = new Client("192.168.206.205", 8002);
				try {
					c.BuildUpConnection();
					c.messageSend("STEP#1");
					Log.v("Socket_Thread", "SENT");
					c.closeConnection();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
	    
	
	    // TODO Auto-generated method stub
	}
	private OnClickListener mClickListener = new OnClickListener(){

		@Override
		public void onClick(View button) {
			if(button.equals(StartButton)){
				isStarted=true;
				StartButton.setEnabled(!isStarted);
			}
			else if(button.equals(StopButton)){
				isStarted = false;
				StartButton.setEnabled(!isStarted);
				myStep = 0;
				Steps.setText(String.valueOf(myStep));
			}
			
		}
    	
    };



	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			orientation = event.values[1];
			if(event.values[1] <= angle && isReturned == true && isStarted == true){
				isReturned = false;
				Thread step = new Thread(sendSteps);
				step.start();
				myStep++;
				Steps.setText(String.valueOf(myStep));
			}
			else if(event.values[1] > angle && isReturned == false && isStarted == true){
				isReturned = true;
			}
		}
		
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.v("AcceleratorActivity_onResume", "Sensor_Start");
    }
    @SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
     // TODO Auto-generated method stub
     super.onPause();
     /*
      * Unregister sensors
      * Stop handle message
      */
     mSensorManager.unregisterListener(this); 
    }

}








