package com.aplication.assistug.helper;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.model.Usuario;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private static String  nombrealumno;
    private FirebaseAuth mAuth;
    private static DatabaseReference mDatabase;

    public static boolean insert(String values, Context context, String uidcurso, String nombrecurso) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario");
        mDatabase.orderByChild("cedula").equalTo(values).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //user = snapshot.getValue(Usuario.class);
                    System.out.println(snapshot);
                    for (DataSnapshot cedula : snapshot.getChildren()) {
                        try {
                            System.out.println(cedula);
                            Usuario user = cedula.getValue(Usuario.class);
                            String uidalumno = user.getUid();
                            nombrealumno = user.getNombre();

                            if (user != null) {
                                System.out.println("alumno encontrado");
                                registraralumno(uidcurso, nombrecurso,uidalumno, context);

                            }


                        } catch (Exception e) {
                            System.out.println(e + "eeeeeeeeeeeeeeeeeeeeeeeeee");
                            return;
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return false;
    }

    private static void registraralumno(String uidcurso, String nombrecurso, String uidalumno, Context context) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos_Alumnos").child(uidcurso).child(uidalumno);

        Map mapAlm = new HashMap();
        mapAlm.put("uidAlumno", uidalumno);
        mapAlm.put("nombre", nombrealumno);
        mapAlm.put("uidCurso", uidcurso);


        mDatabase.setValue(mapAlm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Alumnos_Curso").child(uidalumno).child(uidcurso);

                    Map mapAlmC = new HashMap();
                    System.out.println("aquiiiiiiiiiiiiiiiiiiiiii" + nombrecurso);
                    mapAlmC.put("nombreCurso", nombrecurso);
                    mapAlmC.put("uidAlumno", uidalumno);
                    mapAlmC.put("uidCurso", uidcurso);

                    mDatabase.setValue(mapAlmC).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            mostrarMensaje("Se Registro Correctamente", context);
                        }
                    });

                } else {
                    System.out.println(task.getException().toString());
                    mostrarMensaje("Error: " + task.getException().toString(), context);
                }
            }
        });
    }

        private static void mostrarMensaje(String mensaje, Context c) {
            Toast.makeText(c, mensaje, Toast.LENGTH_SHORT).show();
        }
}
