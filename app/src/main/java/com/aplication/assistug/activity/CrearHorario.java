package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.aplication.assistug.adapter.AdapterCMateria;
import com.aplication.assistug.R;
import com.aplication.assistug.model.CursoModel;
import com.aplication.assistug.model.DiasHorario;
import com.aplication.assistug.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CrearHorario extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    ImageButton iBbackcrearhora;
    Spinner spDia;

    Button btnCrearH, spHora, spHora2 ;
    int hour, minute, hour2, minute2;
    int primer, fin;
    long fechaIni, fechaFin;
    Button btnCrearH2;
    TextView tvMateriaRv;
    RecyclerView rvListHorario;
    ArrayList<String> dias;
    ArrayList<String> horas;
    DiasHorario diasHorario;
    private AdapterCMateria adapterCMateria;
    private List<DiasHorario> diasHorarioList = new ArrayList<>();

    Boolean aux = false;
    int aux2sph1;
    int aux2sph2;

    String materia,semestre,descripcion, grupo, carrera;
    String maestro, uidmaestro;
    String fotocurso;
    String urlUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_horario);

        mAuth = FirebaseAuth.getInstance();

        iBbackcrearhora = findViewById(R.id.iBbackcrearhora);

        spDia = findViewById(R.id.spDia);
        spHora = findViewById(R.id.spHora);
        spHora2 = findViewById(R.id.spHora2);
        btnCrearH = findViewById(R.id.btnCrearH);
        btnCrearH2 = findViewById(R.id.btnCrearH2);
        tvMateriaRv = findViewById(R.id.tvMateriaRv);


        materia = getIntent().getStringExtra("materia");
        semestre = getIntent().getStringExtra("semestre");
        descripcion = getIntent().getStringExtra("descripcion");
        fotocurso = getIntent().getStringExtra("imagencurso");
        grupo = getIntent().getStringExtra("grupo");
        uidmaestro = getIntent().getStringExtra("uidmaestro");
        carrera = getIntent().getStringExtra("carrera");
        fechaIni = getIntent().getLongExtra("fechainicio",0);
        fechaFin = getIntent().getLongExtra("fechafin",0);
        tvMateriaRv.setText(materia);


        iBbackcrearhora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrearHorario.this, CrearCurso.class);
                startActivity(intent);
                finish();
            }
        });
        System.out.println(grupo);
        spinerHorario();
        btnAgregar();
        btnCrearHor ();
        //llenarRv();



    }

    private void btnAgregar (){

        btnCrearH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validacionVacio() == true) {
                    int horaini =primer;
                    int horafin =fin;


                    if (diasHorario == null) {
                        diasHorario = new DiasHorario(materia, semestre, spDia.getSelectedItem().toString(), aux2sph1, aux2sph2, "", grupo, carrera);
                        diasHorarioList.add(diasHorario);
                        //System.out.println(diasHorarioList.size()+diasHorarioList.toString());
                        llenarRv();
                        btnCrearH2.setVisibility(View.VISIBLE);
                    }else{
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

                                diasHorario = new DiasHorario(materia, semestre, spDia.getSelectedItem().toString(), horaini, horafin, "", grupo, carrera);
                                diasHorarioList.add(diasHorario);
                                adapterCMateria.notifyDataSetChanged();

                        }
                        aux = false;

                    }


                }

            }
        });


    }

    private boolean validacionVacio (){
        String subCadena2;
        String subCadena;
        System.out.println("iniciando val");
        if (!spHora.getText().equals("Selecciona") && !spHora2.getText().equals("Selecciona")){

            aux2sph1 = primer;

            aux2sph2 = fin;
        }


        System.out.println(aux2sph2+"-"+aux2sph1);

        if (spDia.getSelectedItem().toString().equals("Selecciona")){
            mensaje("Debes seleccionar un dia de la semana");
            return false;
        }if (spHora.getText().equals("Selecciona")){
            mensaje("Debes seleccionar la hora de inicio");
            return false;
        }if (spHora2.getText().toString().equals("Selecciona")){
            mensaje("Debes seleccionar la hora de fin");
            return false;
        }if (aux2sph1 >= aux2sph2){
            mensaje("La hora de inicio debe ser menor a la hora fin");
            return false;
        }else return true;


    }

    private void llenarRv(){

        //diasHorarioList.add(diasHorario);

        rvListHorario = findViewById(R.id.rvListHorario);
        adapterCMateria = new AdapterCMateria(getApplicationContext(),diasHorarioList);
        rvListHorario.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvListHorario.setAdapter(adapterCMateria);
        adapterCMateria.notifyDataSetChanged();

    }

    private void btnCrearHor (){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario").child(uidmaestro);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Usuario us = snapshot.getValue(Usuario.class);
                    maestro = us.getNombre()+" "+us.getApellido();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnCrearH2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos");
                String uid = mDatabase.push().getKey();//uid materia
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uid);

