package com.example.myhome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity implements View.OnClickListener{

    private CardView textdetect;
    private CardView mediaplayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textdetect=(CardView)findViewById(R.id.text);
        mediaplayer=(CardView)findViewById(R.id.media);

        textdetect.setOnClickListener(this);
        mediaplayer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.text : i=new Intent(this,TextDetection.class);startActivity(i);break;
            case R.id.media : i=new Intent(this,MusicPlayer.class);startActivity(i);break;
            default:break;
        }

    }
}
