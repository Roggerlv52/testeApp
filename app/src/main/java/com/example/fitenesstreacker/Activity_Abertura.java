package com.example.fitenesstreacker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Activity_Abertura extends AppCompatActivity {
    Button abertura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura);

        abertura = findViewById(R.id.Btn_abertura);

        abertura.setOnClickListener(v -> {
            Intent abert = new Intent(Activity_Abertura.this, MainActivity.class);
            startActivity(abert);
        });

    }
}