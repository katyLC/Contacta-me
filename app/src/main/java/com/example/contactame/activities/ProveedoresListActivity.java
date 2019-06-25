package com.example.contactame.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.adapter.AdapterGridProveedores;
import com.example.contactame.data.CategoriaData;
import com.example.contactame.fragments.ProveedorDetalleFragment;
import com.example.contactame.models.Categoria;
import com.example.contactame.models.Proveedor;
import com.example.contactame.models.Servicio;
import com.example.contactame.models.Usuario;
import com.example.contactame.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProveedoresListActivity extends AppCompatActivity {

    private TextView header_nombre;
    private TextView header_direccion;
    private TextView tv_nombre_categoria;
    private TextView tvCantidadProveedores;

    private CircleImageView header_foto;
    private ImageView iv_categoria;

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ActionBar actionBar;

    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    String categoria;

    private int cantidadProveedores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedores_list);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        categoria = getIntent().getStringExtra("categoria");

        iv_categoria = findViewById(R.id.iv_categoria);
        tv_nombre_categoria = findViewById(R.id.tv_nombre_categoria);
        tvCantidadProveedores = findViewById(R.id.tv_cantidad_proveedores);

        initToolbar();
        initNavigationMenu();

        setUpRecyclerView(categoria);
    }

    private void setUpRecyclerView(String categoria) {
        recyclerView = findViewById(R.id.rv20);
        int orientation = this.getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        }
        getProveedores(categoria);
    }

    private void getProveedores(String categoria) {
        final ArrayList<Proveedor> proveedores = new ArrayList<>();
        mFirestore.collection("proveedores")
            .whereEqualTo("servicio_categoria", categoria)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Proveedor proveedor = document.toObject(Proveedor.class);
                            proveedores.add(proveedor);
                        }
                        setUpProveedores(proveedores);
                        if (proveedores.size() == 1) {
                            tvCantidadProveedores.setText(String.format("Hay %s proveedor disponible.",
                                proveedores.size()));
                        } else if (proveedores.size() > 1) {
                            tvCantidadProveedores.setText(String.format("Hay %s proveedores disponibles.",
                                proveedores.size()));
                        } else {
                            tvCantidadProveedores.setText("No hay proveedores disponibles.");
                        }
                    }
                }
            });
    }

    private void setUpProveedores(final ArrayList<Proveedor> proveedores) {
        AdapterGridProveedores adapterGridProveedores = new AdapterGridProveedores(getApplicationContext(), proveedores);
        adapterGridProveedores.setItemClickListener(new AdapterGridProveedores.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Proveedor proveedor, int position) {
                Toast.makeText(ProveedoresListActivity.this, proveedor.getServicio_categoria(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ProveedorMostrarPerfilClienteActivity.class);
                intent.putExtra("proveedor", proveedores.get(position));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapterGridProveedores);

        setupImagenCategoria();
    }

    private void setupImagenCategoria() {

        List<Categoria> categorias = CategoriaData.getCategoria(getApplicationContext());

        for (Categoria categoria : categorias) {
            if (categoria.getDescripcion().equals(this.categoria)) {
                int id = categoria.getImage_bg();
                Utils.setImageToImageView(getApplicationContext(), iv_categoria, id);
            }
        }

        tv_nombre_categoria.setText(categoria);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setNavigationIcon(R.drawable.baseline_menu_black_24);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.md_white_1000));
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
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }
}
