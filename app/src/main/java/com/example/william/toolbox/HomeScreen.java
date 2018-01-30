package com.example.william.toolbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {

    Button ba, bm, bg, bb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        ba = (Button)findViewById(R.id.accelbutton);
        bm = (Button)findViewById(R.id.micbutton);
        bg = (Button)findViewById(R.id.gyrobutton);
        bb = (Button)findViewById(R.id.barobutton);

        setOnClickListeners();
    }

    private void setOnClickListeners(){
        ba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.william.toolbox.Accelactivity");
                startActivity(intent);
            }
        });
        bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.william.toolbox.Micactivity");
                startActivity(intent);
            }
        });
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.william.toolbox.Gyro");
                startActivity(intent);
            }
        });
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.william.toolbox.BaroActivity");
                startActivity(intent);
            }
        });
    }
}
