package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.aplication.assistug.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class OlvidecontraActivity extends AppCompatActivity {
    ConstraintLayout constraintLayoutolvide;
    EditText etemail;
    Button btolvide;
    ImageButton  btbackolvide;
    private   FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvidecontra);

        mAuth = FirebaseAuth.getInstance();
        etemail = findViewById(R.id.etemail);
        btolvide = findViewById(R.id.btolvide);
        btbackolvide = findViewById(R.id.btbackolvide);
        constraintLayoutolvide = findViewById(R.id.constraintLayoutolvide);

        Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_dropup);
        constraintLayoutolvide.startAnimation(animSlideDown);

        btolvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etemail.getText().toString().trim())) {
                    etemail.setError("Debe ingresar su correo");
                    return;
                }else{
                    mAuth.sendPasswordResetEmail(etemail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mostrarMensaje("se ha enviado un correo de recuperación");
                                    } else {
                                        String prueba = task.getException().toString();
                                        mostrarMensaje(task.getException().toString());
                                    }
                                }
                            });
                }
            }
        });

        btbackolvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OlvidecontraActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

    }
}