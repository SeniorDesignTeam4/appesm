package com.example.william.toolbox;

        import android.media.AudioFormat;
        import android.media.AudioRecord;
        import android.media.MediaRecorder;
        import android.os.Environment;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

        import java.io.IOException;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;
        import java.util.Arrays;

public class Micactivity extends AppCompatActivity {

    private int sampleRate = 8000, bufferSize, bufferElement2Rec = 1024, bytesPerElement = 2;
    private Button stop_record, stop, play;
    boolean isRunning = false;
    private AudioRecord myAudioRecorder = null;
    private Thread recordingThread = null;
    private FileOutputStream os = null;
    ArrayList<short[]> aData;
    int a;
    List test_list = new ArrayList();

    private void startRecording() {

        myAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        myAudioRecorder.startRecording();
        isRunning = true;
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"AudioRecorderThread");
        recordingThread.start();

        stop_record.setEnabled(false);
        stop.setEnabled(true);

        Toast.makeText(getApplicationContext(),"Recording Started", Toast.LENGTH_SHORT).show();
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize*2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x0FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    private ArrayList<short[]> shortToArrayList(short[] sData){
        ArrayList<short[]> arrList = new ArrayList<>();
        arrList.add(sData);
        return arrList;
    }

    private void writeAudioDataToFile() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        short sData[] = new short[bufferElement2Rec];
        try {
            os = new FileOutputStream(filePath + "/record.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        a == 0;

        while (isRunning) {
            myAudioRecorder.read(sData, 0, bufferElement2Rec);
            System.out.println("Audio File" + Arrays.toString(sData));
            aData = shortToArrayList(sData);
            String test_str =  Arrays.toString(sData);
            test_str = test_str.substring(0, test_str.length()-1);
            Collections.addAll(test_list, test_str.substring(1));
            try {
                byte bData[] = short2byte(sData);
                /*System.out.println("Short writing to file" + bData.toString());*/
                os.write(bData, 0, bufferElement2Rec * bytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Audio Data 2: " + aData);
        System.out.println("Test List: " + test_list);

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if(myAudioRecorder != null)
        {
            isRunning = false;
            myAudioRecorder.stop();
            System.out.println("Audio Data" + aData);
            myAudioRecorder.release();
            myAudioRecorder = null;
            recordingThread = null;
            System.out.println("Test List: " + test_list);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micactivity);

        bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        play = (Button) findViewById(R.id.play);
        stop_record = (Button) findViewById(R.id.stop_record);
        stop = (Button) findViewById(R.id.stop);
        /*graphMic = (Button) findViewById(R.id.graphMic);*/

        stop.setEnabled(false);
        play.setEnabled(false);

        stop_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                stop_record.setEnabled(true);
                stop.setEnabled(false);

                Toast.makeText(getApplicationContext(), "Audio Recorded", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

