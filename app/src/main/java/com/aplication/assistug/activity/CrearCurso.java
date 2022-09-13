package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aplication.assistug.R;
import com.aplication.assistug.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CrearCurso extends AppCompatActivity {

    EditText etNomMateria,etNomMaestro;
    EditText etSemestreCH, etGrupoCH;
    EditText etDescripcionCH;
    ImageView ivCrearCurso;
    ImageView ivSelectImgCurso;
    long fechaIni, fechaFin;
    Spinner spCarrera;
    Button btnCrearCH;
    String uidmaestro;
    boolean uidencotrado;
    ImageButton iBbackcrearcurso;
    ArrayList<String> carreras;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private DatePickerDialog datePickerDialog, datePickerDialog2;
    private Button btnPicker1, btnPicker2;
    String StringURL = "";
    boolean foto = false;
    Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_curso);
        btnPicker1 = findViewById(R.id.btnPicker1);
        btnPicker2 = findViewById(R.id.btnPicker2);
        spCarrera = findViewById(R.id.spCarrera);
        etGrupoCH = findViewById(R.id.etGrupoCH);
        etNomMaestro = findViewById(R.id.etNomMaestro);
        iBbackcrearcurso = findViewById(R.id.iBbackcrearcurso);
        btnCrearCH = findViewById(R.id.btnCrearCH);
        etNomMateria = findViewById(R.id.etNomMateria);
        etSemestreCH = findViewById(R.id.etSemestreCH);
        etDescripcionCH = findViewById(R.id.etDescripcionCH);
        ivCrearCurso = findViewById(R.id.ivCrearCurso);
        ivSelectImgCurso = findViewById(R.id.ivSelectImgCurso);
        spinnerCarrerra();
        initPicker();
        initPicker2();
        iBbackcrearcurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrearCurso.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ivSelectImgCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revisionPermisos();
            }
        });
        btnCrearHorario();

        btnPicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        btnPicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog2.show();
            }
        });

    }

    private void spinnerCarrerra() {
        carreras = new ArrayList<>();
        carreras.add("Seleccione");
        carreras.add("TELEMÁTICA");
        carreras.add("INGENIERIA INDUSTRIAL");
        carreras.add("SISTEMA INFORMACIÓN");


        ArrayAdapter<String> adapterD = new ArrayAdapter<>(this,R.layout.item_perzonalizado_list_spinner,carreras);
        spCarrera.setAdapter(adapterD);
    }

    private Boolean validacion () {


        if (etNomMaestro.getText().toString().isEmpty()) {
            etNomMaestro.setError("Maestro vacío ");
            return false;
        }if (etNomMateria.getText().toString().isEmpty()){
            etNomMateria.setError("Nombre materia vacío");
            return false;
        }if(etGrupoCH.getText().toString().isEmpty()){
            etGrupoCH.setError("Grupo vacío");
            return false;
        }if (etSemestreCH.getText().toString().isEmpty()){
            etSemestreCH.setError("Semestre vacío");
            return false;
        }if (spCarrera.getSelectedItem().toString().equals("Selecciona")) {
            mostrarMensaje("Seleccione una carrera");
            return false;
        }if(fechaIni == 0L){
            btnPicker1.setError("Seleccione una fecha de inicio");
            return false;
        }if(fechaFin == 0L){
            btnPicker2.setError("Seleccione una fecha de fin");
            return false;
        }if(fechaIni>=fechaFin) {
            btnPicker1.setError("La fecha de fin debe ser mayor");
            return false;
        }if (etDescripcionCH.getText().toString().isEmpty()) {
            etDescripcionCH.setError("Descripcion vacío");
            return false;
        }else{
            return true;
        }





    }

    private void revisionPermisos(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this ,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this,"Por favor acepte los permisos requeridos para continuar",Toast.LENGTH_SHORT).show();
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
            ivCrearCurso.setImageURI(imageuri);
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

    private void btnCrearHorario () {
        btnCrearCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cedulaa = etNomMaestro.getText() + "";
                System.out.println(cedulaa);
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario");
                mDatabase.orderByChild("cedula").equalTo(cedulaa).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            System.out.println(snapshot);
                            for (DataSnapshot cedula : snapshot.getChildren()) {
                                try {
                                    System.out.println(cedula);
                                    Usuario user = cedula.getValue(Usuario.class);
                                    uidmaestro= user.getUid();
                                    System.out.println(uidmaestro);
                                        uidencotrado = true;

                                    if (validacion() == true) {
                                        Intent intent = new Intent(getApplicationContext(), CrearHorario.class);
                                        intent.putExtra("materia", etNomMateria.getText().toString());
                                        intent.putExtra("semestre", etSemestreCH.getText().toString());
                                        intent.putExtra("descripcion", etDescripcionCH.getText().toString());
                                        intent.putExtra("grupo", etGrupoCH.getText().toString());
                                        intent.putExtra("carrera", spCarrera.getSelectedItem().toString());
                                        intent.putExtra("imagencurso", imageuri.toString());
                                        intent.putExtra("fechainicio", fechaIni);
                                        intent.putExtra("fechafin", fechaFin);
                                        intent.putExtra("uidmaestro", uidmaestro);
                                        intent.putExtra("foto", foto);
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                        }else{
                            mostrarMensaje("Maestro no encontrado");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                            mostrarMensaje("Maestro no encontrado");
                    }
                });


            }
        });

    }
    private void mostrarMensaje (String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void initPicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                btnPicker1.setText(date);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // I assume d-M, you may refer to M-d for month-day instead.
                Date dates = null; // You will need try/catch around this
                try {
                    dates = formatter.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                fechaIni = dates.getTime();
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);


    }

    private void initPicker2() {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                btnPicker2.setText(date);

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // I assume d-M, you may refer to M-d for month-day instead.
                Date dates = null; // You will need try/catch around this
                try {
                    dates = formatter.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                fechaFin = dates.getTime();
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog2 = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        return  day + "/" +  month + "/" + year;
    }

}