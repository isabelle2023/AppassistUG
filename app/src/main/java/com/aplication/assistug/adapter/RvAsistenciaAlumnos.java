package com.aplication.assistug.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aplication.assistug.activity.AsistenciaAlumnosMaestroActivity;
import com.aplication.assistug.activity.PerfilActivity;
import com.aplication.assistug.model.Carrera;
import com.aplication.assistug.model.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.aplication.assistug.R;
import com.aplication.assistug.model.AlumnoAsistencia;
import com.aplication.assistug.model.Usuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RvAsistenciaAlumnos extends RecyclerView.Adapter<RvAsistenciaAlumnos.vh> {

    /*
            Atributos del adaptador.
            Como mínimo necesitamos una variable que tenga el contexto de la app y otra que tenga la
            lista de objetos a mostrar.
         */
    private DatabaseReference mDatabase, mDatabaseHorario;
    Context c;
    Spinner spinner;
    String uidAlumno,uidcurso, uidhorario;
    List<AlumnoAsistencia> listasistenciaalumnos;
    ArrayList<String> status;
    View v;


    // Constructor. Lo único que hacemos es inicializar el contexto y la lista de objetos
    public RvAsistenciaAlumnos(Context c, List<AlumnoAsistencia> listasistenciaalumnos, String uidhorario, String uidcurso) {
        this.c = c;
        this.listasistenciaalumnos = listasistenciaalumnos;
        this.uidhorario = uidhorario;
        this.uidcurso = uidcurso;
    }


    /*
        Establecemos qué fichero xml tiene la intefaz gráfica de cada uno de los elementos
        de nuestro RecyclerView
    */
    @NonNull
    @Override
    public vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(c);
        View v = li.inflate(R.layout.rv_asistencia_alumno, parent, false);
        return new vh(v);
    }

    /*
        Para cada uno de los objetos establece sus datos, en nuestro caso establece el nombre,
        contintente, altura e imagen. También establecemos los onclick necesarios. En nuestro caso
        el onclick para borrar elemento y el onclick para visitar la página de la Wikipedia de la
        montaña
     */
    @Override
    public void onBindViewHolder(@NonNull vh holder, final int position) {

        AlumnoAsistencia asistencia = listasistenciaalumnos.get(position);

                holder.nombre.setText(asistencia.getNombre());
                holder.cedula.setText(asistencia.getCedula());
                holder.status.setText(asistencia.getStatus());


        holder.editarasistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uidAlumno = asistencia.getUidAlumno();
                ShowEditar(view, holder.getAdapterPosition());
            }
        });
    }

    // Devolver el número de elementos que componen nuestra RecyclerView
    @Override
    public int getItemCount() {
        return listasistenciaalumnos.size();
    }

    /*
        Clase encargada de enlazar los elementos de la interfaz gráfica con las variables del
        adaptador
    */
    public class vh extends RecyclerView.ViewHolder {
        TextView status, nombre, cedula, fecha;
        ConstraintLayout cardviewasistenciaalumnos, layoutDialog;
        ImageButton editarasistencia;


        public vh(@NonNull View itemView) {
            super(itemView);
            editarasistencia = itemView.findViewById(R.id.iveditarasistencia);
            nombre = itemView.findViewById(R.id.tvasistenciaalumnonombre);
            fecha = itemView.findViewById(R.id.tvfecha);
            cedula = itemView.findViewById(R.id.tvasistenciaAlumnocedula);
            status = itemView.findViewById(R.id.tvasistenciaAlumnostatus);
            cardviewasistenciaalumnos = itemView.findViewById(R.id.cardviewasistenciaalumnos);
            layoutDialog= itemView.findViewById(R.id.layoutDialog);
        }
    }
    private void ShowEditar(View v, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(c).inflate(R.layout.layout_spinner,(ConstraintLayout)v.findViewById(R.id.layoutDialogContainer));
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitleDialogError)).setText("Cambiar Estatus");
        ((TextView) view.findViewById(R.id.textMessageDialogError)).setText("Selecciona el status del alumno:");
        ((TextView) view.findViewById(R.id.buttonYes)).setText("Cambiar");
        ((TextView) view.findViewById(R.id.buttonNo)).setText("Cancelar");
        spinner = view.findViewById(R.id.spinnerDialog);


        status = new ArrayList<>();
        status.add("Falta");
        status.add("Presente");
        status.add("Atrasado");
        status.add("Justificado");
        ArrayAdapter<String> adapterD = new ArrayAdapter<>(c,R.layout.item_perzonalizado_list_spinner,status);
        spinner.setAdapter(adapterD);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Map map = new HashMap();
                map.put("status", spinner.getSelectedItem().toString());

                mDatabaseHorario = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso).child("asistencia").child(uidhorario).child("Alumnos").child(uidAlumno);

                mDatabaseHorario.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){

                     //       Toast.makeText(c,"Status cambiado",Toast.LENGTH_SHORT).show();
                            listasistenciaalumnos.get(position).setStatus( spinner.getSelectedItem().toString());
                            notifyItemChanged(position);
                        }else{

                        }

                    }
                });
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow()!= null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public void orderAlfabeticamente() {

        System.out.println("Tamo dentro");



    }
}