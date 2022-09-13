package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplication.assistug.R;
import com.aplication.assistug.adapter.RvAsistenciaAlumnos;
import com.aplication.assistug.adapter.RvAsistenciaAlumnosAlumno;
import com.aplication.assistug.model.AlumnoAsistencia;
import com.aplication.assistug.model.AsistenciaDias;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AsistenciaAlumnosActivity extends AppCompatActivity {

    String uidcurso, uidhorario, semestre, nombrecurso, dia, tipouser, fecha;
    int horaini,horafin;
    private FirebaseAuth mAuth;
    ImageButton btbackasistencia;
    LinearLayout LLnoAlumnosGA, LLAlumno;
    TextView tvdiaA, tvhorariob, tvstatus, tvfechaA;
    ImageView Ivstatus;
    DatabaseReference mDatabase;
    RecyclerView RvAsistenciaAlumnosAlum;
    RvAsistenciaAlumnosAlumno rvAdapterAsistenciaAlumnosAlumno;
    RecyclerView.LayoutManager lm;
    List<AlumnoAsistencia> asistencia = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia_alumnos);
        btbackasistencia = findViewById(R.id.btbackasistencia);
        mAuth = FirebaseAuth.getInstance();
        Bundle parametros = this.getIntent().getExtras();
        LLnoAlumnosGA = findViewById(R.id.LLnoAlumnosGA);
        LLAlumno = findViewById(R.id.LLAlumno);
        if(parametros !=null){
            uidcurso = parametros.getString("uidcurso");
            uidhorario = parametros.getString("uidhorario");
            nombrecurso =parametros.getString("nombrecurso");
            semestre = parametros.getString("semestre");
            dia = parametros.getString("dia");
            horaini = parametros.getInt("horaini");
            horafin= parametros.getInt("horafin");
            tipouser = parametros.getString("tipouser");
            fecha = parametros.getString("fecha");
        }
        inicializar();
        btnBack();
    }

    private void inicializar() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia");
        mDatabase.orderByChild("fecha").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        AsistenciaDias asistenciaHorario = dataSnapshot.getValue(AsistenciaDias.class);
                        String uidhorario = asistenciaHorario.getUid();
                        DatabaseReference   mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uidhorario).child("Alumnos");
                        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                        AlumnoAsistencia asistenciaAlumno = dataSnapshot.getValue(AlumnoAsistencia.class);
                                        LLnoAlumnosGA.setVisibility(View.GONE);
                                        if(asistenciaAlumno.getUidAlumno().equals(mAuth.getCurrentUser().getUid())){
                                            asistencia.add(asistenciaAlumno);
                                        }
                                    }
                                    if(asistencia.isEmpty()){
                                        LLnoAlumnosGA.setVisibility(View.VISIBLE);
                                        LLAlumno.setVisibility(View.GONE);
                                    }else{
                                        LLnoAlumnosGA.setVisibility(View.GONE);
                                        LLAlumno.setVisibility(View.VISIBLE);
                                    }
                                }


                                rvAdapterAsistenciaAlumnosAlumno.notifyDataSetChanged();

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

        RvAsistenciaAlumnosAlum = findViewById(R.id.rv_asistencia_alumno_a);
        // Establecemos que los elementos del RecyclerView se apilen verticalmente
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        // Creamos un adaptador para el RecyclerView
        rvAdapterAsistenciaAlumnosAlumno = new RvAsistenciaAlumnosAlumno(this, asistencia, uidhorario, uidcurso);
        // Enlazamos el adaptador con el objeto RecyclerView
        RvAsistenciaAlumnosAlum.setAdapter(rvAdapterAsistenciaAlumnosAlumno);
        RvAsistenciaAlumnosAlum.setLayoutManager(gridLayoutManager);


    }
    private void btnBack(){
        btbackasistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AsistenciaAlumnosActivity.this, InformacionCursoActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                intent.putExtra("tipouser", tipouser);
                startActivity(intent);
                finish();
            }
        });
    }
}