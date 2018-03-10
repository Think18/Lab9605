package com.ibm.hellopush;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.List;

public class BookingHistoryActivity extends Activity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
       // Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);


        listView = findViewById(R.id.details);
        populateListView();
    }

    private void populateListView() {
        List<MovieItems> results = MovieManager.getInstance().getResults();
        MovieAdapter ma = new MovieAdapter(this,results);
        listView.setAdapter(ma);

    }
}
