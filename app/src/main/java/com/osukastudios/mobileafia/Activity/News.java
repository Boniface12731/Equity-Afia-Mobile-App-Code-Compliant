package com.osukastudios.mobileafia.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.osukastudios.mobileafia.R;
public class News extends AppCompatActivity {
     Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        uploadButton = findViewById(R.id.orderBtn);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(News.this, "Your Order has been placed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}