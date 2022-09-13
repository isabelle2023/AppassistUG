package com.aplication.assistug.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.aplication.assistug.R;
import com.aplication.assistug.activity.EditarHorarioActivity;
import com.aplication.assistug.model.DiasHorario;

import java.util.List;

public class AdapterHorarioIndividual extends RecyclerView.Adapter<AdapterHorarioIndividual.MyViewHolderAH>{

    private Context context;
    private List<DiasHorario> diasHorarioList;
    private String tipo, uidcurso, nombrecurso, semestre;
    private DatabaseReference mDatabase;

    public AdapterHorarioIndividual(Context context, List<DiasHorario> diasHorarioList, String tipo, String uidcurso, String nombrecurso, String semestre){
        this.context = context;
        this.diasHorarioList = diasHorarioList;
        this.tipo = tipo;
        this.uidcurso = uidcurso;
        this.nombrecurso = nombrecurso;
        this.semestre = semestre;
    }

    @NonNull
    @Override
    public AdapterHorarioIndividual.MyViewHolderAH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_horario_individual, parent, false);
        return new AdapterHorarioIndividual.MyViewHolderAH(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHorarioIndividual.MyViewHolderAH holder, int position) {

        DiasHorario diasHorario = diasHorarioList.get(position);
        holder.tvDia.setText(diasHorario.getDia().toString());

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


        holder.tvHora.setText(x+"HR - "+ x2+"HR");
        if(tipo.equals("alumno")) {
            holder.ivEditar.setVisibility(View.GONE);
            holder.ivBorrar.setVisibility(View.GONE);
        }

        holder.ivEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), EditarHorarioActivity.class);
                intent.putExtra("dia" , diasHorario.getDia());
                intent.putExtra("horaInicio" , diasHorario.getHoraInicio());
                intent.putExtra("horaFin" , diasHorario.getHoraFin());
                intent.putExtra("uid" , diasHorario.getUid());
                intent.putExtra("uidcurso" , uidcurso);
                intent.putExtra("nombrecurso" , nombrecurso);
                intent.putExtra("semestre", semestre);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

        holder.ivBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(diasHorarioList.size() <= 1)) {

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos").child(uidcurso)
                            .child("horario").child(diasHorario.getUid());

                    mDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                MostrarMensaje("Horario eliminado.");
                                diasHorarioList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        }
                    });
                } else {
                    MostrarMensaje("Solo tienes un horario.");
                }

            }
        });



    }

    private void MostrarMensaje(String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return diasHorarioList.size();
    }

    public static class MyViewHolderAH extends RecyclerView.ViewHolder {

        TextView tvDia, tvHora;
        ImageButton ivBorrar,ivEditar;

        public MyViewHolderAH(@NonNull View itemView) {
            super(itemView);

            tvDia = itemView.findViewById(R.id.tvdia);
            tvHora = itemView.findViewById(R.id.tvhora);
            ivEditar = itemView.findViewById(R.id.iveditar);
            ivBorrar = itemView.findViewById(R.id.ivBorrar);

        }
    }

}
