package com.example.contactame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.fragments.ProveedorDetalleFragment;
import com.example.contactame.models.Proveedor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ProveedorMostrarPerfilClienteActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvCiudadProveedor;
    private TextView tvCategoria;
    private TextView tvNombreProveedor;
    private TextView tvExperiencia;
    private TextView tvDiasAtencion;
    private TextView tvHorarioAtencion;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TextView header_nombre;
    private TextView header_direccion;
    private CircleImageView header_foto;
    private ImageView ivFavorito;
    private ImageView ivMensaje;


    FirebaseUser currentUser;

    private boolean mFavorito;

    Proveedor proveedor;

    private FirebaseFirestore mFirebaseFirestore;
    private ListenerRegistration listenerRegistration;
    private String uid;
    Boolean esFavorito;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor_mostrar_perfil_cliente);

        initToolbar();
        initNavigationMenu();
        initComponents();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void esFavorito(final Proveedor proveedor, String uid) {
        DocumentReference favoritosRef = mFirebaseFirestore.collection("favoritos").document(uid);
        favoritosRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                esFavorito = false;
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Map<String, Object> map = task.getResult().getData();
                        for (Map.Entry<String, Object> resultado : map.entrySet()) {
                            if (resultado.getKey().equals(proveedor.getProveedor_uid())) {
                                esFavorito = true;
                                break;
                            }
                        }
                    }
                    updateUI(proveedor, esFavorito);
                }
            }
        });
    }

    private void initComponents() {
        ivMensaje = findViewById(R.id.iv_mensaje);
        ivFavorito = findViewById(R.id.iv_favorito);
        tvCiudadProveedor = findViewById(R.id.tv_ciudad_proveedor);
        tvCategoria = findViewById(R.id.tv_categoria);
        tvNombreProveedor = findViewById(R.id.tv_nombre_proveedor);
        tvExperiencia = findViewById(R.id.tv_experiencia);
        tvDiasAtencion = findViewById(R.id.tv_dias_atencion);
        tvHorarioAtencion = findViewById(R.id.tv_horario_atencion);
        findViewById(R.id.nav_logout);

        ivFavorito.setOnClickListener(this);
        ivMensaje.setOnClickListener(this);
    }

    private void updateUI(Proveedor proveedor, Boolean esFavorito) {

        if (esFavorito) {
            ivFavorito.setImageResource(R.drawable.ic_favorite);
        } else {
            ivFavorito.setImageResource(R.drawable.ic_favorite_border_24dp);
        }

        tvCiudadProveedor.setText(proveedor.getServicio_ciudad());
        tvCategoria.setText(proveedor.getServicio_categoria());
        tvNombreProveedor.setText(proveedor.getProveedor_nombre());
        tvExperiencia.setText(proveedor.getServicio_descripcion());
        tvHorarioAtencion.setText(proveedor.getServicio_horarioAtencion());
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar_cliente);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Proveedor!!!");
    }

    private void initNavigationMenu() {

        NavigationView nav_view = findViewById(R.id.nav_view);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout_cliente);


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

    private void agregarEliminarFavorito() {

        if (mAuth.getCurrentUser() != null) {
            Log.d("USUARIO FAVORITO", String.valueOf(esFavorito));
            DocumentReference favoritoReference = mFirebaseFirestore.collection("favoritos").document(uid);
            Map<String, Object> favorito = new HashMap<>();

            if (esFavorito) {
                favorito.put(proveedor.getProveedor_uid(), FieldValue.delete());
                favoritoReference.update(favorito).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ivFavorito.setImageResource(R.drawable.ic_favorite_border_24dp);
                        } else {
                            Toast.makeText(ProveedorMostrarPerfilClienteActivity.this, "Intente de nuevo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                favoritoReference.update(proveedor.getProveedor_uid(), true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ivFavorito.setImageResource(R.drawable.ic_favorite);
                            } else {
                                Toast.makeText(ProveedorMostrarPerfilClienteActivity.this, "Intente de nuevo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        } else {
            Toast.makeText(this, "Tienes que estar registrado para poder darle favorito a este proveedor",
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        proveedor = intent.getParcelableExtra("proveedor");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("USUARIO UID SERVICIO", String.valueOf(proveedor.getServicio_uid()));

        if (currentUser != null) {
            uid = currentUser.getUid();

            DocumentReference favoritosReference = mFirebaseFirestore.collection("favoritos").document(uid);
            listenerRegistration = favoritosReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        esFavorito = false;
                        Map<String, Object> map = documentSnapshot.getData();
                        for (Map.Entry<String, Object> resultado : map.entrySet()) {
                            if (resultado.getKey().equals(proveedor.getProveedor_uid())) {
                                esFavorito = true;
                                break;
                            }
                        }
                    }
                    esFavorito(proveedor, uid);
                }
            });
        } else {
            updateUI(proveedor, false);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.iv_favorito:
                agregarEliminarFavorito();
                break;
            case R.id.iv_mensaje:
                if (mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("deID", uid);
                    intent.putExtra("proveedor", proveedor);
                    startActivity(intent);
                    break;
                } else {
                    Toast.makeText(this, "Debe de registrarse para contactarse con el proveedor.", Toast.LENGTH_SHORT).show();
                }
        }
    }
}