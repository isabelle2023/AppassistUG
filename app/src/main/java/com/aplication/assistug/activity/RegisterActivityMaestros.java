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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
import com.aplication.assistug.model.Carrera;
import com.aplication.assistug.model.Cedula;
import com.aplication.assistug.model.Registro;
import com.aplication.assistug.model.RegistroMaestro;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class RegisterActivityMaestros extends AppCompatActivity {

    EditText etNombreRM,etApellidoRM,etCedulaRM,etCorreoRM;
    EditText etContraRM,etValcontraRM;
    EditText etDescripRM;
    Button btRegistrarMaestro;
    ImageButton ivSelectImgRM,btbackregisterRM;
    ImageView tvSubirImgRM;

    private FirebaseAuth mAuth;
    private FirebaseAuth mAuthUser;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    String StringURL = "";
    Uri StringURL2 ;
    Boolean foto = false;
    Uri imageuri;
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
                                                                "@ug.edu.ec" +
                                                                "$");

    private static final Pattern CEDULA_PATTERN = Pattern.compile("^" +
            "(?=\\S+$)" +       // no white spaces
            ".{10}" +       // al menos 8 caracteres y máximo 15
            "$");
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_maestros);

        mAuthUser = FirebaseAuth.getInstance();
        etNombreRM = findViewById(R.id.etNombreRM);
        etApellidoRM = findViewById(R.id.etApellidoRM);
        etCedulaRM = findViewById(R.id.etCedulaRM);
        etCorreoRM = findViewById(R.id.etCorreoRM);
        etDescripRM = findViewById(R.id.etDescripRM);
        etContraRM = findViewById(R.id.etContraRM);
        etValcontraRM = findViewById(R.id.etValcontraRM);
        btRegistrarMaestro = findViewById(R.id.btRegistrarMaestro);
        ivSelectImgRM = findViewById(R.id.ivSelectImgRM);
        tvSubirImgRM = findViewById(R.id.tvSubirImgRM);
        spinner = findViewById(R.id.spinnerCarrera);
        btbackregisterRM = findViewById(R.id.btbackregisterRM);

        ArrayList<Carrera> carreras = new ArrayList<>();
        carreras.add(new Carrera(1,"Ingeniería industrial"));
        carreras.add(new Carrera(2,"Sistema de información"));
        carreras.add(new Carrera(3,"Telemática"));

        ArrayAdapter<Carrera> adapter = new ArrayAdapter<>(this, R.layout.style_spinner, carreras);
        spinner.setAdapter(adapter);

        ivSelectImgRM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revisionPermisos();
            }
        });

        recuperarCon();
        btnRegistrar ();
        btnBack();

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
                MostrarMensaje(error.toString());
                //startActivity(new Intent(NuevoUsuarioActivity.this, ActivityMenu.class));
            }
        });


    }

    private void btnRegistrar () {

        btRegistrarMaestro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos() == true){
                    String carreraS = spinner.getSelectedItem().toString();
                    System.out.println(carreraS + "AHHHHHHHHHHHHHHHHHHHHHHHHH");

                    Query query = FirebaseDatabase.getInstance().getReference().child("Cedulas").orderByChild("cedula").equalTo(etCedulaRM.getText().toString().trim());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getChildrenCount() > 0) {
                                MostrarMensaje("La cedula ya se encuentra en uso.");
                            } else {
                                mAuthUser.createUserWithEmailAndPassword(etCorreoRM.getText().toString().trim(),etContraRM.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if(task.isSuccessful()){


                                            if (foto == true){

                                                String path = "imagenperfilmaestro/"+ mAuthUser.getCurrentUser().getUid() +".png";
                                                mStorageRef = FirebaseStorage.getInstance().getReference().child(path);

                                                mStorageRef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                StringURL = uri.toString();

                                                                RegistroMaestro registroMaestro = new RegistroMaestro(mAuthUser.getCurrentUser().getUid(),etNombreRM.getText().toString().trim(),etApellidoRM.getText().toString().trim(),etCedulaRM.getText().toString().trim(),etCorreoRM.getText().toString().trim(),etDescripRM.getText().toString().trim(),etValcontraRM.getText().toString().trim(),StringURL,"maestro","usuario", carreraS);

                                                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario").child(mAuthUser.getCurrentUser().getUid());
                                                                mDatabase.setValue(registroMaestro).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()){

                                                                            etNombreRM.setText("");
                                                                            etApellidoRM.setText("");
                                                                            etCedulaRM.setText("");
                                                                            etCorreoRM.setText("");
                                                                            etDescripRM.setText("");
                                                                            etContraRM.setText("");
                                                                            etValcontraRM.setText("");
                                                                            //ivSelectImgRM.setImageURI(StringURL2);

                                                                            mAuthUser.getCurrentUser().sendEmailVerification()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                MostrarMensaje("Te hemos enviado un correo de verificación");

                                                                                                mAuthUser.signOut();

                                                                                            }
                                                                                        }
                                                                                    });

                                                                        }else{
                                                                            MostrarMensaje("error "+task.getException().toString());
                                                                        }

                                                                    }
                                                                });

                                                            }
                                                        });

                                                    }
                                                });

                                            }else{

                                                RegistroMaestro registroMaestro = new RegistroMaestro(mAuthUser.getCurrentUser().getUid(),etNombreRM.getText().toString().trim(),etApellidoRM.getText().toString().trim(),etCedulaRM.getText().toString().trim(),etCorreoRM.getText().toString().trim(),etDescripRM.getText().toString().trim(),etValcontraRM.getText().toString().trim(),"","maestro","usuario", carreraS);

                                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario").child(mAuthUser.getCurrentUser().getUid());
                                                mDatabase.setValue(registroMaestro).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){

                                                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Cedulas");

                                                            String uidCedula = mDatabase.push().getKey();
                                                            Cedula cedulaModel = new Cedula(uidCedula, etCedulaRM.getText().toString().trim());

                                                            etNombreRM.setText("");
                                                            etApellidoRM.setText("");
                                                            etCedulaRM.setText("");
                                                            etCorreoRM.setText("");
                                                            etDescripRM.setText("");
                                                            etContraRM.setText("");
                                                            etValcontraRM.setText("");

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
                                                                                            MostrarMensaje("Te hemos enviado un correo de verificación");

                                                                                            mAuthUser.signOut();

                                                                                        }
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        MostrarMensaje("error "+task.getException().toString());
                                                                    }
                                                                }
                                                            });



                                                        }else{
                                                            MostrarMensaje("error "+task.getException().toString());
                                                        }

                                                    }
                                                });


                                            }


                                        }

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        });

    }

    private void btnBack () {
        btbackregisterRM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private Boolean validarCampos (){
        String passwordInput = etContraRM.getText().toString().trim();
        String CedulaImput = etCedulaRM.getText().toString().trim();

        if (etNombreRM.getText().toString().trim().isEmpty()){
            etNombreRM.setError("Campo Vacío");
            return false;
        }else if (etApellidoRM.getText().toString().trim().isEmpty()){
            etApellidoRM.setError("Campo Vacío");
            return false;
        }else if (etCedulaRM.getText().toString().trim().isEmpty()){
            etCedulaRM.setError("Campo Vacío");
            return false;
        }else if (!CEDULA_PATTERN.matcher(CedulaImput).matches()){
            etCedulaRM.setError("La cédula debe de tener 10 caracteres");
            return false;
        }else if (etCorreoRM.getText().toString().trim().isEmpty()){
            etCorreoRM.setError("Campo Vacío");
            return false;
        }
        //else if (!EMAIL_ADDRESS.matcher(etCorreoRM.getText().toString()).matches()){
       //     etCorreoRM.setError("Correo Inválido");
       //     return false;
       // }
        else if (etContraRM.getText().toString().trim().isEmpty()){
            MostrarMensaje("Contraseña vacía");
            return false;
        }else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()){
            etContraRM.setError("Formato Inválido");
            return false;
        }else if (!etContraRM.getText().toString().trim().equals(etValcontraRM.getText().toString().trim())){
            MostrarMensaje("La Contraseña No Coincide");
            return false;
        }else{
            //si esto se cumple que haga lo que debe hacer
            return true;
        }

    }



    private void MostrarMensaje (String mensaje){

        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();

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

    private void revisionPermisos(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this ,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this,"Por favor acepte los permisos requeridos para continuar.",Toast.LENGTH_SHORT).show();
            }
            else {

                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
            tvSubirImgRM.setImageURI(imageuri);
            foto = true;
        }
    }

    private void seleccionarArchivo(){
        String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1);


    }

}