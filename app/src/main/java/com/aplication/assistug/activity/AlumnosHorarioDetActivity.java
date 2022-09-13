package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.adapter.AdapterAlumHorario;
import com.aplication.assistug.model.AlumnoCursos;
import com.aplication.assistug.model.AlumnosHorario;
import com.aplication.assistug.model.CursoModel;
import com.aplication.assistug.model.DiasHorario;

import java.util.ArrayList;
import java.util.List;

public class AlumnosHorarioDetActivity extends AppCompatActivity {

    DatabaseReference mData;
    DatabaseReference mData2, mData3;
    FirebaseAuth mAuth;

    RecyclerView rvMostrarHorarioAHD;
    LinearLayout LLnohorarioG;
    ImageButton btnRegresarAHD;
    TextView tvDiaAHD;

    AdapterAlumHorario adapterAlumHorario;

    private List<AlumnosHorario> alumnosHorarioList = new ArrayList<>();
    ArrayList<DiasHorario> list;
    String diaAlum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos_horario_det);

        mAuth = FirebaseAuth.getInstance();
        tvDiaAHD = findViewById(R.id.tvDiaAHD);
        btnRegresarAHD = findViewById(R.id.btnRegresarAHD);
        rvMostrarHorarioAHD = findViewById(R.id.rvMostrarHorarioAHD);
        LLnohorarioG = findViewById(R.id.LLnohorarioG);
        diaAlum = getIntent().getStringExtra("dia");

        regresarArtrasAHD ();
        tvDiaAHD.setText(diaAlum);
        llenarRecyclerV ();

    }

    private void llenarRecyclerV () {

        mData2 = FirebaseDatabase.getInstance().getReference().child("Cursos");
        mData2.orderByChild("maestrouid").equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        CursoModel curso = dataSnapshot.getValue(CursoModel.class);
                        Query query = FirebaseDatabase.getInstance().getReference().child("Cursos")
                                .child(curso.getUid()).child("horario").orderByChild("dia").equalTo(diaAlum);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    LLnohorarioG.setVisibility(View.GONE);
                                    for (DataSnapshot horario : snapshot.getChildren()){
                                        AlumnosHorario alumnosHorario = horario.getValue(AlumnosHorario.class);
                                        alumnosHorarioList.add(alumnosHorario);

                                        //llenando el recyclerview
                                        adapterAlumHorario = new AdapterAlumHorario(getApplicationContext(),alumnosHorarioList);
                                        rvMostrarHorarioAHD.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                                        rvMostrarHorarioAHD.setAdapter(adapterAlumHorario);
                                        adapterAlumHorario.notifyDataSetChanged();

                                    }


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

        mData =  FirebaseDatabase.getInstance().getReference().child("Alumnos_Curso").child(mAuth.getCurrentUser().getUid());
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("en a esnaaaaap");

                if (snapshot.exists()){
                    for (DataSnapshot materia : snapshot.getChildren()){

                        try{
                            AlumnoCursos alumnoCursos = materia.getValue(AlumnoCursos.class);
                            //System.out.println(materia.getValue()+"---------------------------------");

                            //diaAlum = "JUEVES";
                            Query query = FirebaseDatabase.getInstance().getReference().child("Cursos")
                                    .child(alumnoCursos.getUidCurso()).child("horario").orderByChild("dia").equalTo(diaAlum);

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        LLnohorarioG.setVisibility(View.GONE);
                                        for (DataSnapshot horario : snapshot.getChildren()){
                                            AlumnosHorario alumnosHorario = horario.getValue(AlumnosHorario.class);
                                            alumnosHorarioList.add(alumnosHorario);

                                            //llenando el recyclerview
                                            adapterAlumHorario = new AdapterAlumHorario(getApplicationContext(),alumnosHorarioList);
                                            rvMostrarHorarioAHD.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                                            rvMostrarHorarioAHD.setAdapter(adapterAlumHorario);
                                            adapterAlumHorario.notifyDataSetChanged();

                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }catch (Exception e){
                            System.out.println("error"+e);
                        }
                    }
                }else{
              //      Toast.makeText(getApplicationContext(), "No Existen", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void regresarArtrasAHD () {

        btnRegresarAHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AlumnosHorarioActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

}