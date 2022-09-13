package com.aplication.assistug.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplication.assistug.R;
import com.aplication.assistug.model.DiasHorario;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AdapterCMateria extends RecyclerView.Adapter<AdapterCMateria.MyViewHolderCM>{

    private Context context;
    private List<DiasHorario> diasHorarioList;

    public AdapterCMateria(Context context, List<DiasHorario> diasHorarioList) {
        this.context = context;
        this.diasHorarioList = diasHorarioList;
    }

    @NonNull
    @Override
    public AdapterCMateria.MyViewHolderCM onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.materias_item_rv, parent, false);
        return new AdapterCMateria.MyViewHolderCM(view);

    }


    @Override
    public void onBindViewHolder(@NonNull AdapterCMateria.MyViewHolderCM holder, int position) {

        DiasHorario diasHorario = diasHorarioList.get(position);
        //holder.MateriaRv.setText(diasHorario.getMateria().toString());

        int number1 = diasHorario.getHoraInicio();
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

        int number2 = diasHorario.getHoraFin();
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

        holder.DiaRv.setText(diasHorario.getDia().toString());

        holder.HorarioRv.setText( x +" - "+x2);
        holder.ivEliminarMIRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diasHorarioList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
        //holder.SemestreRv.setText(diasHorario.getSemestre().toString());

    }

    @Override
    public int getItemCount() {
        return diasHorarioList.size();
    }

    public static class MyViewHolderCM extends RecyclerView.ViewHolder {

        //TextView MateriaRv;
        TextView DiaRv;
        TextView HorarioRv;
        ImageView ivEliminarMIRv;
        //TextView SemestreRv;

        public MyViewHolderCM(@NonNull View itemView) {
            super(itemView);

            //MateriaRv = itemView.findViewById(R.id.MateriaRv);
            DiaRv = itemView.findViewById(R.id.DiaRv);
            HorarioRv = itemView.findViewById(R.id.HorarioRv);
            ivEliminarMIRv = itemView.findViewById(R.id.ivEliminarMIRv);
            //SemestreRv = itemView.findViewById(R.id.SemestreRv);

        }
    }

}
