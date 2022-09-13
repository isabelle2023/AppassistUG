package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.adapter.AdapterListAlumn;
import com.aplication.assistug.model.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListaAlumnosActivity extends AppCompatActivity {

    DatabaseReference mData;
    FirebaseAuth mauth;

    RecyclerView rvListAlumnos;

    AdapterListAlumn adapterListAlumn;
    private List<Usuario> usuarioList = new ArrayList<>();

    String uidcurso, semestre,nombrecurso;
    String uidalumno;
    ImageButton btnAgregarAlumnCurso;
    ImageButton btnRegresarLAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alumnos);

        rvListAlumnos = findViewById(R.id.rvListAlumnos);
        btnAgregarAlumnCurso = findViewById(R.id.btnAgregarAlumnCurso);
        btnRegresarLAC = findViewById(R.id.btnRegresarLAC);
        uidcurso = getIntent().getStringExtra("uidcurso");
        nombrecurso = getIntent().getStringExtra("nombrecurso");
        semestre = getIntent().getStringExtra("semestre");
        agregarAlumnoCurso ();
        regresarArtrasLA();
        llenarRvListAlumnos();

    }

    private void llenarRvListAlumnos () {

        mData =  FirebaseDatabase.getInstance().getReference().child("Cursos_Alumnos").child(uidcurso);

        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    for (DataSnapshot alumnouid : snapshot.getChildren()){

                        Usuario usuario = alumnouid.getValue(Usuario.class);

                      uidalumno = alumnouid.getKey();


                        System.out.println( uidalumno +"+++++++++++++++++");

                        Query query = FirebaseDatabase.getInstance().getReference().child("Usuario")
                                .orderByChild("uid").equalTo(uidalumno);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    for (DataSnapshot alumnos : snapshot.getChildren()){

                                        Usuario usuario1 = alumnos.getValue(Usuario.class);
                                        usuarioList.add(usuario1);
                                    }

                                    Collections.sort(usuarioList, new Comparator<Usuario>() {
                                        @Override
                                        public int compare(Usuario d1, Usuario d2) {
                                            return  d1.getApellido().compareToIgnoreCase(d2.getApellido());
                                        }

                                    });
                                    //llenando el recyclerview
                                    adapterListAlumn = new AdapterListAlumn(getApplicationContext(),usuarioList,uidcurso);
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
                                    rvListAlumnos.setLayoutManager(gridLayoutManager);
                                    rvListAlumnos.setAdapter(adapterListAlumn);
                                    adapterListAlumn.notifyDataSetChanged();

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
                System.out.println(error+"----------------------------------");
            }
        });



    }

    private void agregarAlumnoCurso () {

        btnAgregarAlumnCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaAlumnosActivity.this, RegistrarAlumnoCurso.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                startActivity(intent);
            }
        });

    }

    private void regresarArtrasLA () {

        btnRegresarLAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), InformacionCursoActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                startActivity(intent);
                finish();

            }
        });

    }

}