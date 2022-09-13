package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.aplication.assistug.R;
import com.aplication.assistug.model.AlumnoCursos;
import com.aplication.assistug.model.DiasHorario;
import com.aplication.assistug.model.HorarioModel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QrMaestroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mDatabaseAlumnoCurso, mDatabaseAsistencia;
    ImageButton btbackqrmaestro;
    TextView tvfecha, tvhorario, tvdia;
    String uidcurso, asistenciaDia, uidhorario, nombrecurso, semestre;
    boolean diaencontrado = false;
    int  asistenciaHoraInicio, asistenciaHoraFin;
    HorarioModel asistenciaActual = null;    Boolean diacorrecto = false;
    int fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_maestro);
        semestre = getIntent().getStringExtra("semestre");
        uidcurso = getIntent().getStringExtra("uidcurso");
        nombrecurso = getIntent().getStringExtra("nombrecurso");

        mAuth = FirebaseAuth.getInstance();
        btbackqrmaestro = findViewById(R.id.btbackqrmaestro);
        tvfecha = findViewById(R.id.tvfecha);
        tvhorario = findViewById(R.id.tvhorario);
        tvdia = findViewById(R.id.tvdia);
        Calendar now = Calendar.getInstance();
        Date currentTime = Calendar.getInstance().getTime();
        // Array con los dias de la semana
        String[] strDays = new String[]{
                "DOMINGO",
                "LUNES",
                "MARTES",
                "MIERCOLES",
                "JUEVES",
                "VIERNES",
                "SABADO"};
        // El dia de la semana inicia en el 1 mientras que el array empieza en el 0
        String diasemana = strDays[now.get(Calendar.DAY_OF_WEEK) - 1];

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("horario");
        mDatabase.orderByChild("dia").equalTo(diasemana).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        DiasHorario diasHorario = dataSnapshot.getValue(DiasHorario.class);
                        diasHorario.getDia();
                        if(diasHorario.getDia().equals(diasemana)){
                            diasHorario.getHoraInicio();
                            diasHorario.getHoraFin();
                            int dia = currentTime.getHours()*100 + currentTime.getMinutes();
                            if(dia >= diasHorario.getHoraInicio() && dia<= diasHorario.getHoraFin()){
                                diacorrecto = true;

                                int number1 = diasHorario.getHoraInicio();
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


                                int number2 = diasHorario.getHoraFin();
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

                                tvhorario.setText( x +" - "+x2);
                                asistenciaDia = diasemana;
                                asistenciaHoraInicio = diasHorario.getHoraInicio();
                                asistenciaHoraFin  = diasHorario.getHoraFin();
                                tvdia.setText(diasHorario.getDia());
                                generarAsistencia();
                            }
                        }
                    }
                }
                if(!diacorrecto){
                    Toast.makeText(QrMaestroActivity.this, "Fuera del horario para registrar asistencia", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(QrMaestroActivity.this, InformacionCursoActivity.class);
                    intent.putExtra("uidcurso" , uidcurso);
                    intent.putExtra("nombrecurso" , nombrecurso);
                    intent.putExtra("semestre", semestre);
                    startActivity(intent);
                    finish();
                }
                diacorrecto = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        String date = sdf.format(new Date());
        tvfecha.setText( date);



        btbackqrmaestro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QrMaestroActivity.this, InformacionCursoActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                startActivity(intent);
                finish();
            }
        });
    }

    private void generarAsistencia(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia");

        String uid = mDatabase.push().getKey();
        Calendar calendar = Calendar.getInstance();
        long timeMilli2 = calendar.getTimeInMillis();


        mDatabase.orderByChild("fecha").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        HorarioModel asistenciadias = dataSnapshot.getValue(HorarioModel.class);
                        uidhorario = asistenciadias.getUid();
                        Date dateactual = new Date(timeMilli2);
                        String fechaactual = new SimpleDateFormat("dd/MM/yyyy").format(dateactual);

                        Date dateguardada = new Date(asistenciadias.getFecha());

                        String fechaguardada = new SimpleDateFormat("dd/MM/yyyy").format(dateguardada);

                            if(fechaactual.equals(fechaguardada)){
                              diaencontrado = true;
                              asistenciaActual = asistenciadias;
                            }
                        }

                    if(diaencontrado){
                        ImageView imgqr = findViewById(R.id.imgqr);
                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(asistenciaActual.getUid(), BarcodeFormat.QR_CODE, 750, 750);
                            imgqr.setImageBitmap(bitmap);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        mDatabase =  FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uid);
                        Map mapAsist = new HashMap();
                        mapAsist.put("uid",uid);
                        mapAsist.put("dia", asistenciaDia);
                        mapAsist.put("fecha",timeMilli2);
                        mapAsist.put("horaInicio",asistenciaHoraInicio);
                        mapAsist.put("horaFin", asistenciaHoraFin);

                        mDatabase.setValue(mapAsist).addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mDatabaseAlumnoCurso = FirebaseDatabase.getInstance().getReference().child("Cursos_Alumnos").child(uidcurso);
                                    mDatabaseAlumnoCurso.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Calendar calendar = Calendar.getInstance();
                                            long timeMilli2 = calendar.getTimeInMillis();
                                            for (DataSnapshot alumno : snapshot.getChildren()){
                                                try {
                                                    AlumnoCursos user = alumno.getValue(AlumnoCursos.class);
                                                    Map<String, Object> map = new HashMap<String, Object>();
                                                    map.put("uidAlumno", user.getUidAlumno());
                                                    map.put("uidCurso", user.getUidCurso());
                                                    map.put("fecha",timeMilli2);
                                                    map.put("status", "Falta");
                                                    map.put("horaLlegada", 0);
                                                    mDatabaseAsistencia = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uid).child("Alumnos").child(user.getUidAlumno());
                                                    mDatabaseAsistencia.setValue(map);

                                                    // mDatabaseAsistencia =  FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uid);
                                                    ImageView imgqr = findViewById(R.id.imgqr);
                                                    try {
                                                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                                        Bitmap bitmap = barcodeEncoder.encodeBitmap(uid, BarcodeFormat.QR_CODE, 750, 750);
                                                        imgqr.setImageBitmap(bitmap);
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }catch (Exception e) {
                                                    System.out.println(e);
                                                    continue;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }else {
                                    System.out.println(task.getException().toString());
                                    mostrarMensaje("Error: "+task.getException().toString());
                                }
                            }
                        });
                    }
                }else{
                        mDatabase =  FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uid);
                        Map mapAsist = new HashMap();
                        mapAsist.put("uid",uid);
                        mapAsist.put("dia", asistenciaDia);
                        mapAsist.put("fecha",timeMilli2);
                        mapAsist.put("horaInicio",asistenciaHoraInicio);
                        mapAsist.put("horaFin", asistenciaHoraFin);

                        mDatabase.setValue(mapAsist).addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mDatabaseAlumnoCurso = FirebaseDatabase.getInstance().getReference().child("Cursos_Alumnos").child(uidcurso);
                                    mDatabaseAlumnoCurso.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Calendar calendar = Calendar.getInstance();
                                            long timeMilli2 = calendar.getTimeInMillis();
                                            for (DataSnapshot alumno : snapshot.getChildren()){
                                                try {
                                                    AlumnoCursos user = alumno.getValue(AlumnoCursos.class);
                                                    Map<String, Object> map = new HashMap<String, Object>();
                                                    map.put("uidAlumno", user.getUidAlumno());
                                                    map.put("uidCurso", user.getUidCurso());
                                                    map.put("fecha",timeMilli2);
                                                    map.put("status", "Falta");
                                                    map.put("horaLlegada", 0);
                                                    mDatabaseAsistencia = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uid).child("Alumnos").child(user.getUidAlumno());
                                                    mDatabaseAsistencia.setValue(map);

                                                    // mDatabaseAsistencia =  FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uid);
                                                    ImageView imgqr = findViewById(R.id.imgqr);
                                                    try {
                                                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                                        Bitmap bitmap = barcodeEncoder.encodeBitmap(uid, BarcodeFormat.QR_CODE, 750, 750);
                                                        imgqr.setImageBitmap(bitmap);
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }catch (Exception e) {
                                                    System.out.println(e);
                                                    continue;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }else {
                                    System.out.println(task.getException().toString());
                                    mostrarMensaje("Error: "+task.getException().toString());
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

    private void mostrarMensaje (String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}