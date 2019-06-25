package com.example.contactame.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.adapter.FavoritosGridAdapter;
import com.example.contactame.models.Proveedor;
import com.example.contactame.models.Servicio;
import com.example.contactame.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.Map;

public class FavoritosActivity extends AppCompatActivity {

    private View view;
    private ArrayList<Usuario> usuarios;
    private ArrayList<Servicio> servicios;
    private ArrayList<Proveedor> proveedores;
    private FavoritosGridAdapter mAdapter;
    private RecyclerView ivRecyclerFavorito;

    int numeroColumnas = 2;

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
        setContentView(R.layout.activity_favoritos);


        mFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            getFavoritos(uid);
        }

        initToolbar();
        initNavigationMenu();
        initComponent();

    }

    private void initComponent() {
        ivRecyclerFavorito = findViewById(R.id.rv_item_favorito);
        ivRecyclerFavorito.setLayoutManager(new GridLayoutManager(getApplicationContext(), numeroColumnas));
        ivRecyclerFavorito.setItemAnimator(new DefaultItemAnimator());
    }

    private void updateUI(ArrayList<Proveedor> proveedores) {
        mAdapter = new FavoritosGridAdapter(proveedores);
        ivRecyclerFavorito.setAdapter(mAdapter);
    }

    private void getFavoritos(String uid) {
        DocumentReference docFavoritos = mFirestore.collection("favoritos").document(uid);
        final ArrayList<String> uidList = new ArrayList<>();
        docFavoritos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Map<String, Object> map = task.getResult().getData();
                        for (Map.Entry<String, Object> resultado : map.entrySet()) {
                            uidList.add(resultado.getKey());
                            getProveedores(uidList);
                        }
                    }
                }
            }
        });
    }

//    private void getServicios(final ArrayList<String> uidList) {
//        final ArrayList<Servicio> servicios = new ArrayList<>();
//        final ArrayList<Proveedor> proveedores = new ArrayList<>();
//
//        //        for (String uid : uidList) {
//        //            DocumentReference docServicios = mFirestore.collection("servicios").document(uid);
//        //                docServicios.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//        //                    @Override
//        //                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//        //                        Servicio servicio = documentSnapshot.toObject(Servicio.class);
//        //                        if (servicio != null) {
//        //                            Proveedor proveedor = new Proveedor();
//        //                            proveedor.setServicio_uid(servicio.getServicio_uid());
//        //                            proveedor.setUsuario_uid(servicio.getUsuario_uid());
//        //                            proveedor.setServicio_categoria(servicio.getCategoria());
//        //                            proveedor.setServicio_ciudad(servicio.getCiudad());
//        //                            proveedor.setServicio_descripcion(servicio.getDescripcion());
//        //                            proveedor.setServicio_horarioAtencion(servicio.getHorarioAtencion());
//        //                            proveedor.setServicio_ubicacion(servicio.getUbicacion());
//        //                            proveedores.add(proveedor);
//        //                        }
//        //                        getUsuarios(proveedores);
//        //                    }
//        //                });
//        //        }
//        mFirestore.collection("servicios")
//            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    ArrayList<Servicio> servicios = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        for (String uid : uidList) {
//                            if (document.getId().equals(uid)) {
//                                Servicio servicio = document.toObject(Servicio.class);
//                                Proveedor proveedor = new Proveedor();
//                                proveedor.setServicio_uid(servicio.getServicio_uid());
//                                proveedor.setUsuario_uid(servicio.getUsuario_uid());
//                                proveedor.setServicio_categoria(servicio.getCategoria());
//                                proveedor.setServicio_ciudad(servicio.getCiudad());
//                                proveedor.setServicio_descripcion(servicio.getDescripcion());
//                                proveedor.setServicio_horarioAtencion(servicio.getHorarioAtencion());
//                                proveedor.setServicio_ubicacion(servicio.getUbicacion());
//                                proveedores.add(proveedor);
//                            }
//                        }
//                    }
//                    getUsuarios(proveedores);
//                }
//            }
//        });
//
//    }
//
//    private void getUsuarios(final ArrayList<Proveedor> proveedores) {
//        for (final Proveedor proveedor : proveedores) {
//            DocumentReference docUsuarios = mFirestore.collection("usuarios").document(proveedor.getUsuario_uid());
//            docUsuarios.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
//                    if (usuario != null) {
//                        for (Proveedor proveedor : proveedores) {
//                            if (usuario.getServicio_uid().equals(proveedor.getServicio_uid())) {
//                                proveedor.setProveedor_dni(usuario.getDni());
//                                proveedor.setProveedor_email(usuario.getEmail());
//                                proveedor.setProveedor_nombre(String.format("%s %s", usuario.getNombres(), usuario.getApellidos()));
//                                break;
//                            }
//                        }
//                    }
//                    updateUI(proveedores);
//                }
//            });
//        }
//    }

    private void getProveedores(final ArrayList<String> uidList) {
        mFirestore.collection("proveedores")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    ArrayList<Proveedor> proveedores = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            for (String uid : uidList) {
                                if (document.getId().equals(uid)) {
                                    Proveedor proveedor = document.toObject(Proveedor.class);
                                    proveedores.add(proveedor);
                                    break;
                                }
                            }
                        }
                        updateUI(proveedores);
                    }
                }
            });
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

    @Override
    public void onStart() {
        super.onStart();
    }
}
