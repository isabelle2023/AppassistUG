package com.aplication.assistug.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.aplication.assistug.R;

public class MainActivity extends AppCompatActivity {
    private  static int SPLASH_SCREEN = 5000;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if( user == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                    finish();
                }
            }
        },SPLASH_SCREEN);













    }
}