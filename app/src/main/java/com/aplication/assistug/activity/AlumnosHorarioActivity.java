package com.aplication.assistug.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.aplication.assistug.model.AlumnosHorario;

import java.util.ArrayList;
import java.util.List;

public class AlumnosHorarioActivity extends AppCompatActivity {

    DatabaseReference mData;
    FirebaseAuth mauth;
    ImageButton Ibsalirsemana;
    ListView listvHorarioAlumnos;
    TextView tvDiaSemana;
    ArrayList<String> diasSemana = new ArrayList<>();

    private List<AlumnosHorario> alumnosHorarioList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos_horario);

        Ibsalirsemana = findViewById(R.id.Ibsalirsemana);

        Ibsalirsemana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlumnosHorarioActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mauth = FirebaseAuth.getInstance();
        //rvHorarioAlumnos = findViewById(R.id.rvHorarioAlumnos);
        listvHorarioAlumnos = findViewById(R.id.listvHorarioAlumnos);
        tvDiaSemana = findViewById(R.id.tvDiaSemana);

        regresarArtrasAHA();
        listavie();



    }

    private void listavie (){

        diasSemana = new ArrayList<>();
        diasSemana.add("LUNES");
        diasSemana.add("MARTES");
        diasSemana.add("MIERCOLES");
        diasSemana.add("JUEVES");
        diasSemana.add("VIERNES");
        diasSemana.add("SÁBADO");

        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this,R.layout.item_dias_semana,R.id.tvDiaSemana,diasSemana);
        listvHorarioAlumnos.setAdapter(adapt);

        listvHorarioAlumnos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(AlumnosHorarioActivity.this, listvHorarioAlumnos.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AlumnosHorarioDetActivity.class);
                intent.putExtra("dia",listvHorarioAlumnos.getItemAtPosition(i).toString());
                startActivity(intent);

            }
        });

    }

   private void regresarArtrasAHA () {

       //Intent intent = new Intent(getApplicationContext(), AlumnosHorarioDetActivity.class);
       //startActivity(intent);
       //finish();

   }

}