package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplication.assistug.model.CursoModel;
import com.aplication.assistug.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.adapter.RvAsistenciaAlumnos;
import com.aplication.assistug.model.AlumnoAsistencia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class AsistenciaAlumnosMaestroActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String uidcurso, uidhorario, semestre, nombrecurso, nombremaestro, grupo, dia, fechareciba, tipouser;
    int horaini,horafin;
    TextView tvhorarioAD, tvdiaAD,tvfecha;
    ImageButton ivbackasistenciaalumnos;
    int alumnosconectados;
    LinearLayout LLnoAlumnosG;
    long fecha;
    String ts;
    RecyclerView RvAsistenciaAlumnos;
    RvAsistenciaAlumnos rvAdapterAsistenciaAlumno;
    RecyclerView.LayoutManager lm;
    List<AlumnoAsistencia> asistencia = new ArrayList<>();
    List<AlumnoAsistencia> asistenciademo = new ArrayList<>();
    ImageView ivDescargar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia_alumnos_maestro);
        mAuth = FirebaseAuth.getInstance();
        ivDescargar = findViewById(R.id.ivdownload);
        LLnoAlumnosG = findViewById(R.id.LLnoAlumnosG);
        tvdiaAD = findViewById(R.id.tvdiaAD);
        tvfecha = findViewById(R.id.tvfecha);
        tvhorarioAD = findViewById(R.id.tvhorarioAD);
        ivbackasistenciaalumnos = findViewById(R.id.ivbackasistenciaalumnos);
        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){
            uidcurso = parametros.getString("uidcurso");
            uidhorario = parametros.getString("uidhorario");
            nombrecurso =parametros.getString("nombrecurso");
            semestre = parametros.getString("semestre");
            dia = parametros.getString("dia");
            fechareciba = parametros.getString("fecha");
            horaini = parametros.getInt("horaini");
            horafin= parametros.getInt("horafin");
            tipouser = parametros.getString("tipouser");

        }
        tvfecha.setText(fechareciba);
        tvdiaAD.setText(dia);

        int number1 = horaini;
        String number = String.valueOf(number1);
        String[] digits = number.split("(?<=.)");
        String x = null;
        if(digits.length >1) {
            if (digits.length < 4) {
                x = 0 + "" + digits[0] + ":" + digits[1] + "" + digits[2];
            } else {
                x = digits[0] + "" + digits[1] + ":" + digits[2] + "" + digits[3];
            }
        }else{
            x = "00:00";
        }


        int number2 = horafin;
        String numberx = String.valueOf(number2);
        String[] digits2 = numberx.split("(?<=.)");
        String x2 = null;
        if(digits2.length >1 ){
            if(digits2.length<4){
                x2 = 0 + "" +digits2[0] + ":" + digits2[1] + "" +digits2[2];
            }else{
                x2 = digits2[0] + "" +digits2[1] + ":" + digits2[2] + "" +digits2[3];
            }

        }else{
            x2 = "00:00";
        }

        tvhorarioAD.setText(x +"HR - " + x2 + "HR");

        ivbackasistenciaalumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AsistenciaAlumnosMaestroActivity.this, AsistenciaDiasActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                intent.putExtra("tipouser", tipouser);
                startActivity(intent);
                finish();
            }
        });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos");
        mDatabase.orderByChild("uid").equalTo(uidcurso).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    LLnoAlumnosG.setVisibility(View.GONE);
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uidhorario).child("Alumnos");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
          //      asistenciademo.clear();
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        AlumnoAsistencia asistenciaAlumno = dataSnapshot.getValue(AlumnoAsistencia.class);
                        asistenciademo.add(asistenciaAlumno);

                        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Usuario").child(asistenciaAlumno.getUidAlumno());

                        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Usuario alumno = snapshot.getValue(Usuario.class);
                                    asistenciaAlumno.setNombre(alumno.getApellido() + " " + alumno.getNombre());
                                    asistenciaAlumno.setCedula(alumno.getCedula());
                                    if(!asistenciaAlumno.getStatus().equals("Falta")){
                                        alumnosconectados++;
                                    }
                                    asistencia.add(asistenciaAlumno);
                                    RvAsistenciaAlumnos = findViewById(R.id.rv_asistencia_alumno);
                                    // Establecemos que los elementos del RecyclerView se apilen verticalmente
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(AsistenciaAlumnosMaestroActivity.this,1);
                                    // Creamos un adaptador para el RecyclerView

                                    rvAdapterAsistenciaAlumno = new RvAsistenciaAlumnos(AsistenciaAlumnosMaestroActivity.this, asistencia, uidhorario, uidcurso);
                                    // Enlazamos el adaptador con el objeto RecyclerView
                                    RvAsistenciaAlumnos.setAdapter(rvAdapterAsistenciaAlumno);
                                    RvAsistenciaAlumnos.setLayoutManager(gridLayoutManager);

                                    Collections.sort(asistencia, new Comparator<AlumnoAsistencia>() {
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



        ivDescargar.setOnClickListener(new View.OnClickListener() {
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
        Date date = new Date(fecha);
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(date);
        sheet = workbook.createSheet("Asistencia");
        Row row = null;



        sheet.addMergedRegion(new CellRangeAddress(0,0, 0,15));

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
        cell.setCellValue("Semestre y grupo");
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(1,1, 9,11));

        cell = row.createCell(9);
        cell.setCellValue("Alumnos conectados");
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(1,1, 12,15));




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
        cell.setCellValue(semestre + " " + grupo);
        cell.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(2,2, 9,11));



        cell = row.createCell(9);
        cell.setCellValue(alumnosconectados);
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
        cell.setCellValue("Hora");
        cell.setCellStyle(style);

        cell = row.createCell(9);
        cell.setCellValue("Detalle");
        cell.setCellStyle(style);



        for(int i = 1; i <= asistencia.size(); i++) {
            row = sheet.createRow(i+4);
            sheet.addMergedRegion(new CellRangeAddress(i+4,i+4, 0,2));
            sheet.addMergedRegion(new CellRangeAddress(i+4,i+4, 3,5));
            sheet.addMergedRegion(new CellRangeAddress(i+4,i+4, 6,8));
            sheet.addMergedRegion(new CellRangeAddress(i+4,i+4, 9,11));
            sheet.addMergedRegion(new CellRangeAddress(i+4,i+4, 12,15));

            cell = row.createCell(0);
            cell.setCellValue(asistencia.get(i-1).getCedula());


            cell = row.createCell(3);
            cell.setCellValue(asistencia.get(i-1).getNombre());


            cell = row.createCell(6);
            if (asistencia.get(i-1).getHoraLlegada() == 0){
                cell.setCellValue("--");
            }
            else{
                Date hora = new Date(asistencia.get(i-1).getHoraLlegada());
                String time = new SimpleDateFormat("hh:mm:ss").format(hora);
                cell.setCellValue(time);
            }

            cell = row.createCell(9);
            cell.setCellValue(asistencia.get(i-1).getStatus());


        }

        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("Registro de asistencia "+ fechareciba);
        cell.setCellStyle(cellStyle);




        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Lista de asistencia - "+System.currentTimeMillis()+".xls");
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

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_SHORT).show();
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
}