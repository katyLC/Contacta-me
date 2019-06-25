package com.example.contactame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.adapter.ContactosGridAdapter;
import com.example.contactame.models.Contacto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class ContactosActivity extends AppCompatActivity {


    private ArrayList<Contacto> contactos = new ArrayList<>();
    ContactosGridAdapter contactosGridAdapter;

    private TextView header_nombre;
    private TextView header_direccion;
    private CircleImageView header_foto;
    private Toolbar toolbar;
    private ActionBar actionBar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView rvItemContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        initComponents();
        getUltimosMensajes();
    }

    private void initComponents() {
        rvItemContacto = findViewById(R.id.rv_item_contacto);

        initToolbar();
        initNavigationMenu();
    }

    private void getUltimosMensajes() {
        final String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance().collection("ultimos-mensajes")
            .document(uid)
            .collection("contactos")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                    if (documentChanges != null) {
                        for (DocumentChange doc : documentChanges) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                Contacto contacto = doc.getDocument().toObject(Contacto.class);
                                contactos.add(contacto);
                                setUpContactos(contactos);
                            }
                        }
                    }
                }
            });
    }

    private void setUpContactos(final ArrayList<Contacto> contactos) {
        contactosGridAdapter = new ContactosGridAdapter(getApplicationContext(), contactos);
        contactosGridAdapter.notifyDataSetChanged();
        rvItemContacto.setAdapter(contactosGridAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvItemContacto.setLayoutManager(mLayoutManager);
        contactosGridAdapter.setOnItemClickListener(new ContactosGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Contacto contacto, int position) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("contacto", contactos.get(position));
                startActivity(intent);
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
}