//                fotoCurso(fotocurso,uid);
                System.out.println(fotocurso);
                //CursoModel cursoModel = new CursoModel(uid,materia,semestre,mAuth.getCurrentUser().getUid(),maestro,fotoCurso(uid,fotocurso));

                if (TextUtils.isEmpty(fotocurso)){


                    fotocurso = "";
                    CursoModel cursoModel = new CursoModel(uid,materia,semestre,uidmaestro,maestro, fotocurso, grupo, carrera, fechaIni, fechaFin);
                    insertarDatosYfoto(cursoModel,uid);

                }else{

                    String path = "imagencurso/"+ uid +".png";
                    mStorageRef = FirebaseStorage.getInstance().getReference().child(path);
                    System.out.println(fotocurso);
                    mStorageRef.putFile(Uri.parse(fotocurso)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri uri) {

                                    urlUri = uri.toString();
                                    CursoModel cursoModel = new CursoModel(uid,materia,semestre,uidmaestro,maestro,urlUri, grupo, carrera, fechaIni, fechaFin);
                                    insertarDatosYfoto(cursoModel,uid);

                                }
                            }).addOnFailureListener(new OnFailureListener() {

                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }

//                Map<String,String> map = new HashMap();
//                map.put("nombrecurso",materia);
////                map.put("nombremaestro",materia);
//                map.put("semestre",semestre);
//                map.put("maestrouid",mAuth.getCurrentUser().getUid());
//                map.put("descripcion",descripcion);
//                map.put("fotocurso",fotocurso);



            }
        });

    }

    private void insertarDatosYfoto(CursoModel cursoModel,String uidm){

        mDatabase.setValue(cursoModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){


                    for (int i = 0 ; i<diasHorarioList.size() ; i++) {
                        //mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidm).child("horario");
                        String uidh = mDatabase.push().getKey();//uid horario
                        diasHorarioList.get(i).setUid(uidh);
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidm).child("horario").child(uidh);
                        mDatabase.setValue(diasHorarioList.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CrearHorario.this, "El curso fue registrado con éxito", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), RegistrarAlumnoCursoAdmin.class);
                                    intent.putExtra("uidcurso" , uidm);
                                    intent.putExtra("materia", materia);
                                    intent.putExtra("semestre",semestre);
                                    intent.putExtra("descripcion", descripcion);
                                    intent.putExtra("grupo", grupo);
                                    intent.putExtra("carrera", carrera);
                                    intent.putExtra("imagencurso", fotocurso);
                                    intent.putExtra("fechainicio", fechaIni);
                                    intent.putExtra("fechafin", fechaFin);
                                    intent.putExtra("uidmaestro", uidmaestro);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }

                }
            }
        });

    }

    private void spinerHorario(){

        dias = new ArrayList<>();
        dias.add("Selecciona");
        dias.add("LUNES");
        dias.add("MARTES");
        dias.add("MIERCOLES");
        dias.add("JUEVES");
        dias.add("VIERNES");
        dias.add("SÁBADO");


        ArrayAdapter<String> adapterD = new ArrayAdapter<>(this,R.layout.item_perzonalizado_list_spinner,dias);
        spDia.setAdapter(adapterD);



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