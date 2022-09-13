package com.aplication.assistug.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.activity.InformacionCursoActivity;
import com.aplication.assistug.model.CursoModel;
import com.aplication.assistug.model.Usuario;
import com.squareup.picasso.Picasso;

public class RvCursos extends RecyclerView.Adapter<RvCursos.vh> {
    /*
        Atributos del adaptador.
        Como mínimo necesitamos una variable que tenga el contexto de la app y otra que tenga la
        lista de objetos a mostrar.
     */
    Context c;
    List<CursoModel> listcursos;
    private DatabaseReference mDatabase;

    // Constructor. Lo único que hacemos es inicializar el contexto y la lista de objetos
    public RvCursos(Context c, List<CursoModel> listcursos) {
        this.c = c;
        this.listcursos = listcursos;
    }

    /*
        Establecemos qué fichero xml tiene la intefaz gráfica de cada uno de los elementos
        de nuestro RecyclerView
    */
    @NonNull
    @Override
    public vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(c);
        View v = li.inflate(R.layout.rv_curso_row, parent, false);
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
        CursoModel curso = listcursos.get(position);
        holder.curso.setText(curso.getNombreCurso());

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario").child(curso.getMaestrouid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                holder.maestro.setText(usuario.getNombre()+' '+usuario.getApellido());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.semestre.setText("Semestre: " + curso.getSemestre());
        holder.grupo.setText("Grupo: " + curso.getGrupo());

        if (!curso.getFotocurso().isEmpty()) {
            Picasso.get().load(curso.getFotocurso()).into(holder.foto);
        }


        // onclick para ir a la página de la Wikipedia
       holder.btvermas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c.getApplicationContext(), InformacionCursoActivity.class);
                intent.putExtra("uidcurso" , curso.getUid());
                intent.putExtra("nombrecurso" , curso.getNombreCurso());
                intent.putExtra("semestre", curso.getSemestre());
                c.startActivity(intent);
            }
        });

        // onclick para borrar el monte
      /*  holder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                montes.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, montes.size());
            }
        });*/
    }

    // Devolver el número de elementos que componen nuestra RecyclerView
    @Override
    public int getItemCount() {
        return listcursos.size();
    }

    /*
        Clase encargada de enlazar los elementos de la interfaz gráfica con las variables del
        adaptador
    */
    public class vh extends RecyclerView.ViewHolder {
        TextView curso, maestro,semestre, grupo;
        ImageView foto;
        Button btvermas;

        public vh(@NonNull View itemView) {
            super(itemView);
            grupo = itemView.findViewById(R.id.row_grupo);
            curso = itemView.findViewById(R.id.row_nombre);
            maestro =  itemView.findViewById(R.id.row_maestro);
            foto =  itemView.findViewById(R.id.row_img);
            btvermas = itemView.findViewById(R.id.row_button);
            semestre = itemView.findViewById(R.id.row_semestre);
        }
    }
}