package com.trello.victorsample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView secondImageView = (ImageView) findViewById(R.id.second_image_view);
        secondImageView.setImageResource(R.drawable.ic_smartphone);
    }

}
