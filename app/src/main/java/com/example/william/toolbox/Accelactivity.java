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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.attr.data;
import static android.os.SystemClock.elapsedRealtime;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static com.example.william.toolbox.R.id.lineChart;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Accelactivity extends AppCompatActivity implements SensorEventListener {

    public static final String EXTRA_MESSAGE = "com.example.william.toolbox.MESSAGE";

    private TextView xText,yText,zText,timeView,textviewCounter,textviewSamplingFrequency;
    private LinearLayout ll1;
    private Button clear,record,export;
    private RadioButton xplot,yplot,zplot;
    private Sensor mySensor;
    private SensorManager SM;
    private int counter = 0;
    private ArrayList<Float> xarraylist = new ArrayList<Float>();
    private ArrayList<Float> yarraylist = new ArrayList<Float>();
    private ArrayList<Float> zarraylist = new ArrayList<Float>();
    private ArrayList<Float> timearraylist = new ArrayList<Float>();
    private boolean recordIsPressed = FALSE;
    private long initialTime;
    private long currentTime;
    private String ztestlabel,xtestlabel,ytestlabel;
    //testes testes testes

    private LineChart mChart;
    private BarChart barChart;
    private BarEntry xentry,yentry,zentry;
    private ArrayList<String> barLabels = new ArrayList<String>();
    private boolean xbool,ybool,zbool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelactivity);

        //create sensor manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Accelerometer
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Register sensor Listener
        //SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_FASTEST);
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);

        //Assign TextView
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        //xplot = (RadioButton)findViewById(R.id.xplot);
        //yplot = (RadioButton)findViewById(R.id.yplot);
        //zplot = (RadioButton)findViewById(R.id.zplot);
        timeView = (TextView)findViewById(R.id.timeView);
        ll1 = (LinearLayout)findViewById(R.id.ll1);
        clear = (Button)findViewById(R.id.clear);
        record = (Button)findViewById(R.id.record);
        export = (Button)findViewById(R.id.export);
        textviewCounter = (TextView) findViewById(R.id.textviewCounter);
        textviewSamplingFrequency = (TextView) findViewById(R.id.textviewSamplingFrequency);
        //create radio buttons to assign test (data) label
        ztestlabel = "z-axis accelerometer data";
        xtestlabel = "x-axis accelerometer data";
        ytestlabel = "y-axis accelerometer data";


        mChart = (LineChart) findViewById(R.id.lineChart);
        barChart = (BarChart) findViewById(R.id.barChart);
        barLabels.add("x");
        barLabels.add("y");
        barLabels.add("z");
        
        //customize
        //mChart.setDescription();
        //mChart.setNoDataText("No data");

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
        barChart.setBackgroundColor(Color.LTGRAY);





        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //crunch those numbas
                String data = "";
                int len = timearraylist.size();
                String header = "x,y,z,t\n";

                data = data.concat(header);

                for(int i = 0; i<len; i++){
                    data = data.concat(xarraylist.get(i).toString());
                    data = data.concat(",");
                    data = data.concat(yarraylist.get(i).toString());
                    data = data.concat(",");
                    data = data.concat(zarraylist.get(i).toString());
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
                    mChart.setVisibility(timeView.VISIBLE);
                    barChart.setVisibility(timeView.INVISIBLE);

                    //final plot
                    /*
                    ArrayList<Entry> entries = new ArrayList<Entry>();

                    if (timearraylist.size() != 0){
                        //add up the entries
                        for (int i = 0; i<timearraylist.size();i++){
                            entries.add(new Entry(timearraylist.get(i),zarraylist.get(i)));
                        }
                    }
                    else{
                        //clear any residual entries if the data sets are empty.
                        entries.clear();
                    }


                    Collections.sort(entries, new EntryXComparator());
                    LineDataSet set = new LineDataSet(entries,testlabel);
                    List<ILineDataSet> dsets = new ArrayList<ILineDataSet>();
                    dsets.add(set);
                    LineData dd = new LineData(dsets);

                    mChart.setData(dd);
                    mChart.invalidate(); // refresh
                    */

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
                xarraylist = new ArrayList<Float>();
                yarraylist = new ArrayList<Float>();
                zarraylist = new ArrayList<Float>();
                timearraylist = new ArrayList<Float>();
                mChart.setVisibility(timeView.INVISIBLE);
                barChart.setVisibility(timeView.VISIBLE);


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

    /*
        xplot.setOnClickListener(new View.OnClickListener() {
                                     public void onClick(View v) {
                                        if()
                                     }
                                 }
        );
        */
        //xplot.toggle();

        //xplot.setOnClickListener();

        zbool = true;
        xbool = true;
        ybool = true;
    }

    //When a new value is ready, per the rate indicated by sensor delay
    @Override
    public void onSensorChanged(SensorEvent event) {
        //assign data
        xentry = new BarEntry(0,(float)(event.values[0]/9.81));
        yentry = new BarEntry(1,(float)(event.values[1]/9.81));
        zentry = new BarEntry(2,(float)(event.values[2]/9.81));

        ArrayList<BarEntry> xEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> zEntries = new ArrayList<BarEntry>();
        //update barEntries
        xEntries.add(xentry);
        yEntries.add(yentry);
        zEntries.add(zentry);

        //barchart commands
        BarDataSet xset = new BarDataSet(xEntries,"X Axis (G's)");
        BarDataSet yset = new BarDataSet(yEntries,"Y Axis (G's)");
        BarDataSet zset = new BarDataSet(zEntries,"Z Axis (G's)");


        //barChart.setDescription("3 Axis Acceleration (G's)");


        xText.setText("X: " + (float)Math.round(event.values[0] * 1000) / 1000);

        if (Math.abs(event.values[0]) > Math.abs(8.5)){
            xText.setTextColor(Color.parseColor("#ff4444"));
            xset.setColor(Color.parseColor("#ff4444"));
        }
        else if (Math.abs(event.values[0]) > Math.abs(4.5)){
            xText.setTextColor(Color.parseColor("#ccff00"));
            xset.setColor(Color.parseColor("#ccff00"));
        }
        else{
            xText.setTextColor(Color.parseColor("#006699"));
            xset.setColor(Color.parseColor("#006699"));
        }

        yText.setText("Y: " + (float)Math.round(event.values[1] * 1000) / 1000);

        if (Math.abs(event.values[1]) > Math.abs(8.5)){
            yText.setTextColor(Color.parseColor("#ff4444"));
            yset.setColor(Color.parseColor("#ff4444"));
        }
        else if (Math.abs(event.values[1]) > Math.abs(4.5)){
            yText.setTextColor(Color.parseColor("#ccff00"));
            yset.setColor(Color.parseColor("#ccff00"));
        }
        else{
            yText.setTextColor(Color.parseColor("#006699"));
            yset.setColor(Color.parseColor("#006699"));
        }

        zText.setText("Z: " + (float)Math.round(event.values[2] * 1000) / 1000);

        if (Math.abs(event.values[2]) > 8.5){
            zText.setTextColor(Color.parseColor("#ff4444"));
            zset.setColor(Color.parseColor("#ff4444"));
        }
        else if (Math.abs(event.values[2]) > Math.abs(4.5)){
            zText.setTextColor(Color.parseColor("#ccff00"));
            zset.setColor(Color.parseColor("#ccff00"));
        }
        else{
            zText.setTextColor(Color.parseColor("#006699"));
            zset.setColor(Color.parseColor("#006699"));
        }

        //update time value
        timeView.setText("Recording Time: " + (currentTime-initialTime)/1000.0);

        BarData bd = new BarData(xset);
        bd.addDataSet(yset);
        bd.addDataSet(zset);

        barChart.setData(bd);
        //barChart.setY
        barChart.invalidate();

        //if record button was pressed once, record
        if (recordIsPressed) {
            mChart.setVisibility(timeView.VISIBLE);
            barChart.setVisibility(timeView.INVISIBLE);
            currentTime = elapsedRealtime(); //keep track of elapsed time
            timearraylist.add((float)(currentTime - initialTime)/1000);
            xarraylist.add(event.values[0]);
            yarraylist.add(event.values[1]);
            zarraylist.add(event.values[2]);
            textviewCounter.setText("Counter: " + (counter +1));

            //ultra inefficient plotting
            ArrayList<Entry> zentries = new ArrayList<Entry>();
            ArrayList<Entry> yentries = new ArrayList<Entry>();
            ArrayList<Entry> xentries = new ArrayList<Entry>();

            //z vs t
            if (timearraylist.size() != 0 && zbool){
                //add up the entries
                for (int i = 0; i<timearraylist.size();i++){
                    zentries.add(new Entry(timearraylist.get(i),zarraylist.get(i)));
                }
            }
            else{
                //clear any residual entries if the data sets are empty.
                zentries.clear();
            }

            //y vs t
            if (timearraylist.size() != 0 && ybool){
                //add up the entries
                for (int i = 0; i<timearraylist.size();i++){
                    yentries.add(new Entry(timearraylist.get(i),yarraylist.get(i)));
                }
            }
            else{
                //clear any residual entries if the data sets are empty.
                yentries.clear();
            }

            //x vs t
            if (timearraylist.size() != 0 && xbool){
                //add up the entries
                for (int i = 0; i<timearraylist.size();i++){
                    xentries.add(new Entry(timearraylist.get(i),xarraylist.get(i)));
                }
            }
            else{
                //clear any residual entries if the data sets are empty.
                xentries.clear();
            }

            //Collections.sort(zentries, new EntryXComparator());//not necessary, t values always in order

            //make lds
            LineDataSet zlset = new LineDataSet(zentries,ztestlabel);
            LineDataSet ylset = new LineDataSet(yentries,ytestlabel);
            LineDataSet xlset = new LineDataSet(xentries,xtestlabel);
            List<ILineDataSet> dsets = new ArrayList<ILineDataSet>();

            //customize dsets
            zlset.setCubicIntensity(0.2f);
            zlset.setAxisDependency(YAxis.AxisDependency.LEFT);
            zlset.setColor(ColorTemplate.getHoloBlue());
            zlset.setCircleColor(ColorTemplate.getHoloBlue());
            zlset.setLineWidth(2f);
            zlset.setCircleRadius(4f);
            zlset.setFillAlpha(65);
            zlset.setFillColor(ColorTemplate.getHoloBlue());
            zlset.setHighLightColor(Color.rgb(244,117,177));
            zlset.setValueTextSize(10f);

            ylset.setCubicIntensity(0.2f);
            ylset.setAxisDependency(YAxis.AxisDependency.LEFT);
            ylset.setColor(Color.YELLOW);
            ylset.setCircleColor(Color.BLACK);
            ylset.setLineWidth(2f);
            ylset.setCircleRadius(4f);
            ylset.setFillAlpha(65);
            ylset.setFillColor(ColorTemplate.getHoloBlue());
            ylset.setHighLightColor(Color.rgb(244,117,177));
            ylset.setValueTextSize(10f);

            xlset.setCubicIntensity(0.2f);
            xlset.setAxisDependency(YAxis.AxisDependency.LEFT);
            xlset.setColor(Color.WHITE);
            xlset.setCircleColor(Color.RED);
            xlset.setLineWidth(2f);
            xlset.setCircleRadius(4f);
            xlset.setFillAlpha(65);
            xlset.setFillColor(ColorTemplate.getHoloBlue());
            xlset.setHighLightColor(Color.rgb(244,117,177));
            xlset.setValueTextSize(10f);


            //add to datasetlist
            dsets.add(zlset);
            dsets.add(ylset);
            dsets.add(xlset);

            //finish the job
            LineData dd = new LineData(dsets);
            mChart.setData(dd);
            mChart.invalidate(); // refresh

            //data plotting
            /*
            LineData xvals = new LineData();
            xvals.setValueTextColor(Color.WHITE);

            //add data
            mChart.setData(xvals);

            //add legend
            Legend l = mChart.getLegend();

            //customize the legend
            l.setForm(Legend.LegendForm.LINE);
            l.setTextColor(Color.WHITE);

            XAxis x1 = mChart.getXAxis();
            x1.setTextColor(Color.WHITE);
            //x1.setDrawGrideLines(false);
            x1.setAvoidFirstLastClipping(true);

            YAxis y1 = mChart.getAxisLeft();
            y1.setTextColor(Color.WHITE);
            y1.setAxisMaximum(120f);
            y1.setDrawGridLines(true);

            YAxis y12 = mChart.getAxisRight();
            y12.setEnabled(false);
            */
            //iterate
            counter = counter + 1;
        }

    }
