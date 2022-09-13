package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplication.assistug.adapter.RvAsistenciaAlumnos;
import com.aplication.assistug.model.AlumnoAsistencia;
import com.aplication.assistug.model.CursoModel;
import com.aplication.assistug.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.adapter.RvAsistenciaDias;
import com.aplication.assistug.model.AsistenciaDias;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AsistenciaDiasActivity extends AppCompatActivity {
    String uidcurso, nombrecurso, semestre, tipouser, nombremaestro, grupo;
    ImageButton ivbackasistenciadias;
    RecyclerView RvAsistenciaDias;
    Button btnReporteG;
    RvAsistenciaDias rvAdapterAsistenciaDias;
    RecyclerView.LayoutManager lm;
    LinearLayout LLnodias;
    List<AsistenciaDias>  asistencia = new ArrayList<>();
    List<AlumnoAsistencia> asistenciademo = new ArrayList<>();
    List<AlumnoAsistencia> asistenciaG = new ArrayList<>();
    String uidhorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia_dias);
        LLnodias = findViewById(R.id.LLnodias);
        ivbackasistenciadias = findViewById(R.id.ivbackasistenciadias);
        btnReporteG = findViewById(R.id.btnReporteG);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uidcurso = getIntent().getStringExtra("uidcurso");
        nombrecurso = getIntent().getStringExtra("nombrecurso");
        semestre = getIntent().getStringExtra("semestre");
        tipouser = getIntent().getStringExtra("tipouser");

        ivbackasistenciadias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AsistenciaDiasActivity.this, InformacionCursoActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                intent.putExtra("tipouser", tipouser);
                startActivity(intent);
                finish();
            }
        });

        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Cursos");
        mDatabase2.orderByChild("uid").equalTo(uidcurso).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        CursoModel curso = dataSnapshot.getValue(CursoModel.class);
                        nombremaestro = curso.getMaestro();
                        nombrecurso = curso.getNombreCurso();
                        semestre = curso.getSemestre();
                        grupo = curso.getGrupo();

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia");
        mDatabase.orderByChild("fecha").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                asistencia.clear();
                if(snapshot.exists()){
                    LLnodias.setVisibility(View.GONE);
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        AsistenciaDias dias = dataSnapshot.getValue(AsistenciaDias.class);
                        asistencia.add(dias);
                        uidhorario = dias.getUid();

                        DatabaseReference mDatabase3 = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uidhorario).child("Alumnos");
                        mDatabase3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //      asistenciademo.clear();
                                if(snapshot.exists()){
                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                        AlumnoAsistencia asistenciaAlumno = dataSnapshot.getValue(AlumnoAsistencia.class);
                                        DatabaseReference mDatabase4 = FirebaseDatabase.getInstance().getReference().child("Usuario").child(asistenciaAlumno.getUidAlumno());

                                        mDatabase4.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    Usuario alumno = snapshot.getValue(Usuario.class);
                                                    asistenciaAlumno.setNombre(alumno.getApellido() + " " + alumno.getNombre());
                                                    asistenciaAlumno.setCedula(alumno.getCedula());

                                                    asistenciaG.add(asistenciaAlumno);

                                                    Collections.sort(asistenciaG, new Comparator<AlumnoAsistencia>() {
                                                        @Override
                                                        public int compare(AlumnoAsistencia d1, AlumnoAsistencia d2) {
                                                            return  d1.getNombre().compareToIgnoreCase(d2.getNombre());
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




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                    }


                }
                rvAdapterAsistenciaDias.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Enlazamos la interfaz gráfica del RecyclerView con el código
        RvAsistenciaDias = findViewById(R.id.rv_asistenciadias);
        // Establecemos que los elementos del RecyclerView se apilen verticalmente
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        // Creamos un adaptador para el RecyclerView
        rvAdapterAsistenciaDias = new RvAsistenciaDias(this, asistencia,uidcurso, nombrecurso, tipouser);
        // Enlazamos el adaptador con el objeto RecyclerView
        RvAsistenciaDias.setAdapter(rvAdapterAsistenciaDias);
        RvAsistenciaDias.setLayoutManager(gridLayoutManager);


        btnReporteG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()) {
                    exportar();
                }
            }
        });
    }




    private void exportar() {
        Workbook workbook = new HSSFWorkbook();
        Cell cell = null;
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        Font font = workbook.createFont();
        font.setColor(HSSFColor.WHITE.index);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);


        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.SEA_GREEN.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFont(font);
        Sheet sheet = null;
        sheet = workbook.createSheet("Asistencia");
        Row row = null;



        sheet.addMergedRegion(new CellRangeAddress(0,0, 0,11));

        row = sheet.createRow(1);
        sheet.addMergedRegion(new CellRangeAddress(1,1, 0,2));

        cell = row.createCell(0);
        cell.setCellValue("Curso");
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(1,1, 3,5));

        cell = row.createCell(3);
        cell.setCellValue("Maestro");
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(1,1, 6,8));

        cell = row.createCell(6);
        cell.setCellValue("Semestre");
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(1,1, 9,11));

        cell = row.createCell(9);
        cell.setCellValue("Grupo");
        cell.setCellStyle(style);



        //cell oculta para evitar que la ultima columna se apriete
        cell = row.createCell(17);
        cell.setCellValue("");

        row = sheet.createRow(2);
        sheet.addMergedRegion(new CellRangeAddress(2,2, 0,2));
        cell = row.createCell(0);
        cell.setCellValue(nombrecurso);
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(2,2, 3,5));

        cell = row.createCell(3);
        cell.setCellValue(nombremaestro);
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(2,2, 6,8));

        cell = row.createCell(6);
        cell.setCellValue(semestre);
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(2,2, 9,11));
        cell = row.createCell(9);
        cell.setCellValue( grupo);
        cell.setCellStyle(style);




        sheet.addMergedRegion(new CellRangeAddress(3,4, 0,2));
        sheet.addMergedRegion(new CellRangeAddress(3,4, 3,5));
        sheet.addMergedRegion(new CellRangeAddress(3,4, 6,8));
        sheet.addMergedRegion(new CellRangeAddress(3,4, 9,11));

        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("Cédula");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("Alumno");
        cell.setCellStyle(style);


        cell = row.createCell(6);
        cell.setCellValue("Fecha");
        cell.setCellStyle(style);


        cell = row.createCell(9);
        cell.setCellValue("Asistencia");
        cell.setCellStyle(style);


      /*  row = sheet.createRow(4);
        for(int y = 0; y<asistencia.size(); y++){

            asistencia.get(y).getFecha();

            cell = row.createCell(6+y);
            cell.setCellValue(asistencia.get(y).getFecha());
            cell.setCellStyle(style);
        }*/



        for(int i = 1; i <= asistenciaG.size(); i++) {
            row = sheet.createRow(i+4);
            sheet.addMergedRegion(new CellRangeAddress(i+4,i+4, 0,2));
            sheet.addMergedRegion(new CellRangeAddress(i+4,i+4, 3,5));
            sheet.addMergedRegion(new CellRangeAddress(i+4,i+4, 6,8));
            sheet.addMergedRegion(new CellRangeAddress(i+4,i+4, 9,11));

            cell = row.createCell(0);
            cell.setCellValue(asistenciaG.get(i-1).getCedula());


            cell = row.createCell(3);
            cell.setCellValue(asistenciaG.get(i-1).getNombre());


            cell = row.createCell(6);

            Date date = new Date(asistenciaG.get(i-1).getFecha());
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(date);
            cell.setCellValue(timeStamp);

            cell = row.createCell(9);
                cell.setCellValue(asistenciaG.get(i-1).getStatus());


        }

        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("Registro de asistencia" );
        cell.setCellStyle(cellStyle);




        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Lista de asistencia  general - "+System.currentTimeMillis()+".xls");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            mostrarMensaje("El expendiente fue generado");
        } catch (FileNotFoundException e) {
            mostrarMensaje(e.toString());
        } catch (IOException e) {
            mostrarMensaje(e.toString());
        }


    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_SHORT).show();
    }
}