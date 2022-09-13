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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RvAsistenciaAlumnosAlumno extends RecyclerView.Adapter<RvAsistenciaAlumnosAlumno.vh> {

    /*
            Atributos del adaptador.
            Como mínimo necesitamos una variable que tenga el contexto de la app y otra que tenga la
            lista de objetos a mostrar.
         */
    private DatabaseReference mDatabase, mDatabaseHorario;
    Context c;
    String uidcurso, uidhorario;
    List<AlumnoAsistencia> listasistenciaalumnos;


    // Constructor. Lo único que hacemos es inicializar el contexto y la lista de objetos
    public RvAsistenciaAlumnosAlumno(Context c, List<AlumnoAsistencia> listasistenciaalumnos, String uidhorario, String uidcurso) {
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
        View v = li.inflate(R.layout.rv_asistencia_alumno_alumno, parent, false);
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

        Date date = new Date(asistencia.getFecha());
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(date);
        holder.fecha.setText(timeStamp);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario").child(asistencia.getUidAlumno());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario alumno = snapshot.getValue(Usuario.class);
                holder.nombre.setText(alumno.getNombre() + " " + alumno.getApellido());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       holder.status.setText(asistencia.getStatus());



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
        TextView status, nombre, fecha;
        ConstraintLayout cardviewasistenciaalumnos;


        public vh(@NonNull View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.tvasistenciaAlumnosfecha);
            nombre = itemView.findViewById(R.id.tvasistenciaalumnonombrea);
            status = itemView.findViewById(R.id.tvasistenciastatusa);
            cardviewasistenciaalumnos = itemView.findViewById(R.id.cardviewasistenciaalumnos_alumnos);

        }
    }

}