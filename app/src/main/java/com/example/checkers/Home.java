package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void startGame(View v) {
        // Get game mode
        Button button = (Button)v;
        String gameMode = button.getText().toString();

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("mode", gameMode);
        startActivity(i);
    }
}