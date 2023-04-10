package com.osukastudios.mobileafia.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MenuItem;
import com.osukastudios.mobileafia.Adapter.MyMovieAdapter;
import com.osukastudios.mobileafia.Model.MyMovieData;
import com.osukastudios.mobileafia.R;

public class Location extends AppCompatActivity{
    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        settingsToolbar = findViewById(R.id.locationToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Branches of Equity Afia");

        RecyclerView recyclerView = findViewById(R.id.locationRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyMovieData[] myMovieData = new MyMovieData[]{
                new MyMovieData("UpperHill", "Equity Afia Medical Centre", R.drawable.applogo),
                new MyMovieData("Roysambu", "Equity Afia Medical Centre", R.drawable.applogo),
                new MyMovieData("Westlands", "Equity Afia Medical Centre", R.drawable.applogo),
                new MyMovieData("Moi Avenue", "Equity Afia Medical Centre", R.drawable.applogo),
                new MyMovieData("Kangemi", "Equity Afia Medical Centre", R.drawable.applogo),
                new MyMovieData("Eastleigh", "Equity Afia Medical Centre", R.drawable.applogo),
                new MyMovieData("Nyali", "Equity Afia Medical Centre", R.drawable.applogo),
        };
        MyMovieAdapter myMovieAdapter = new MyMovieAdapter(myMovieData,Location.this);
        recyclerView.setAdapter(myMovieAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}