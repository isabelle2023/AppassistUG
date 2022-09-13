package com.aplication.assistug.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplication.assistug.model.AlumnoAsistencia;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.model.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterListAlumn extends RecyclerView.Adapter<AdapterListAlumn.MyViewHolderLA>{

    private DatabaseReference mData;
    private DatabaseReference mData2;

    private Context context;
    private List<Usuario> usuarioList;
    private String uidcurso;

    public AdapterListAlumn(Context context, List<Usuario> usuarioList, String uidcurso){

        this.context = context;
        this.usuarioList = usuarioList;
        this.uidcurso = uidcurso;

    }

    @NonNull
    @Override
    public AdapterListAlumn.MyViewHolderLA onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_lista_alumno, parent, false);
        return new AdapterListAlumn.MyViewHolderLA(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListAlumn.MyViewHolderLA holder, int position) {

        Usuario usuario = usuarioList.get(position);
        holder.tvNomMatAHD.setText(usuario.getApellido() + " " + usuario.getNombre());
        holder.tvCedulaLA.setText(usuario.getCedula());



        holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                usuarioList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                holder.btnBorrar.setEnabled(!holder.btnBorrar.isEnabled());

                mData = FirebaseDatabase.getInstance().getReference().child("Cursos_Alumnos").child(uidcurso)
                        .child(usuario.getUid());

                mData.removeValue();

                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) {

                            mData2 = FirebaseDatabase.getInstance().getReference().child("Alumnos_Curso")
                                    .child(usuario.getUid()).child(uidcurso);
                            mData2.removeValue();
                            Toast.makeText(context, "Se ha eliminado correctamente a " + usuario.getNombre(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public static class MyViewHolderLA extends RecyclerView.ViewHolder{

        TextView tvNomMatAHD;
        TextView tvCedulaLA;
        ImageButton btnBorrar;

        public MyViewHolderLA(@NonNull View itemView) {
            super(itemView);

            tvNomMatAHD = itemView.findViewById(R.id.tvNomMatAHD);
            tvCedulaLA = itemView.findViewById(R.id.tvCedulaLA);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);

        }
    }

}
