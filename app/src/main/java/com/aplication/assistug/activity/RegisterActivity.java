package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.aplication.assistug.R;
import com.aplication.assistug.model.Cedula;
import com.aplication.assistug.model.Registro;
import com.aplication.assistug.model.Usuario;

import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    EditText etNombre, etApellido,etCedula, etCorreo;
    TextInputEditText etContra, etValcontra;
    ImageView  tvSubirImg, ibscrolldown;
    ImageButton ivSelectImg, btbackregister;
    ScrollView scrollview;
    boolean foto = false;
    Button btRegister;
    String StringURL = "";
    Uri imageuri;
    private FirebaseAuth  mAuthUser;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase, mDatabasem;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
                                                                    "(?=.*[0-9])" +     // al menos 1 número
                                                                    "(?=.*[a-z])"+      // al menos 1 letra min
                                                                    "(?=.*[A-Z])"+      // al menos 1 letra may
                                                                    "(?=.*[!@#&()–[{}]:;',?/*~$^+=<>])"+      // al menos 1 caracter esp
                                                                    "(?=\\S+$)" +       // no white spaces
                                                                    ".{8,15}" +         // al menos 8 caracteres y máximo 15
                                                                    "$");

    private static final Pattern EMAIL_ADDRESS = Pattern.compile("^" +
                                                                "[a-z0-9]{1,256}" +
                                                                "\\@ug.edu.ec" +
                                                                "$");

    private static final Pattern CEDULA_PATTERN = Pattern.compile("^" +
            "(?=\\S+$)" +       // no white spaces
            ".{10}" +         // al menos 8 caracteres y máximo 15
            "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        scrollview = findViewById(R.id.scrollview);
        mAuthUser = FirebaseAuth.getInstance();
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCedula = findViewById(R.id.etCedula);
        etCorreo = findViewById(R.id.etCorreo);
        etContra = findViewById(R.id.etContra);
        etValcontra = findViewById(R.id.etValcontra);
        btRegister = findViewById(R.id.btRegistrar);
        btbackregister = findViewById(R.id.btbackregister);
        ivSelectImg = findViewById(R.id.ivSelectImg);

        ibscrolldown = findViewById(R.id.scrollviewdown);
        tvSubirImg = findViewById(R.id.tvSubirImg);

        recuperarCon();
        btnregistrar();
        ivSelectImg();
        btbackregister();
        ibscrolldown();

    }

    private void ibscrolldown() {
        ibscrolldown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                etCorreo.clearFocus();
            }
        });
    }

    private void btbackregister() {
        btbackregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void ivSelectImg() {
        ivSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revisionPermisos();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                } else System.out.println("hoa");
                return;
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

    }

    private void btnregistrar() {
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String passwordInput = etContra.getText().toString().trim();
                String emailInput = etCorreo.getText().toString().trim();

                if(TextUtils.isEmpty(etNombre.getText().toString().trim())) {
                    etNombre.setError("Debe ingresar su nombre.");
                    return;
                }
                if (TextUtils.isEmpty(etApellido.getText().toString().trim())){
                    etApellido.setError("Debe ingresar su apellido");
                    return;
                }
                if(TextUtils.isEmpty(etCedula.getText().toString().trim())) {
                    etCedula.setError("Debe ingresar su Celula.");
                    return;
                }
                if(TextUtils.isEmpty(etCorreo.getText().toString().trim())) {
                    etCorreo.setError("Debe ingresar su correo.");
                    return;
                }
          /*     if (!EMAIL_ADDRESS.matcher(emailInput).matches()){
                    etCorreo.setError("Formato Inválido");
                          return;
                }*/
                if(TextUtils.isEmpty(etContra.getText().toString().trim())) {
                    etContra.setError("Debe ingresar su contraseña.");
                    return;
                }
                if (!PASSWORD_PATTERN.matcher(passwordInput).matches()){
                    etContra.setError("Formato Inválido");
                    return;
                }
                if(TextUtils.isEmpty(etValcontra.getText().toString().trim())) {
                    etValcontra.setError("Debe volver a ingresar su contraseña.");
                    return;
                }
                if(!etContra.getText().toString().trim().equals(etValcontra.getText().toString().trim())){
                    mostrarMensaje("Las contraseñas son diferentes");
                    return;
                }

                Query query = FirebaseDatabase.getInstance().getReference().child("Cedulas").orderByChild("cedula").equalTo(etCedula.getText().toString().trim());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount() > 0) {
                            mostrarMensaje("La cedula ya se encuentra en uso.");
                        } else {

                            registrarUsuario(etNombre.getText().toString().trim(), etApellido.getText().toString().trim(), etCedula.getText().toString().trim(), etCorreo.getText().toString().trim(), etValcontra.getText().toString().trim());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        mostrarMensaje(error.toString());
                    }
                });


            }
        });
    }

    private void recuperarCon () {

        mDatabase = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Registro");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Registro registro = snapshot.getValue(Registro.class);

                    FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                            .setDatabaseUrl(registro.getDatabaseurl())
                            .setApiKey(registro.getApikey())
                            .setApplicationId(registro.getApplicationid()).build();

                    try { FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "AssistUG");
                        mAuthUser = FirebaseAuth.getInstance(myApp);

                    } catch (IllegalStateException e){
                        mAuthUser = FirebaseAuth.getInstance(FirebaseApp.getInstance("AssistUG"));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarMensaje(error.toString());
                //startActivity(new Intent(NuevoUsuarioActivity.this, ActivityMenu.class));
            }
        });


    }
    private void  registrarUsuario(String nombre, String apellido, String cedula, String correo, String valcontra ){

        mAuthUser.createUserWithEmailAndPassword(correo, valcontra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(foto) {
                        String path = "imagenperfil/"+ mAuthUser.getCurrentUser().getUid() +".png";
                        mStorageRef = FirebaseStorage.getInstance().getReference().child(path);
                        mStorageRef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {

                                    @Override
                                    public void onSuccess(Uri uri) {
                                        StringURL = uri.toString();

                                        Usuario user = new Usuario(mAuthUser.getCurrentUser().getUid(),nombre, apellido, cedula, correo, "usuario","alumno", StringURL);
                                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario").child(mAuthUser.getCurrentUser().getUid());

                                        mDatabase.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Cedulas");

                                                    String uidCedula = mDatabase.push().getKey();
                                                    Cedula cedulaModel = new Cedula(uidCedula, cedula);


                                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Cedulas").child(uidCedula);

                                                    mDatabase.setValue(cedulaModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {

                                                                mAuthUser.getCurrentUser().sendEmailVerification()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    mostrarMensaje("Te hemos enviado un correo de verificación");

                                                                                    mAuthUser.signOut();
                                                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                                    startActivity(intent);
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        });




                                    }
                                }).addOnFailureListener(new OnFailureListener() {

                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    } else {
                        Usuario user = new Usuario(mAuthUser.getCurrentUser().getUid(),nombre, apellido, cedula, correo, "usuario","alumno", "");
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario").child(mAuthUser.getCurrentUser().getUid());

                        mDatabase.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Cedulas");

                                    String uidCedula = mDatabase.push().getKey();
                                    Cedula cedulaModel = new Cedula(uidCedula, cedula);


                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Cedulas").child(uidCedula);

                                    mDatabase.setValue(cedulaModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                etNombre.setText("");
                                                etApellido.setText("");
                                                etCedula.setText("");
                                                etCorreo.setText("");
                                                etContra.setText("");
                                                etValcontra.setText("");

                                                mAuthUser.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mostrarMensaje("Te hemos enviado un correo de verificación");

                                                                    mAuthUser.signOut();
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                                } else {
                                    mostrarMensaje(task.getException().toString());
                                }
                            }
                        });

                    }

                } else {
                    mostrarMensaje(task.getException().toString());
                }
            }
        });





    }
    private void revisionPermisos(){
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this ,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this,"Por favor acepte los permisos requeridos para continuar.",Toast.LENGTH_SHORT).show();
            }
            else {

                ActivityCompat.requestPermissions(RegisterActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

        }
        else {
            seleccionarArchivo();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1 && data != null){
            imageuri = data.getData();
            tvSubirImg.setImageURI(imageuri);
            foto = true;
        }
    }

    private void seleccionarArchivo(){
        String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1);


    }


}