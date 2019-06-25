package com.example.contactame.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.models.Usuario;
import com.example.contactame.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProveedorDetalleActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView civFoto;
    private ImageButton badge2;
    private TextView tvNombre;
    private TextView tvTelefono;
    private TextView tvDni;
    private TextView tvEmail;
    private TextView tvExperiencia;
    private TextView tvCalificacion;
    private FloatingActionButton fabEditar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

    private String uid;

    private TextView header_nombre;
    private TextView header_direccion;
    private CircleImageView header_foto;
    private Toolbar toolbar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor_detalle);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        initToolbar();
        initNavigationMenu();
        initComponents();
    }

    private void initComponents() {
        civFoto = findViewById(R.id.civ_foto);
        tvNombre = findViewById(R.id.tv_nombre);
        tvTelefono = findViewById(R.id.tv_telefono);
        tvDni = findViewById(R.id.tv_dni);
        tvEmail = findViewById(R.id.tv_email);
        tvExperiencia = findViewById(R.id.tv_experiencia);
        tvCalificacion = findViewById(R.id.tv_calificacion_promedio);
        fabEditar = findViewById(R.id.fab_editar);

        fabEditar.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser usuario) {
        if (usuario != null) {
            DocumentReference docRef = mFirestore.collection("usuarios").document(uid);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    if (usuario != null) {
                        Utils.setCircleImageToImageViewFromFirebase(getApplicationContext(), civFoto,
                            usuario.getFotoUrl(), 1, 000);
                        tvNombre.setText(String.format("%s %s", usuario.getNombres(), usuario.getApellidos()));
                        tvEmail.setText(usuario.getEmail());
                        tvTelefono.setText(usuario.getTelefono());
                        tvExperiencia.setText(usuario.getExperiencia());
                    }
                }
            });

        }
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Contactame!!!");
    }

    private void initNavigationMenu() {

        NavigationView nav_view = findViewById(R.id.nav_view);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);


        header_nombre = nav_view.getHeaderView(0).findViewById(R.id.header_nombre);
        header_direccion = nav_view.getHeaderView(0).findViewById(R.id.header_cambiar_estado);
        header_foto = nav_view.getHeaderView(0).findViewById(R.id.header_foto);

        header_direccion.setTextColor(getResources().getColor(R.color.blue_700));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name
            , R.string.app_name) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_perfil:
                        if (currentUser != null) {
                            Intent intent = new Intent(getApplicationContext(), ProveedorDetalleActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Debe de registrarse para poder acceder a su " +
                                    "perfil.",
                                Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_mensaje:
                        if (currentUser != null) {
                            Intent intent = new Intent(getApplicationContext(), ContactosActivity.class);
//                            intent.putExtra("deID", currentUser.getUid());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Debe de registrarse para poder acceder a su " +
                                    "perfil.",
                                Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_citas:
                        if (currentUser != null) {
                            Intent intent = new Intent(getApplicationContext(), CitasActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Debe de registrarse para ver sus citas.",
                                Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_servicio:
                        if (currentUser != null) {
                            Intent intent = new Intent(getApplicationContext(), ProveedorServicioActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Debe de registrarse para poder acceder a su " +
                                    "perfil.",
                                Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_logout:
                        if (currentUser != null) {
                            mAuth.signOut();
                            Toast.makeText(getApplicationContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent1);
                        } else {

                            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(login);
                        }
                        break;
                }
                drawer.closeDrawers();
                return true;
            }
        });
        drawer.closeDrawers();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fab_editar) {
            Intent intent = new Intent(this, ProveedorEditarDetalleActivity.class);
            startActivity(intent);
        }
    }
}
