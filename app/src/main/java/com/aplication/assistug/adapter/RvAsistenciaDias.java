package com.aplication.assistug.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aplication.assistug.R;
import com.aplication.assistug.activity.AsistenciaAlumnosActivity;
import com.aplication.assistug.activity.AsistenciaAlumnosMaestroActivity;
import com.aplication.assistug.model.AsistenciaDias;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RvAsistenciaDias extends RecyclerView.Adapter<RvAsistenciaDias.vh> {

    /*
            Atributos del adaptador.
            Como mínimo necesitamos una variable que tenga el contexto de la app y otra que tenga la
            lista de objetos a mostrar.
         */
    Context c;
    String uidcurso, nombrecurso, tipouser;
    List<AsistenciaDias> listasistencia;

    // Constructor. Lo único que hacemos es inicializar el contexto y la lista de objetos
    public RvAsistenciaDias(Context c, List<AsistenciaDias> listasistencia, String uidcurso, String nombrecurso, String tipouser) {
        this.c = c;
        this.listasistencia = listasistencia;
        this.uidcurso = uidcurso;
        this.nombrecurso = nombrecurso;
        this.tipouser = tipouser;
    }
    /*
        Establecemos qué fichero xml tiene la intefaz gráfica de cada uno de los elementos
        de nuestro RecyclerView
    */
    @NonNull
    @Override
    public vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(c);
        View v = li.inflate(R.layout.rv_asistencia_dias, parent, false);
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



        AsistenciaDias asistencia = listasistencia.get(position);
        Date date = new Date(asistencia.getFecha());
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(date);

        holder.fecha.setText(timeStamp);

        holder.cardviewasistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(tipouser.equals("maestro") || tipouser.equals("")){
                    intent = new Intent(c.getApplicationContext(), AsistenciaAlumnosMaestroActivity.class);
                }else{

                    intent = new Intent(c.getApplicationContext(), AsistenciaAlumnosActivity.class);
                }
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso", nombrecurso);
                intent.putExtra("uidhorario" , asistencia.getUid());
                intent.putExtra("fecha" , timeStamp);
                intent.putExtra("dia" , asistencia.getDia());
                intent.putExtra("horaini", asistencia.getHoraInicio());
                intent.putExtra("horafin", asistencia.getHoraFin());
                intent.putExtra("tipouser", tipouser);
                c.startActivity(intent);
            }
        });
    }

    // Devolver el número de elementos que componen nuestra RecyclerView
    @Override
    public int getItemCount() {
        return listasistencia.size();
    }

    /*
        Clase encargada de enlazar los elementos de la interfaz gráfica con las variables del
        adaptador
    */
    public class vh extends RecyclerView.ViewHolder {
        TextView fecha, dia, hora;
        ConstraintLayout cardviewasistencia;

        public vh(@NonNull View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.tvasistenciafecha);

            cardviewasistencia = itemView.findViewById(R.id.cardviewasistencia);
        }
    }
}