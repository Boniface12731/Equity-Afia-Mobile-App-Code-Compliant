package com.osukastudios.mobileafia.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.osukastudios.mobileafia.R;
import com.osukastudios.mobileafia.databinding.ActivityMainBinding;

public class OnlineDoctor extends AppCompatActivity{

    private Toolbar settingsToolbar;
    GridView gridView;
    String[] fruitName = {  "Amoxicillin",
                            "Glycerin",
                            "Famotidine",
                            "Metrophol",
                            "Oxycontin",
                            "Dipentum"};
    int[]fruitImages = {R.drawable.amoxyl,
                        R.drawable.glyce,
                        R.drawable.famotidine,
                        R.drawable.metrophol,
                        R.drawable.oxycontin,
                        R.drawable.dipentum};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_doctor);

        settingsToolbar = findViewById(R.id.onlineToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Pharmacy");

        gridView =findViewById(R.id.gridView);
        CustomAdapter customAdapter = new CustomAdapter();
        gridView.setAdapter(customAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent =new Intent(getApplicationContext(), GridItemActivity.class);
               intent.putExtra("name",fruitName[position]);
               intent.putExtra("image",fruitImages[position]);
               startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
                default:
                return super.onOptionsItemSelected(item);
        }
    }
    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return fruitImages.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view1  = getLayoutInflater().inflate(R.layout.raw_data,null);
            TextView name = view1.findViewById(R.id.fruits);
            ImageView image = view1.findViewById(R.id.images);
            name.setText(fruitName[position]);
            image.setImageResource(fruitImages[position]);
            return view1;
        }
    }
}