/*
    @Override
    protected void onResume() {
        super.onResume();

        //now, we're going to simulate real time data addition

        new Thread()
    }
*/
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing for now
    }

    //method to add entry to a line chart
    private void addEntry() {

        LineData dat = mChart.getData();

        if (dat != null) {
            LineDataSet set = (LineDataSet) dat.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                dat.addDataSet(set);
            }

            //add a new random value
            //dat.addXValue();
            /*
            dat.addEntry(
                    new Entry((float) Math.random()*75)+60f,set.getEntryCount(),0
            );*/

            dat.addEntry(new Entry(1,1),dat.getDataSetCount());
            /*
            dat.addEntry(new Entry(2,2),dat.getDataSetCount());
            dat.addEntry(new Entry(3,3),dat.getDataSetCount());
            dat.addEntry(new Entry(4,4),dat.getDataSetCount());
            dat.addEntry(new Entry(5,5),dat.getDataSetCount());
            */


            //notify chart data have changed
            mChart.notifyDataSetChanged();

            //limit number of visible entries
            //mChart.setVisibleXRange(0,20);

            //scroll to the last entry
            //mChart.moveViewToX(dat.getXValCount() - 7);
            //mChart.moveViewToX(dat.getDataSetCount() - 7);

        }

    }
//Method to create set
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "SPL Db");
        //set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244,117,177));
        set.setValueTextSize(10f);

        return set;
    }
}
