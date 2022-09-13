package com.aplication.assistug.activity;

import static com.google.android.gms.wearable.DataMap.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.aplication.assistug.R;


public class LoginActivity extends AppCompatActivity {

    CheckBox checkBox;
    GoogleApiClient googleApiClient;
    LinearLayout cardview;
    Button btIniciar, btOlvidar;
    TextInputEditText etContralogin;
    EditText etCorreologin;
    private FirebaseAuth mAuth;
    String siteKey = "6LfwG9YgAAAAAKshugyD6fa17KVXzUxm8GxsB0Bj";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        checkBox = findViewById(R.id.checkBox);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .build();
        googleApiClient.connect();
        cardview = findViewById(R.id.cardviewlogin);
        btIniciar =findViewById(R.id.btiniciar);
        btOlvidar = findViewById(R.id.btolvidar);
        etContralogin = findViewById(R.id.etContraLogin);
        etCorreologin = findViewById(R.id.etCorreologin);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            checkBox.setChecked(false);
                SafetyNet.SafetyNetApi.verifyWithRecaptcha(googleApiClient, siteKey)
                        .setResultCallback(new ResultCallback() {
                            @Override
                            public void onResult(@NonNull Result result) {

                                Status status = result.getStatus();
                                if ((status != null) && status.isSuccess()) {
                                    checkBox.setChecked(true);
                                    Toast.makeText(getApplicationContext(), "Verification Successful", Toast.LENGTH_SHORT).show();;
                                } else {
                                    Log.e("MY_APP_TAG", "Error occurred " +
                                            "when communicating with the reCAPTCHA service.");}
                            }
                        });
            };
    });
        int googleServiceStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this,googleServiceStatus, 10);
        if(dialog!=null){
            dialog.show();
        }



        Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_dropup);
        cardview.startAnimation(animSlideDown);

        btIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etCorreologin.getText().toString().trim())) {
                    etCorreologin.setError("Debe ingresar su correo");
                    return;
                }
                if(TextUtils.isEmpty(etContralogin.getText().toString().trim())) {
                    etContralogin.setError("Debe ingresar su contraseña");
                    return;
                }
                if(!checkBox.isChecked()) {
                    mostrarMensaje("Debe validar el reCAPTCHA");
                    return;
                }
                SignIn(etCorreologin.getText().toString().trim(), etContralogin.getText().toString().trim());

            }
        });



        btOlvidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, OlvidecontraActivity.class);
                startActivity(intent);
            }
        });


    }

    private void SignIn(String correo, String contra) {
        mAuth.signInWithEmailAndPassword(correo, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified()) {
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        mostrarMensaje("Verifica tu correo");
                        mAuth.signOut();
                    }
                }   else {
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(LoginActivity.this, "La contraseña es invalida, vuelva a intentarlo", Toast.LENGTH_LONG).show();
                    }catch(FirebaseAuthInvalidUserException e){
                        Toast.makeText(LoginActivity.this, "El usuario no ha sido encontrado,  solicite la creación de una nueva cuenta al administrador", Toast.LENGTH_LONG).show();
                    }catch(FirebaseNetworkException e){
                        Toast.makeText(LoginActivity.this, "Revisa tu conexión a internet", Toast.LENGTH_LONG).show();
                    }
                    catch(FirebaseTooManyRequestsException e){
                        Toast.makeText(LoginActivity.this, "Se han solicitado demasiadas peticiones de inicio de sesión incorrectas, intentelo más tarde", Toast.LENGTH_LONG).show();
                    }
                    catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                    }

                }

            }
        });

    }
    private void mostrarMensaje(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

    }


}