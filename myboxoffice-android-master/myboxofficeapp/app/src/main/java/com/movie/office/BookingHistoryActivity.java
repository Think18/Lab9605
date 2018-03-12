package com.movie.office;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class BookingHistoryActivity extends Activity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.movie.office.R.layout.activity_booking_history);
       // Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);


        listView = findViewById(com.movie.office.R.id.details);
        populateListView();
    }

    private void populateListView() {
        List<MovieItems> results = MovieManager.getInstance().getResults();
        MovieAdapter ma = new MovieAdapter(this,results);
        listView.setAdapter(ma);

    }
}
