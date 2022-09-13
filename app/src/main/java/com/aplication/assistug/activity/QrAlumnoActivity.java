package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.aplication.assistug.R;
import com.aplication.assistug.model.DiasHorario;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QrAlumnoActivity extends AppCompatActivity {
    private DatabaseReference mDatabase, mDatabaseAsistencia;
    private FirebaseAuth mAuth;
    Button btscan;
    String escaneo;
    String uidcurso, diasemana, semestre, nombrecurso;
    ImageButton btbackqralumno;
    int horaactual, asistenciaHoraInicio, asistenciaHoraFin;
    TextView tvmateria, tvhorario, tvdia, tvfecha;
    Boolean diacorrecto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_alumno);
        btscan = findViewById(R.id.btscan);
        mAuth = FirebaseAuth.getInstance();
        tvmateria = findViewById(R.id.tvmateria);
        tvdia = findViewById(R.id.tvdiaa);
        tvhorario = findViewById(R.id.tvhorarioa);
        tvfecha = findViewById(R.id.tvfechaa);
        btbackqralumno = findViewById(R.id.btbackqralumno);

        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){
            semestre = getIntent().getStringExtra("semestre");
            uidcurso = getIntent().getStringExtra("uidcurso");
            nombrecurso = getIntent().getStringExtra("nombrecurso");
        }

        btnback();
        inicializar();
        btnescanear();
    }

    private void btnescanear() {
        btscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(QrAlumnoActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });
    }

    private void inicializar() {
        Calendar now = Calendar.getInstance();
        Date currentTime = Calendar.getInstance().getTime();
        String[] strDays = new String[]{
                "DOMINGO",
                "LUNES",
                "MARTES",
                "MIERCOLES",
                "JUEVES",
                "VIERNES",
                "SABADO"};
        // El dia de la semana inicia en el 1 mientras que el array empieza en el 0
        diasemana = strDays[now.get(Calendar.DAY_OF_WEEK) - 1];

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("horario");
        mDatabase.orderByChild("dia").equalTo(diasemana).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        DiasHorario diasHorario = dataSnapshot.getValue(DiasHorario.class);
                        diasHorario.getDia();
                        if(diasHorario.getDia().equals(diasemana)){
                            asistenciaHoraInicio = diasHorario.getHoraInicio();
                            asistenciaHoraFin = diasHorario.getHoraFin();
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
                                tvdia.setText(diasHorario.getDia());
                                tvfecha.setText( (now.get(Calendar.MONTH) + 1)
                                        + "/"
                                        + now.get(Calendar.DATE)
                                        + "/"
                                        + now.get(Calendar.YEAR));
                                tvmateria.setText(diasHorario.getMateria());
                            }

                        }
                    }
                }
                if(!diacorrecto){
                    Toast.makeText(QrAlumnoActivity.this, "Fuera del horario para registrar asistencia", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(QrAlumnoActivity.this, InformacionCursoActivity.class);
                    intent.putExtra("uidcurso" , uidcurso);
                    startActivity(intent);
                }
                diacorrecto = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void btnback(){
        btbackqralumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QrAlumnoActivity.this, InformacionCursoActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                startActivity(intent);
                finish();
            }
        });
    }
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Lectora Cancelada", Toast.LENGTH_SHORT) .show();
            }else{
                Toast.makeText(this, "Asistencia tomada exitosamente", Toast.LENGTH_SHORT).show();
                escaneo = result.getContents();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(escaneo).child("Alumnos").child(mAuth.getCurrentUser().getUid());
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                        Date currentTime = Calendar.getInstance().getTime();
                            horaactual = currentTime.getHours()*100;
                            horaactual = horaactual + currentTime.getMinutes();

                            Calendar calendar = Calendar.getInstance();
                            long timeMilli2 = calendar.getTimeInMillis();

                                    Map<String, Object> map = new HashMap<String, Object>();

                                    if(horaactual <asistenciaHoraInicio+15){

                                        map.put("status", "Presente");
                                        map.put("horaLlegada", timeMilli2);
                                        btscan.setText("Presente");
                                    }
                                    else{
                                        map.put("status", "Atrasado");
                                        map.put("horaLlegada", timeMilli2);
                                        btscan.setText("Atrasado");
                                    }
                                    mDatabase.updateChildren(map);
                                    btscan.setClickable(false);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}














