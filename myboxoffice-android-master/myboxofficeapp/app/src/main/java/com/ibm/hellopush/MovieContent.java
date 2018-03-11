package com.ibm.hellopush;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MovieContent extends AppCompatActivity {
    public static final String PREFS="example";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_content);
    }
}
