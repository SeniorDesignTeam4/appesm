package com.example.william.toolbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class Export extends AppCompatActivity {

    private String fbody = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        Intent myintent = getIntent();
        String data = myintent.getStringExtra(Accelactivity.EXTRA_MESSAGE);

/*
        switch (fbody = body) {
        }
*/
        switch(fbody = data){

        }
        Button b1 = (Button) findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                String emailList[] = {"dough7@vt.edu"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,emailList);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Testo");
                emailIntent.putExtra(Intent.EXTRA_TEXT,"Did this send?");
                //emailIntent.putExtra(Intent.EXTRA_TEXT,fbody);
                emailIntent.putExtra(Intent.EXTRA_TEXT,fbody);
                startActivity(Intent.createChooser(emailIntent,"Choice email APP"));
            }

        });
    }
}
