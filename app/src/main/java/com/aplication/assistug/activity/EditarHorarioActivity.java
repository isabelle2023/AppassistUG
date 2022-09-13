package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.model.DiasHorario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditarHorarioActivity extends AppCompatActivity {

    ArrayList<String> dias;
    ArrayList<String> horas;
    Button btnEditar;
    ImageView btnVolver;
    Spinner spDia;
    Button spHora, spHora2 ;
    int hour, minute, hour2, minute2;
    int primer, fin;
    String dia, uid, uidcurso, nombrecurso, semestre;
    int horaInicio, horaFin;
    private List<DiasHorario> diasHorarioList = new ArrayList<>();
    private DatabaseReference mDatabase;
    Boolean aux = false;
    int aux2sph1;
    int aux2sph2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_horario);
        spDia = findViewById(R.id.spDia);
        spHora = findViewById(R.id.spHora);
        spHora2 = findViewById(R.id.spHora2);
        btnEditar = findViewById(R.id.btneditar);
        semestre = getIntent().getStringExtra("semestre");
        uid = getIntent().getStringExtra("uid");
        uidcurso = getIntent().getStringExtra("uidcurso");
        nombrecurso = getIntent().getStringExtra("nombrecurso");
        dia = getIntent().getStringExtra("dia");
        horaInicio = getIntent().getIntExtra("horaInicio", 0);
        horaFin = getIntent().getIntExtra("horaFin", 0);
        btnVolver = findViewById(R.id.btnvolver);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HorariosIndividualActivity.class);
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre" , semestre);
                startActivity(intent);
                finish();
            }
        });



        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validacionVacio()) {
                    int horaini;
                    int horafin;

                    if(primer ==0){
                        horaini = horaInicio;
                    }else{
                        horaini =primer;
                    }

                    if(fin ==0){
                        horafin = horaFin;
                    }else{
                        horafin =fin;
                    }

                    for (int i = 0; i<diasHorarioList.size(); i++) {
                        //if (!diasHorario.getDia().equals(spDia.getSelectedItem().toString())) {
                        System.out.println(diasHorarioList.get(i).getDia()+"---"+spDia.getSelectedItem().toString());
                        if (!diasHorarioList.get(i).getDia().equals(spDia.getSelectedItem().toString())) {


                        }else if(diasHorarioList.get(i).getDia().equals(spDia.getSelectedItem().toString()) || diasHorarioList.get(i).getHoraInicio() == horaini && diasHorarioList.get(i).getHoraFin() == horafin){

                            aux = true;
                            System.out.println(aux);

                        }
                    }

                    if (aux == false){

                        Map<String, Object> map = new HashMap<>();
                        map.put("dia", spDia.getSelectedItem().toString());
                        map.put("horaInicio",horaini);
                        map.put("horaFin", horafin);
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("horario").child(uid);
                        mDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    mensaje("Horario actualizado");
                                    Intent intent = new Intent(getApplicationContext(), HorariosIndividualActivity.class);
                                    intent.putExtra("uidcurso" , uidcurso);
                                    intent.putExtra("nombrecurso" , nombrecurso);
                                    intent.putExtra("semestre" , semestre);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });



                    } else {
                        mensaje("El horario ingresado coincide con otro horario");
                    }

                    aux =false;



                }

            }
        });


        spinerHorario();
        spDia.setSelection(dias.indexOf(dia));
        int number1 = horaInicio;
        String number = String.valueOf(number1);
        String[] digits = number.split("(?<=.)");
        String x;

        if(digits.length >1){
            if(digits.length<4){
                x = 0 + "" +digits[0] + ":" + digits[1] + "" +digits[2];
            }else{
                x = digits[0] + "" +digits[1] + ":" + digits[2] + "" +digits[3];
            }


        }else{
            x = "00:00";
        }
        spHora.setText(x);
        int number2 = horaFin;
        String numberx = String.valueOf(number2);
        String[] digits2 = numberx.split("(?<=.)");
        String x2;
        if(digits2.length >1 ){
            if(digits2.length<4){
                x2 = 0 + "" +digits2[0] + ":" + digits2[1] + "" +digits2[2];
            }else{
                x2 = digits2[0] + "" +digits2[1] + ":" + digits2[2] + "" +digits2[3];
            }

        }else{
            x2 = "00:00";
        }
        spHora2.setText(x2);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("horario");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        DiasHorario diasHorario = dataSnapshot.getValue(DiasHorario.class);
                        if(!uid.equals(diasHorario.getUid())) {
                            diasHorarioList.add(diasHorario);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void spinerHorario() {
        horas = new ArrayList<>();
        horas.add("Selecciona");
        horas.add("7");
        horas.add("8");
        horas.add("9");
        horas.add("10");
        horas.add("11");
        horas.add("12");
        horas.add("13");
        horas.add("14");
        horas.add("15");
        horas.add("16");
        horas.add("17");
        horas.add("18");
        horas.add("19");
        horas.add("20");

        dias = new ArrayList<>();
        dias.add("Selecciona");
        dias.add("LUNES");
        dias.add("MARTES");
        dias.add("MIERCOLES");
        dias.add("JUEVES");
        dias.add("VIERNES");
        dias.add("SABADO");


        ArrayAdapter<String> adapterD = new ArrayAdapter<>(this,R.layout.item_perzonalizado_list_spinner,dias);
        spDia.setAdapter(adapterD);



    }

    private boolean validacionVacio (){
        String subCadena2;
        String subCadena;
        System.out.println("iniciando val");
      if (!spHora.getText().equals("Selecciona") && !spHora2.getText().equals("Selecciona")){
            System.out.println("en el else");



          if(primer ==0){
              aux2sph1 = horaInicio;
          }else{
              aux2sph1 =primer;
          }

          if(fin ==0){
              aux2sph2 = horaFin;
          }else{
              aux2sph2 =fin;
          }
        }


        System.out.println(aux2sph2+"-"+aux2sph1);

        if (spDia.getSelectedItem().toString().equals("Selecciona")){
            mensaje("Debes seleccionar un dia de la semana");
            return false;
        }if (spHora.getText().equals("Selecciona")){
            mensaje("Debes seleccionar la hora de inicio");
            return false;
        }if (spHora2.getText().equals("Selecciona")){
            mensaje("Debes seleccionar la hora de fin");
            return false;
        }if (aux2sph1 >= aux2sph2){
            mensaje("La Hora de inicio debe ser menor a la hora de fin");
            return false;
        }else return true;

    }

    private void mensaje (String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    public void popTime2(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker timePicker, int selectHora, int selectMinuto) {
                hour2 = selectHora;
                minute2 = selectMinuto;
                fin = hour2*100 + minute2;
                System.out.println(hour2*100 + minute2);
                spHora2.setText(String.format(Locale.getDefault(), "%02d:%02d", hour2,minute2));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour2, minute2, true);
        timePickerDialog.setTitle("Selecciona la hora de inicio:");
        timePickerDialog.show();

    }

    public void popTime1(View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker timePicker, int selectHora, int selectMinuto) {
                hour = selectHora;
                minute = selectMinuto;
                primer = hour*100 + minute;
                System.out.println(hour*100 + minute);
                spHora.setText(String.format(Locale.getDefault(), "%02d:%02d", hour,minute));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Selecciona la hora de inicio:");
        timePickerDialog.show();
    }
}