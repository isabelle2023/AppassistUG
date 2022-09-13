package com.aplication.assistug.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplication.assistug.R;
import com.aplication.assistug.model.AlumnosHorario;

import java.util.List;

public class AdapterAlumHorario extends RecyclerView.Adapter<AdapterAlumHorario.MyViewHolderAH>{

    private Context context;
    private List<AlumnosHorario> alumnosHorarioList;

    public AdapterAlumHorario (Context context, List<AlumnosHorario> alumnosHorarioList){
        this.context = context;
        this.alumnosHorarioList = alumnosHorarioList;
    }

    @NonNull
    @Override
    public AdapterAlumHorario.MyViewHolderAH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_horario_det, parent, false);
        return new AdapterAlumHorario.MyViewHolderAH(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAlumHorario.MyViewHolderAH holder, int position) {

        AlumnosHorario alumnosHorario = alumnosHorarioList.get(position);
        holder.tvNomMatAHD.setText(alumnosHorario.getMateria()+"");
        int number1 = alumnosHorario.getHoraInicio();
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


        int number2 = alumnosHorario.getHoraFin();
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


        holder.tvMostrarHoraAHD.setText(x+"HR - "+ x2+"HR");
        holder.tvMostrarSemestreAHD.setText(alumnosHorario.getSemestre());
        holder.tvMostrarGrupoAHD.setText(alumnosHorario.getGrupo());



    }

    @Override
    public int getItemCount() {
        return alumnosHorarioList.size();
    }

    public static class MyViewHolderAH extends RecyclerView.ViewHolder {

        TextView tvNomMatAHD;
        TextView tvMostrarHoraAHD;
        TextView tvMostrarSemestreAHD,  tvMostrarGrupoAHD;

        public MyViewHolderAH(@NonNull View itemView) {
            super(itemView);

            tvNomMatAHD = itemView.findViewById(R.id.tvNomMatAHD);
            tvMostrarHoraAHD = itemView.findViewById(R.id.tvMostrarHoraAHD);
            tvMostrarSemestreAHD = itemView.findViewById(R.id.tvMostrarSemestreAHD);
            tvMostrarGrupoAHD = itemView.findViewById(R.id.tvMostrarGrupoAHD);
        }
    }

}
