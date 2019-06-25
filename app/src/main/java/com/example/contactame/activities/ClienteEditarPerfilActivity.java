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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.balysv.materialripple.MaterialRippleLayout;
import com.example.contactame.R;
import com.example.contactame.fragments.ClienteEditarPerfilFragment;
import com.example.contactame.fragments.ProveedorDetalleFragment;
import com.example.contactame.models.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import de.hdodenhof.circleimageview.CircleImageView;

public class ClienteEditarPerfilActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView ivFoto;
    private EditText etNombre;
    private EditText etApellidos;
    private EditText etEmail;
    private EditText etDni;
    private EditText etTelefono;
    private LinearLayout lytForm;
    private LinearLayout lytGuardar;
    private FloatingActionButton fabFoto;

    private TextView header_nombre;
    private TextView header_direccion;
    private CircleImageView header_foto;

    private Toolbar toolbar;
    private ActionBar actionBar;

    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_editar_perfil);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        initToolbar();
        initNavigationMenu();
        initComponents();
    }

    private void initComponents() {
        ivFoto = findViewById(R.id.iv_foto);
        etNombre = findViewById(R.id.et_nombre);
        etApellidos = findViewById(R.id.et_apellidos);
        etEmail = findViewById(R.id.et_email);
        etDni = findViewById(R.id.et_dni);
        etTelefono = findViewById(R.id.et_telefono);
        lytForm = findViewById(R.id.lyt_form);
        lytGuardar = findViewById(R.id.lyt_guardar);
        fabFoto = findViewById(R.id.fab_foto);

        lytGuardar.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Contactame!!!");
    }

    private void updateUI(FirebaseUser usuario) {
        if (usuario != null) {
            String uid = usuario.getUid();
            DocumentReference docRef = mFirestore.collection("usuarios").document(uid);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    if (usuario != null) {
                        etNombre.setText(usuario.getNombres());
                        etApellidos.setText(usuario.getApellidos());
                        etEmail.setText(usuario.getEmail());
                        etTelefono.setText(usuario.getTelefono());
                        etDni.setText(usuario.getDni());
                    }
                }
            });
        }
    }

    private void actualizarPerfil() {
        String nombres = String.valueOf(etNombre.getText());
        String apellidos = String.valueOf(etApellidos.getText());
        String dni = String.valueOf(etDni.getText());
        String telefono = String.valueOf(etTelefono.getText());

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = mFirestore.collection("usuarios").document(uid);
        docRef.update("nombres", nombres);
        docRef.update("apellidos", apellidos);
        docRef.update("dni", dni);
        docRef.update("telefono", telefono).
            addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.lyt_guardar:
                actualizarPerfil();
                break;
        }
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
                            Intent intent = new Intent(getApplicationContext(), ClienteEditarPerfilActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Debe de registrarse para poder acceder a su " +
                                    "perfil.",
                                Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_favorito:
                        if (currentUser != null) {
                            Intent intent = new Intent(getApplicationContext(), FavoritosActivity.class);
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

}
