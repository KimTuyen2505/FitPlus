package com.example.firstproject;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        // Set user name
        TextView userName = findViewById(R.id.userName);
        userName.setText("Nguyễn Đặng Kim Tuyến");
    }
}