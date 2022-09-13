package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplication.assistug.adapter.RvCursos;
import com.aplication.assistug.model.AlumnoCursos;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.model.CursoModel;
import com.aplication.assistug.model.Usuario;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InformacionCursoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    NavigationView navigationView;
    Button btalumnos, btqr, btasistencia, btnhorarios;
    ImageButton btbackinformacion;
    String  uidcurso, nombrecurso, semestre, tipo;
    TextView tvAsignatura, tvGeneracion;
    List<CursoModel> cursos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_curso);
        uidcurso = getIntent().getStringExtra("uidcurso");
        nombrecurso = getIntent().getStringExtra("nombrecurso");
        semestre = getIntent().getStringExtra("semestre");
        mAuth = FirebaseAuth.getInstance();
        btalumnos = findViewById(R.id.btalumnos);
        btqr = findViewById(R.id.btqr);
        btbackinformacion = findViewById(R.id.btbackinformacion);
        btasistencia = findViewById(R.id.btasistencia);
        btnhorarios = findViewById(R.id.btnhorarios);
        tvAsignatura = findViewById(R.id.tvasignatura);
        tvGeneracion = findViewById(R.id.tvGeneracion);


        cargarcurso();
        btback();
        ocultarOpciones();
        btnAlumnos();
        btnHorario();

    }

    private void btnHorario() {
        btnhorarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformacionCursoActivity.this, HorariosIndividualActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                startActivity(intent);

            }
        });
    }


    private void btback() {
        btbackinformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformacionCursoActivity.this, MenuActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cargarcurso() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos");
        mDatabase.orderByChild("uid").equalTo(uidcurso).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cursos.clear();
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        CursoModel curso = dataSnapshot.getValue(CursoModel.class);
                        cursos.add(curso);
                        tvAsignatura.setText(curso.getNombreCurso());
                        Date ini= new Date(curso.getFechaIni());
                        Date fin = new Date(curso.getFechaFin());
                        String fechaini = new SimpleDateFormat("dd/MM/yyyy").format(ini);
                        String fechafin = new SimpleDateFormat("dd/MM/yyyy").format(fin);
                        tvGeneracion.setText(fechaini + "  -  " + fechafin);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ocultarOpciones() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario")
                .child(mAuth.getCurrentUser().getUid());

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Usuario sesiones = snapshot.getValue(Usuario.class);

                    tipo = sesiones.getTipo();
                    if (sesiones.getTipo().equals("maestro")){
                        System.out.println("ERES ADMIN TIENES TODAS LA OPCIONES");
                        btalumnos.setVisibility(View.VISIBLE);
                        btalumnos.setEnabled(true);

                        btasistencia.setVisibility(View.VISIBLE);
                        btasistencia.setEnabled(true);

                        btnhorarios.setVisibility(View.VISIBLE);
                        btnhorarios.setEnabled(true);

                        btqr.setVisibility(View.VISIBLE);
                        btqr.setEnabled(true);

                            btasistencia.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(InformacionCursoActivity.this, AsistenciaDiasActivity.class);
                                    intent.putExtra("uidcurso" , uidcurso);
                                    intent.putExtra("nombrecurso" , nombrecurso);
                                    intent.putExtra("semestre", semestre);
                                    intent.putExtra("tipouser", tipo);
                                    startActivity(intent);

                                }
                            });



                        btqr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(InformacionCursoActivity.this, QrMaestroActivity.class);
                                intent.putExtra("uidcurso" , uidcurso);
                                intent.putExtra("nombrecurso" , nombrecurso);
                                intent.putExtra("semestre", semestre);
                                startActivity(intent);

                            }
                        });

                    }else if( sesiones.getTipo().equals("")){
                        System.out.println("ERES ADMIN TIENES TODAS LA OPCIONES");
                        btalumnos.setVisibility(View.VISIBLE);
                        btalumnos.setEnabled(true);

                        btnhorarios.setVisibility(View.VISIBLE);
                        btnhorarios.setEnabled(true);

                        btasistencia.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(InformacionCursoActivity.this, AsistenciaDiasActivity.class);
                                intent.putExtra("uidcurso" , uidcurso);
                                intent.putExtra("nombrecurso" , nombrecurso);
                                intent.putExtra("semestre", semestre);
                                intent.putExtra("tipouser", tipo);
                                startActivity(intent);

                            }
                        });



                        btqr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(InformacionCursoActivity.this, QrMaestroActivity.class);
                                intent.putExtra("uidcurso" , uidcurso);
                                intent.putExtra("nombrecurso" , nombrecurso);
                                intent.putExtra("semestre", semestre);
                                startActivity(intent);
                                finish();

                            }
                        });
                    }else {
                        System.out.println("ERES USUARIO SE OCULTAN CIERTAS OPCIONES");

                        btasistencia.setVisibility(View.VISIBLE);
                        btasistencia.setEnabled(true);

                        btnhorarios.setVisibility(View.VISIBLE);
                        btnhorarios.setEnabled(true);

                        btqr.setVisibility(View.VISIBLE);
                        btqr.setEnabled(true);

                        btqr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(InformacionCursoActivity.this, QrAlumnoActivity.class);
                                intent.putExtra("uidcurso" , uidcurso);
                                intent.putExtra("nombrecurso" , nombrecurso);
                                intent.putExtra("semestre", semestre);
                                startActivity(intent);
                                finish();

                            }
                        });


                            btasistencia.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(InformacionCursoActivity.this, AsistenciaAlumnosActivity.class);
                                    intent.putExtra("uidcurso" , uidcurso);
                                    intent.putExtra("nombrecurso" , nombrecurso);
                                    intent.putExtra("semestre", semestre);
                                    intent.putExtra("tipouser", tipo);
                                    startActivity(intent);

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

    private void btnAlumnos (){

        btalumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformacionCursoActivity.this, ListaAlumnosActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                startActivity(intent);

            }
        });

    }

}