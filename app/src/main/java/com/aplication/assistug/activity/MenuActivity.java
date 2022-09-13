package com.aplication.assistug.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aplication.assistug.R;
import com.aplication.assistug.adapter.RvCursos;
import com.aplication.assistug.model.AlumnoCursos;
import com.aplication.assistug.model.CursoModel;
import com.aplication.assistug.model.Usuario;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    LinearLayout LLnocursos, LLAdmin;
    ConstraintLayout Cccursos;
    NavigationView navigationView;
    Toolbar toolbar;

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Menu menu;
    ImageView circlevImgPerfil;
    TextView tvNombrePerfil; //tv de mi navbar
    TextView tvCorreoPerfil;//tv de mi nabvar
    TextView tvTipo;


    RecyclerView rv;
    RvCursos rva;
    RecyclerView.LayoutManager lm;
    List<CursoModel> cursos = new ArrayList<>();
    List<AlumnoCursos> alumnocursoslist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


                mAuth = FirebaseAuth.getInstance();


        setUpToolbar();
        MenuP();
        ocultarOpcionesMenu (this);
        navigationView.bringToFront();//traer al frente toolbar

    }

    private void ocultarOpcionesMenu(Context context) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuario")
                .child(mAuth.getCurrentUser().getUid());

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Usuario sesiones = snapshot.getValue(Usuario.class);
                    navigationView = findViewById(R.id.navegationView);
                    Cccursos = findViewById(R.id.Cccursos);
                    LLnocursos = findViewById(R.id.LLnocursos);
                    if (sesiones.getTipo().equals("maestro")){
                        //asi mandamos info a nuestro nav_cabezera
                        View header = navigationView.getHeaderView(0);
                        circlevImgPerfil = header.findViewById(R.id.circlevImgPerfil);
                        tvNombrePerfil = header.findViewById(R.id.tvNombrePerfil);
                        tvCorreoPerfil = header.findViewById(R.id.tvCorreoPerfil);
                        if (!sesiones.getUrlfoto().isEmpty()) {
                            Picasso.get().load(sesiones.getUrlfoto()).into(circlevImgPerfil);
                        }
                        //circlevImgPerfil.setImageURI();//aqui mandamos la info a nuestro nav_cabezera
                        tvNombrePerfil.setText(sesiones.getNombre());//aqui mandamos la info a nuestro nav_cabezera
                        tvCorreoPerfil.setText(sesiones.getCorreo());//aqui mandamos la info a nuestro nav_cabezera

                        menu = navigationView.getMenu();
                        MenuItem registrarcurso = menu.findItem(R.id.itRegCurso);
                        MenuItem RegMaestro = menu.findItem(R.id.itRegMaestro);
                        MenuItem RegAlumno = menu.findItem(R.id.itRegAlumno);
                        RegMaestro.setVisible(false);
                        registrarcurso.setVisible(false);
                        RegAlumno.setVisible(false);


                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos");
                        mDatabase.orderByChild("maestrouid").equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                cursos.clear();
                                if(snapshot.exists()){
                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                        CursoModel curso = dataSnapshot.getValue(CursoModel.class);
                                        cursos.add(curso);
                                        Cccursos.setVisibility(View.VISIBLE);
                                        LLnocursos.setVisibility(View.GONE);
                                    }
                                }else{
                                    Cccursos.setVisibility(View.GONE);
                                    LLnocursos.setVisibility(View.VISIBLE);
                                }
                                rva.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println(error+"------------AAAAAA");

                            }
                        });
                        // Enlazamos la interfaz gráfica del RecyclerView con el código
                        rv = findViewById(R.id.rv_cursos);
                        // Establecemos que los elementos del RecyclerView se apilen verticalmente
                        lm = new LinearLayoutManager(context);
                        // Creamos un adaptador para el RecyclerView
                        rva = new RvCursos(context, cursos);
                        // Enlazamos el adaptador con el objeto RecyclerView
                        rv.setAdapter(rva);
                        rv.setLayoutManager(lm);

                    }else if(sesiones.getTipo().equals("alumno")){
                        System.out.println("ERES ALUMNO SE OCULTAN CIERTAS OPCIONES");
                        tvTipo = findViewById(R.id.tvTipo);
                        tvTipo.setText("Mis clases");
                        //asi mandamos info a nuestro nav_cabezera
                        View header = navigationView.getHeaderView(0);
                        circlevImgPerfil = header.findViewById(R.id.circlevImgPerfil);
                        tvNombrePerfil = header.findViewById(R.id.tvNombrePerfil);
                        tvCorreoPerfil = header.findViewById(R.id.tvCorreoPerfil);
                        if (!sesiones.getUrlfoto().isEmpty()) {
                            Picasso.get().load(sesiones.getUrlfoto()).into(circlevImgPerfil);
                        }
                        tvNombrePerfil.setText(sesiones.getNombre());//aqui mandamos la info a nuestro nav_cabezera
                        tvCorreoPerfil.setText(sesiones.getCorreo());//aqui mandamos la info a nuestro nav_cabezera

                        menu = navigationView.getMenu();
                        MenuItem registrarcurso = menu.findItem(R.id.itRegCurso);
                        MenuItem RegMaestro = menu.findItem(R.id.itRegMaestro);
                        MenuItem RegAlumno = menu.findItem(R.id.itRegAlumno);
                        registrarcurso.setVisible(false);
                        RegMaestro.setVisible(false);
                        RegAlumno.setVisible(false);

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Alumnos_Curso").child(mAuth.getCurrentUser().getUid());
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                        AlumnoCursos alumnoCursos = dataSnapshot.getValue(AlumnoCursos.class);
                                        alumnocursoslist.add(alumnoCursos);
                                    }

                                    for(int i= 0; i < alumnocursoslist.size(); i++) {

                                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos")
                                                .child(alumnocursoslist.get(i).getUidCurso());

                                        int finalI = i;
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    Cccursos.setVisibility(View.VISIBLE);
                                                    CursoModel curso = snapshot.getValue(CursoModel.class);
                                                    cursos.add(curso);

                                                }else{
                                                    Cccursos.setVisibility(View.GONE);
                                                    LLnocursos.setVisibility(View.VISIBLE);
                                                }
                                                if(finalI == alumnocursoslist.size()-1){
                                                    // Enlazamos la interfaz gráfica del RecyclerView con el código
                                                    rv = findViewById(R.id.rv_cursos);
                                                    // Establecemos que los elementos del RecyclerView se apilen verticalmente
                                                    lm = new LinearLayoutManager(context);
                                                    // Creamos un adaptador para el RecyclerView
                                                    rva = new RvCursos(context, cursos);
                                                    // Enlazamos el adaptador con el objeto RecyclerView
                                                    rv.setAdapter(rva);
                                                    rv.setLayoutManager(lm);
                                                    rva.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }else{
                                    Cccursos.setVisibility(View.GONE);
                                    LLnocursos.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }else if (sesiones.getPrivilegio().equals("admin")){

                        System.out.println("ERES Admin SE OCULTAN CIERTAS OPCIONES");
                        LLAdmin = findViewById(R.id.LLAdmin);
                        LLAdmin.setVisibility(View.GONE);
                        tvTipo = findViewById(R.id.tvTipo);
                        tvTipo.setText("Administración");
                        //asi mandamos info a nuestro nav_cabezera
                        View header = navigationView.getHeaderView(0);

                        circlevImgPerfil = header.findViewById(R.id.circlevImgPerfil);
                        tvNombrePerfil = header.findViewById(R.id.tvNombrePerfil);
                        tvCorreoPerfil = header.findViewById(R.id.tvCorreoPerfil);
                        if (!sesiones.getUrlfoto().isEmpty()) {
                            Picasso.get().load(sesiones.getUrlfoto()).into(circlevImgPerfil);
                        }
                        tvNombrePerfil.setText(sesiones.getNombre());//aqui mandamos la info a nuestro nav_cabezera
                        tvCorreoPerfil.setText(sesiones.getCorreo());//aqui mandamos la info a nuestro nav_cabezera

                        menu = navigationView.getMenu();
                        MenuItem registrarcurso = menu.findItem(R.id.itRegCurso);
                        MenuItem reghorario = menu.findItem(R.id.itHorario);

                        registrarcurso.setVisible(true);
                        reghorario.setVisible(false);

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cursos");
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                cursos.clear();
                                if(snapshot.exists()){
                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                        CursoModel curso = dataSnapshot.getValue(CursoModel.class);
                                        cursos.add(curso);
                                        Cccursos.setVisibility(View.VISIBLE);
                                        LLnocursos.setVisibility(View.GONE);
                                    }
                                }else{
                                    Cccursos.setVisibility(View.GONE);
                                    LLnocursos.setVisibility(View.VISIBLE);
                                }
                                rva.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println(error+"------------AAAAAA");

                            }
                        });
                        // Enlazamos la interfaz gráfica del RecyclerView con el código
                        rv = findViewById(R.id.rv_cursos);
                        // Establecemos que los elementos del RecyclerView se apilen verticalmente
                        lm = new LinearLayoutManager(context);
                        // Creamos un adaptador para el RecyclerView
                        rva = new RvCursos(context, cursos);
                        // Enlazamos el adaptador con el objeto RecyclerView
                        rv.setAdapter(rva);
                        rv.setLayoutManager(lm);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.dlMenu);
        toolbar = findViewById(R.id.main_toolbar);
        // setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    private void MenuP() {

        navigationView = findViewById(R.id.navegationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.itPerfil:{

                        Intent intent = new Intent(MenuActivity.this, PerfilActivity.class);
                        startActivity(intent);
                    }
                    break;

                    case  R.id.itRegMaestro:{

                        Intent intent = new Intent(MenuActivity.this, RegisterActivityMaestros.class);
                        startActivity(intent);
                        Toast.makeText(MenuActivity.this, "Registrar Maestro", Toast.LENGTH_SHORT).show();

                    }

                    break;

                    case  R.id.itRegAlumno:{

                        Intent intent = new Intent(MenuActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        Toast.makeText(MenuActivity.this, "Registrar Alumno", Toast.LENGTH_SHORT).show();

                    }

                    break;

                    case  R.id.itHorario:{

                        Intent intent = new Intent(MenuActivity.this, AlumnosHorarioActivity.class);
                        startActivity(intent);
                        Toast.makeText(MenuActivity.this, "Horario", Toast.LENGTH_SHORT).show();

                    }

                    break;

                    case  R.id.itRegCurso:{

                        Intent intent = new Intent(MenuActivity.this, CrearCurso.class);
                        startActivity(intent);
                        Toast.makeText(MenuActivity.this, "Registrar Curso", Toast.LENGTH_SHORT).show();

                    }

                    break;

                    case  R.id.itCerrarS:{

                        mAuth.signOut();

                        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }

                    break;

                }
                return false;
            }
        });

    }

}
















