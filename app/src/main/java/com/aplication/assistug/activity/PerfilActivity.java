package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aplication.assistug.model.RegistroMaestro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.aplication.assistug.R;
import com.aplication.assistug.model.Usuario;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PerfilActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private DatabaseReference mDatabase;
    Uri imageuri;
    TextView tvnombre, tvNombreedit, tvApellido,tvDescrip;
    CircularImageView cargarImagen;
    ImageButton ivcambiarimagen, ivcambiarnombre,ivcambiarapellido,ivcambiarcontra, ivbackmenuperfil,ivcambiardescrip;
    EditText ettexto,etcontra, etDescrip;
    LinearLayout Lndescrip;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=.*[0-9])" +     // al menos 1 número
            "(?=.*[a-z])"+      // al menos 1 letra min
            "(?=.*[A-Z])"+      // al menos 1 letra may
            "(?=.*[!@#&()–[{}]:;',?/*~$^+=<>])"+      // al menos 1 caracter esp
            "(?=\\S+$)" +       // no white spaces
            ".{8,15}" +         // al menos 8 caracteres y máximo 15
            "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        tvnombre = findViewById(R.id.tvnombre);
        tvNombreedit = findViewById(R.id.tvNombreedit);
        tvApellido = findViewById(R.id.tvApellido);
        tvDescrip = findViewById(R.id.tvDescrip);
        etDescrip = findViewById(R.id.etDescrip);
        cargarImagen = findViewById(R.id.imagen);
        ettexto = findViewById(R.id.etexto);
        Lndescrip = findViewById(R.id.Lndescrip);
        ivcambiardescrip = findViewById(R.id.ivcambiardescrip);
        ivcambiarnombre = findViewById(R.id.ivcambiarnombre);
        ivcambiarapellido = findViewById(R.id.ivcambiarapellido);
        ivcambiarcontra = findViewById(R.id.ivcambiarcontra);
        etcontra = findViewById(R.id.etcontraupdate);
        ivcambiarimagen = findViewById(R.id.ivcambiarimagen);
        ivbackmenuperfil = findViewById(R.id.ivbackmenuperfil);
        getdatos();

        ivbackmenuperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                finish();
            }
        });

        ivcambiarimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revisionPermisos();
            }
        });

        ivcambiarnombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowNameAlertDialog();
            }
        });

        ivcambiarapellido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowApellidoAlertDialog();
            }
        });

        ivcambiarcontra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPasswordAlertDialog();
            }
        });

        ivcambiardescrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {ShowDescripAlertDialog();}
        });
    }

    private void getdatos() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario").child(mAuth.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    RegistroMaestro user = snapshot.getValue(RegistroMaestro.class);
                    tvnombre.setText(user.getNombre());
                    tvNombreedit.setText(user.getNombre());
                    tvApellido.setText(user.getApellido());
                    if(user.getTipo().equals("maestro")){
                        tvDescrip.setVisibility(View.VISIBLE);
                        Lndescrip.setVisibility(View.VISIBLE);
                        etDescrip.setText(user.getDescripcion());
                    }

                    if (!user.getUrlfoto().isEmpty()) {
                        Picasso.get().load(user.getUrlfoto()).into(cargarImagen);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void ShowApellidoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(PerfilActivity.this).inflate(
                R.layout.layout_edittext,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitleDialogError)).setText("Cambiar Apellido");
        ((TextView) view.findViewById(R.id.textMessageDialogError)).setText("Escribe tu nuevo Apellido de usuario:");
        ((TextView) view.findViewById(R.id.buttonYes)).setText("Cambiar");
        ((TextView) view.findViewById(R.id.buttonNo)).setText("Cancelar");
        ettexto = view.findViewById(R.id.etexto);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(ettexto.getText().toString().trim().isEmpty()){
                    Toast.makeText(PerfilActivity.this,"Escribe un apellido",Toast.LENGTH_SHORT).show();
                }else{

                      Map map = new HashMap();
                      map.put("apellido", ettexto.getText().toString());
                      mDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                          @Override
                          public void onComplete(@NonNull Task task) {
                              tvApellido.setText(ettexto.getText().toString());
                              Toast.makeText(PerfilActivity.this,"Apellido cambiado",Toast.LENGTH_SHORT).show();
                          }
                      });
                    alertDialog.dismiss();
                }

            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow()!= null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    private void ShowNameAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(PerfilActivity.this).inflate(
                R.layout.layout_edittext,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitleDialogError)).setText("Cambiar nombre");
        ((TextView) view.findViewById(R.id.textMessageDialogError)).setText("Escribe tu nuevo nombre de usuario:");
        ((TextView) view.findViewById(R.id.buttonYes)).setText("Cambiar");
        ((TextView) view.findViewById(R.id.buttonNo)).setText("Cancelar");
        ettexto = view.findViewById(R.id.etexto);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(ettexto.getText().toString().trim().isEmpty()){
                    Toast.makeText(PerfilActivity.this,"Escribe un nombre",Toast.LENGTH_SHORT).show();
                }else{
                    Map map = new HashMap();
                    map.put("nombre", ettexto.getText().toString());
                    mDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            tvnombre.setText(ettexto.getText().toString());
                            tvNombreedit.setText(ettexto.getText().toString());
                            Toast.makeText(PerfilActivity.this,"Nombre cambiado",Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertDialog.dismiss();
                }

            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow()!= null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    private void ShowPasswordValAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(PerfilActivity.this).inflate(
                R.layout.layout_edittext_password_new,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitleDialogError)).setText("Cambiar Contraseña");
        ((TextView) view.findViewById(R.id.textMessageDialogError)).setText("Escribe tu nueva contraseña de usuario: Debe contener al menos 8 caracteres,  1 letra  mayúscula, 1 letra minúscula, 1 carácter especial y 1 número.");
        ((TextView) view.findViewById(R.id.buttonYes)).setText("Cambiar");
        ((TextView) view.findViewById(R.id.buttonNo)).setText("Cancelar");
        etcontra= view.findViewById(R.id.etcontraupdate);

        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if (validarCampos()){
                   mAuth.getCurrentUser().updatePassword(etcontra.getText().toString())
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {
                                       Toast.makeText(PerfilActivity.this,"contraseña actualizada",Toast.LENGTH_SHORT).show();

                                   }
                               }
                           });
                   alertDialog.dismiss();
               }

            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });
        if(alertDialog.getWindow()!= null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    private void ShowPasswordAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(PerfilActivity.this).inflate(
                R.layout.layout_edittext_password,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitleDialogError)).setText("Cambiar Contraseña");
        ((TextView) view.findViewById(R.id.textMessageDialogError)).setText("Escribe tu contraseña actual de usuario:");
        ((TextView) view.findViewById(R.id.buttonYes)).setText("Cambiar");
        ((TextView) view.findViewById(R.id.buttonNo)).setText("Cancelar");
        etcontra= view.findViewById(R.id.etcontraupdate);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(etcontra.getText().toString().trim().isEmpty()){
                    Toast.makeText(PerfilActivity.this,"Escribe tú contraseña",Toast.LENGTH_SHORT).show();
                }else{
                        AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), etcontra.getText().toString()); //Guardamos como creedenciales el email guardado y la contraseña que ha introducido
                        mAuth.getCurrentUser().reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(PerfilActivity.this, "Contraseña correcta", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                ShowPasswordValAlertDialog();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PerfilActivity.this, "contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }

                }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow()!= null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    private void ShowDescripAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(PerfilActivity.this).inflate(
                R.layout.layout_edittext,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitleDialogError)).setText("Cambiar Descripción");
        ((TextView) view.findViewById(R.id.textMessageDialogError)).setText("Escribe tu nueva descripción:");
        ((TextView) view.findViewById(R.id.buttonYes)).setText("Cambiar");
        ((TextView) view.findViewById(R.id.buttonNo)).setText("Cancelar");
        ettexto = view.findViewById(R.id.etexto);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(ettexto.getText().toString().trim().isEmpty()){
                    Toast.makeText(PerfilActivity.this,"Escribe una descripción",Toast.LENGTH_SHORT).show();
                }else{

                    Map map = new HashMap();
                    map.put("descripcion", ettexto.getText().toString());
                    mDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            etDescrip.setText(ettexto.getText().toString());
                            Toast.makeText(PerfilActivity.this,"Descripción a cambiado",Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.dismiss();
                }

            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow()!= null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    private void revisionPermisos(){
        if (ContextCompat.checkSelfPermission(PerfilActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(PerfilActivity.this ,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(PerfilActivity.this,"Por favor acepte los permisos requeridos para continuar.",Toast.LENGTH_SHORT).show();
            }
            else {

                ActivityCompat.requestPermissions(PerfilActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

        }
        else {
            seleccionarArchivo();
        }
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1 && data != null){
            imageuri = data.getData();
            cargarImagen.setImageURI(imageuri);
            String path = "imagenperfil/"+ mAuth.getCurrentUser().getUid() +".png";
            mStorageRef = FirebaseStorage.getInstance().getReference().child(path);
            mStorageRef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onSuccess(Uri uri) {
                            String StringURL = uri.toString();

                            Map<String,Object> map = new HashMap<>();
                            map.put("urlfoto", StringURL);
                            mDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(PerfilActivity.this, "Exitoso",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {

                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PerfilActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void seleccionarArchivo(){
        String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");//pendiente falta que seleccione solo pdf
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1);


    }

    private Boolean validarCampos (){
        String passwordInput = etcontra.getText().toString().trim();

        if (etcontra.getText().toString().trim().isEmpty()){
            MostrarMensaje("Contraseña vacía");
            return false;
        }else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()){
            etcontra.setError("Formato Inválido");
            return false;
        }else{
            //si esto se cumple que haga lo que debe hacer
            return true;
        }

    }

    private void MostrarMensaje (String mensaje){

        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();

    }
}