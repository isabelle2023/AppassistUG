package com.aplication.assistug.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.helper.ExcelHelper;
import com.aplication.assistug.model.Usuario;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class RegistrarAlumnoCurso extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_MEMORY_ACCESS = 0;
    private static String fileType = "";
    private static String extensionXLS = "XLS";
    private static String extensionXLXS = "XLXS";
    ActivityResultLauncher<Intent> filePicker;
    private View mLayout;


    EditText etCedulaAlumn;
    TextView tvNomAlumn2, tvCorreoAlumn2RAC, tvprobar;
    Button btnBuscarAlumnRAC, btnRegistrarAlumnRAC, BtExcel;
    ImageButton btbackRAC;
    String uidcurso, uidalumno, nombrecurso, semestre;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseHorario;
    private DatabaseReference mDatabaseAlumnoCurso;
    private DatabaseReference mDatabaseUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_alumno_curso);
        mLayout = findViewById(R.id.register_alumno_curso_layout);
        tvprobar = findViewById(R.id.tvprobar);
        etCedulaAlumn = findViewById(R.id.etCedulaAlumn);
        tvNomAlumn2 = findViewById(R.id.tvNomAlumn2);
        tvCorreoAlumn2RAC = findViewById(R.id.tvCorreoAlumn2RAC);
        btnBuscarAlumnRAC = findViewById(R.id.btnBuscarAlumnRAC);
        btnRegistrarAlumnRAC = findViewById(R.id.btnRegistrarAlumnRAC);//hacer visible cuando se obtengan los datos
        btbackRAC = findViewById(R.id.btbackRAC);
        BtExcel = findViewById(R.id.BtExcel);

        uidcurso = getIntent().getStringExtra("uidcurso");
        nombrecurso = getIntent().getStringExtra("nombrecurso");
        semestre = getIntent().getStringExtra("semestre");

        BtExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileType = extensionXLS;
                OpenFilePicker();
            }
        });
        filePicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent intent1 = result.getData();

                        Uri uri = intent1.getData();
                        ReadExcelFile(RegistrarAlumnoCurso.this
                                , uri);

                    }
                });

        btnBack();
        btnBuscar();
        btnRegistrarAlumn();

    }


    private void btnRegistrarAlumn() {

        btnRegistrarAlumnRAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos_Alumnos").child(uidcurso).child(uidalumno);

                Map mapAlm = new HashMap();
                mapAlm.put("uidAlumno", uidalumno);
                mapAlm.put("nombre", tvNomAlumn2.getText().toString());
                mapAlm.put("uidCurso", uidcurso);


                mDatabase.setValue(mapAlm).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Alumnos_Curso").child(uidalumno).child(uidcurso);

                            Map mapAlmC = new HashMap();
                            System.out.println("aquiiiiiiiiiiiiiiiiiiiiii" + nombrecurso);
                            mapAlmC.put("nombreCurso", nombrecurso);
                            mapAlmC.put("uidAlumno", uidalumno);
                            mapAlmC.put("uidCurso", uidcurso);

                            mDatabase.setValue(mapAlmC).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mostrarMensaje("Se Registro Correctamente");
                                        btnRegistrarAlumnRAC.setVisibility(View.INVISIBLE);
                                        etCedulaAlumn.setText("--");
                                        tvNomAlumn2.setText("--");
                                        tvCorreoAlumn2RAC.setText("");
                                    }
                                }
                            });

                        } else {
                            System.out.println(task.getException().toString());
                            mostrarMensaje("Error: " + task.getException().toString());
                        }
                    }
                });

            }
        });

    }

    private void btnBuscar() {

        btnBuscarAlumnRAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cedulaa = etCedulaAlumn.getText() + "";
                System.out.println(cedulaa);
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario");
                mDatabase.orderByChild("cedula").equalTo(cedulaa).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //user = snapshot.getValue(Usuario.class);
                            System.out.println(snapshot);
                            for (DataSnapshot cedula : snapshot.getChildren()) {
                                try {
                                    System.out.println(cedula);
                                    Usuario user = cedula.getValue(Usuario.class);
                                    uidalumno = user.getUid();
                                    tvNomAlumn2.setText(user.getNombre() + " " + user.getApellido());
                                    tvCorreoAlumn2RAC.setText(user.getCorreo());
                                    if (user != null) {
                                        btnRegistrarAlumnRAC.setVisibility(View.VISIBLE);

                                    }

                                } catch (Exception e) {
                                    System.out.println(e + "eeeeeeeeeeeeeeeeeeeeeeeeee");
                                    continue;
                                }
                            }
                        } else {
                            Toast.makeText(RegistrarAlumnoCurso.this, "El alumno no esta registrado.", Toast.LENGTH_SHORT).show();
                            if (btnRegistrarAlumnRAC.getVisibility() == View.VISIBLE) {
                                tvNomAlumn2.setText("--");
                                tvCorreoAlumn2RAC.setText("--");
                                uidalumno = null;
                                btnRegistrarAlumnRAC.setVisibility(View.INVISIBLE);
                            }

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void btnBack() {

        btbackRAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListaAlumnosActivity.class);
                intent.putExtra("uidcurso", uidcurso);
                intent.putExtra("nombrecurso", nombrecurso);
                intent.putExtra("semestre", semestre);
                startActivity(intent);
                finish();
            }
        });

    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private boolean CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            Snackbar.make(mLayout, R.string.storage_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestStoragePermission();
                }
            }).show();


            return false;
        }
    }




    public void ReadExcelFile(Context context, Uri uri) {

        System.out.println(uri);
        String name = (new File(uri.getPath())).getName();
        System.out.println(name);

        try {
            FileInputStream importdb = new FileInputStream(getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());
            HSSFWorkbook workbook = new HSSFWorkbook(importdb);
            importdb.close();
            Sheet sheet1 = workbook.getSheetAt(0);

            ExcelHelper.insertExcelToSqlite( uidcurso,getApplicationContext(),sheet1, nombrecurso);

        } catch (Exception e) {
            System.out.println("aqui no po fa " + e);
        }
    }


    public void ChooseFile() {
        try {
            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE);

            if (fileType == extensionXLS)
                fileIntent.setType("application/vnd.ms-excel");
            else
                fileIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            filePicker.launch(fileIntent);
        } catch (Exception ex) {
            Toast("ChooseFile error: " + ex.getMessage().toString(), ex);

        }
    }

    void Toast(String message, Exception ex) {
        if (ex != null)
            Log.e("Error", ex.getMessage().toString());
        Toast.makeText(RegistrarAlumnoCurso.this, message, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_MEMORY_ACCESS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                OpenFilePicker();
            } else {
                Snackbar.make(mLayout, R.string.storage_access_denied,
                                Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void requestStoragePermission() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(RegistrarAlumnoCurso.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_MEMORY_ACCESS);

        } else {
            Snackbar.make(mLayout, R.string.storage_unavailable, Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_MEMORY_ACCESS);
        }
    }


    public void OpenFilePicker() {
        try {
            if (CheckPermission()) {
                ChooseFile();
            }
        } catch (ActivityNotFoundException e) {
        }

    }
}