package com.example.william.toolbox;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import static android.os.SystemClock.elapsedRealtime;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by William on 1/4/2018.
 */

public class BaroActivity extends AppCompatActivity implements SensorEventListener {
    public static final String EXTRA_MESSAGE = "com.example.william.toolbox.MESSAGE";
    public static final String X_MESS = "com.example.william.toolbox.XMessage";
    private TextView timeView,textviewCounter;
    private Button clear,record,export;
    private Sensor mySensor;
    private SensorManager SM;
    private int counter = 0;
    private ArrayList<Float> arraylist = new ArrayList<Float>();
    private ArrayList<Float> timearraylist = new ArrayList<Float>();
    private boolean recordIsPressed = FALSE;
    private long initialTime;
    private long currentTime;
    private String testlabel;
    private LineChart mChart;
    private ArrayList<String> barLabels = new ArrayList<String>();
    private boolean xbool,ybool,zbool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baro);

//create sensor manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Gyro
        mySensor = SM.getDefaultSensor(Sensor.TYPE_PRESSURE);

        //Register sensor Listener
        //SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_FASTEST);
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);

        timeView = (TextView)findViewById(R.id.timeView);
        clear = (Button)findViewById(R.id.clear);
        record = (Button)findViewById(R.id.record);
        export = (Button)findViewById(R.id.export);
        textviewCounter = (TextView) findViewById(R.id.textviewCounter);
        testlabel = "Pressure (Pa?)";

        mChart = (LineChart) findViewById(R.id.lineChart);

        //enable value highlighting
        mChart.setHighlightPerDragEnabled(true);

        //enable touch gestures
        mChart.setTouchEnabled(true);

        //enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        //enable pinch zoom scaling
        mChart.setPinchZoom(true);

        //alt background color
        mChart.setBackgroundColor(Color.LTGRAY);

        //enable value highlighting
        mChart.setHighlightPerDragEnabled(true);

        //enable touch gestures
        mChart.setTouchEnabled(true);

        //enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        //enable pinch zoom scaling
        mChart.setPinchZoom(true);

        //alt background color
        mChart.setBackgroundColor(Color.LTGRAY);

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //crunch those numbas
                String data = "";
                int len = timearraylist.size();
                String header = "x,y,z,t\n";

                data = data.concat(header);

                for(int i = 0; i<len; i++){
                    data = data.concat(arraylist.get(i).toString());
                    data = data.concat(",");
                    data = data.concat(timearraylist.get(i).toString());
                    data = data.concat("\n");
                }

                //Open the new activity
                Intent ExportIntent = new Intent("com.example.william.toolbox.Export");
                ExportIntent.putExtra(EXTRA_MESSAGE, data);
                startActivity(ExportIntent);
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //stop recording
                if (recordIsPressed) {
                    recordIsPressed = FALSE;
                    record.setText("Record");
                    //mChart.setVisibility(timeView.VISIBLE);
                }
                else{
                    recordIsPressed = TRUE;
                    record.setText("Stop");
                    initialTime = elapsedRealtime();
                    currentTime = elapsedRealtime();
                    //textviewSamplingFrequency.setText("Avg. Sampling Freq: " + (double) (counter+1)/(time[counter]*1000));
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //creates new empty arrays, clearing all data
                arraylist = new ArrayList<Float>();
                timearraylist = new ArrayList<Float>();
                //mChart.setVisibility(timeView.INVISIBLE);


                //resets chart data
                mChart.clear();

                //resets time data
                initialTime = elapsedRealtime();
                currentTime = elapsedRealtime();
                //resets counter
                counter = 0;
                textviewCounter.setText("Counter: " + (counter));
                //textviewCounter.setText("Avg. Sampling Freq: 0");
                //clear data points
            }
        });


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//update time value
        timeView.setText("Recording Time: " + (currentTime - initialTime) / 1000.0);

//if record button was pressed once, record
        if (recordIsPressed) {
            mChart.setVisibility(timeView.VISIBLE);
            currentTime = elapsedRealtime(); //keep track of elapsed time
            timearraylist.add((float) (currentTime - initialTime) / 1000);
            arraylist.add(event.values[0] * (float)(180/Math.PI));
            textviewCounter.setText("Counter: " + (counter + 1));

            //ultra inefficient plotting
            ArrayList<Entry> entries = new ArrayList<Entry>();

            //x vs t
            if (timearraylist.size() != 0) {
                //add up the entries
                for (int i = 0; i < timearraylist.size(); i++) {
                    entries.add(new Entry(timearraylist.get(i), arraylist.get(i)));
                }
            } else {
                //clear any residual entries if the data sets are empty.
                entries.clear();
            }

            //Collections.sort(zentries, new EntryXComparator());//not necessary, t values always in order

            //make lds
            LineDataSet lset = new LineDataSet(entries, testlabel);
            List<ILineDataSet> dsets = new ArrayList<ILineDataSet>();

            //customize dsets
            lset.setCubicIntensity(0.2f);
            lset.setAxisDependency(YAxis.AxisDependency.LEFT);
            lset.setColor(ColorTemplate.getHoloBlue());
            lset.setCircleColor(ColorTemplate.getHoloBlue());
            lset.setLineWidth(2f);
            lset.setCircleRadius(4f);
            lset.setFillAlpha(65);
            lset.setFillColor(ColorTemplate.getHoloBlue());
            lset.setHighLightColor(Color.rgb(244, 117, 177));
            lset.setValueTextSize(10f);

            //add to datasetlist
            dsets.add(lset);

            //finish the job
            LineData dd = new LineData(dsets);
            mChart.setData(dd);
            mChart.invalidate(); // refresh

            counter = counter + 1;

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
