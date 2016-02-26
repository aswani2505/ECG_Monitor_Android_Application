package com.example.tommy.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    boolean isRunning = false;
    boolean isWindowOpen = true;
    Patient[] patient = new Patient[3];    // Array of patients
    float[] graphValues = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    Spinner spinner;
    int value;
    private TextView OurMessage;
    private TextView OurMessage1;
    private TextView OurMessage2;
    private SensorManager manager;
    private Sensor accelerometer;
    private float[] x,y,z;
    private int index = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("ECG Monitor");
        setContentView(R.layout.activity_main);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        addItemsOnListener();
        addListenerOnSpinner();

        patient[0] = new Patient("Alex Abner", 1234, 40, "Male");
        float[] ecgData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        patient[0].setECGValues(ecgData);

        patient[1] = new Patient("Bartholomew Bosley", 5678, 65, "Male");
        patient[1].setECGValues(ecgData);

        patient[2] = new Patient("Catherine Cooper", 9101, 27, "Female");
        patient[2].setECGValues(ecgData);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.

    private void addListenerOnSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value = spinner.getSelectedItemPosition();
                OurMessage = (TextView) findViewById(R.id.textView);    // age
                OurMessage.setText(String.valueOf(patient[value].getAge()));
                OurMessage1 = (TextView) findViewById(R.id.textView2);  // name
                OurMessage1.setText(patient[value].getName());
                OurMessage2 = (TextView) findViewById(R.id.textView3);  // gender
                OurMessage2.setText(patient[value].getGender());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void addItemsOnListener() {
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.Patient, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        isWindowOpen = true;
        final float[] extremes = {0, 150};  // Values used to calibrate graph.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GraphView graph = (GraphView) findViewById(R.id.view);
                graph.setValues(extremes);
            }
        });
        final Thread th = new Thread(new Runnable() {
            // points shown on the graph
            @Override
            public void run() {
                while (isWindowOpen) {
                    float[] ecgdata = patient[value].getECGValues();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GraphView graph = (GraphView) findViewById(R.id.view);
                            graph.setValues(graphValues);
                        }
                    });

                    float temp = ecgdata[0];
                    for (int j = 0; j < ecgdata.length - 1; j++) {
                        ecgdata[j] = ecgdata[j + 1];
                    }
                    ecgdata[ecgdata.length - 1] = temp;

                    // Move graph left while running.
                    if (isRunning) {
                        for (int j = 0; j < graphValues.length - 1; j++) {
                            graphValues[j] = graphValues[j + 1];
                        }
                        graphValues[graphValues.length - 1] = ecgdata[0];
                    }

                    // Delete all graph values if not running.
                    else {
                        for (int j = 0; j < graphValues.length; j++) {
                            graphValues[j] = 0;
                        }
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        //break;
                    }
                }
            }
        });
        th.start();


        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //th.interrupt();
                if (isRunning == true) {
                    isRunning = false;
                    OurMessage.setText("");
                    OurMessage1.setText("");
                    OurMessage2.setText("");
                }
            }
        });

        Button runButton = (Button) findViewById(R.id.runButton);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = true;
                OurMessage = (TextView) findViewById(R.id.textView);    // age
                OurMessage.setText(String.valueOf(patient[value].getAge()));
                OurMessage1 = (TextView) findViewById(R.id.textView2);  // name
                OurMessage1.setText(patient[value].getName());
                OurMessage2 = (TextView) findViewById(R.id.textView3);  // gender
                OurMessage2.setText(patient[value].getGender());

//                Intent intent = new Intent(MainActivity.this, acclService.class);
//                startService(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        isWindowOpen = false;
        manager.unregisterListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.tommy.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.tommy.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        index++;
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x[index] = event.values[0];
            y[index] = event.values[1];
            z[index] = event.values[2];

        }
        patient[0].setECGValues(x);
        patient[1].setECGValues(y);
        patient[2].setECGValues(z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

