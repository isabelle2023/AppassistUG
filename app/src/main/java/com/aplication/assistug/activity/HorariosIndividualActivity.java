package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.adapter.AdapterHorarioIndividual;
import com.aplication.assistug.model.DiasHorario;
import com.aplication.assistug.model.Usuario;

import java.util.ArrayList;

public class HorariosIndividualActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterHorarioIndividual adapterHorarioIndividual;
    ArrayList<DiasHorario> list;
    TextView tvAsignatura;
    String uidcurso, nombrecurso, semestre;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    ImageButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios_individual);
        uidcurso = getIntent().getStringExtra("uidcurso");
        nombrecurso = getIntent().getStringExtra("nombrecurso");
        semestre = getIntent().getStringExtra("semestre");
        tvAsignatura = findViewById(R.id.tvasignatura);
        mAuth = FirebaseAuth.getInstance();
        btnVolver = findViewById(R.id.btnregresar);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InformacionCursoActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                startActivity(intent);
            }
        });



        recyclerView = findViewById(R.id.rvhorario);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario").child(mAuth.getCurrentUser().getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    adapterHorarioIndividual = new AdapterHorarioIndividual(getApplicationContext(),list, usuario.getTipo(), uidcurso, nombrecurso, semestre);
                    recyclerView.setAdapter(adapterHorarioIndividual);

                    mDatabase = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("Cursos").child(uidcurso).child("horario");


                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                list.clear();
                                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    DiasHorario diasHorario = dataSnapshot.getValue(DiasHorario.class);
                                    list.add(diasHorario);
                                }
                                adapterHorarioIndividual.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        list = new ArrayList<>();



    }
}