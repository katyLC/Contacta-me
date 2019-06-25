package com.example.contactame.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.contactame.R;
import com.example.contactame.models.Usuario;
import com.example.contactame.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProveedorEditarDetalleActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView ivFoto;
    private FloatingActionButton fabFoto;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;

    private EditText etDistrito;
    private EditText etNombre;
    private EditText etApellidos;
    private EditText etEmail;
    private EditText etDni;
    private EditText etTelefono;
    private EditText etExperiencia;
    private EditText etDescripcion;

    private LinearLayout lyt_guardar;

    private TextView header_nombre;
    private TextView header_direccion;
    private CircleImageView header_foto;
    private Toolbar toolbar;
    private ActionBar actionBar;

    private FirebaseUser currentUser;

    private String fotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor_editar_detalle);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        initToolbar();
        initNavigationMenu();
        initComponent();

    }

    private void initComponent() {
        ivFoto = findViewById(R.id.iv_foto);
        etNombre = findViewById(R.id.et_nombre);
        etApellidos = findViewById(R.id.et_apellidos);
        etEmail = findViewById(R.id.et_email);
        etDni = findViewById(R.id.et_dni);
        etTelefono = findViewById(R.id.et_telefono);
        etExperiencia = findViewById(R.id.et_experiencia);

        fabFoto = findViewById(R.id.fab_foto);
        lyt_guardar = findViewById(R.id.lyt_guardar);

        fabFoto.setOnClickListener(this);

        lyt_guardar.setOnClickListener(this);
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

//                        if (!usuario.getFotoUrl().contains("http")) {
//                            try {
//                                Bitmap imagen = decodeFromFirebaseBase64(usuario.getFotoUrl());
//                                ivFoto.setImageBitmap(imagen);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Utils.setImageToImageViewFromFirebase(getApplicationContext(), ivFoto, usuario.getFotoUrl());
//                        }

//                        if (usuario.getFotoUrl() != null) {
                            Utils.setImageToImageViewFromFirebase(getApplicationContext(), ivFoto, usuario.getFotoUrl());
//                        }

                        etNombre.setText(usuario.getNombres());
                        etApellidos.setText(usuario.getApellidos());
                        etEmail.setText(usuario.getEmail());
                        etTelefono.setText(usuario.getTelefono());
                        etDni.setText(usuario.getDni());
                        etExperiencia.setText(usuario.getExperiencia());
                    }
                }
            });
        }
    }

    private void tomarFoto() {
        Intent tomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tomarFoto.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            ProveedorEditarDetalleActivity.this.startActivityForResult(tomarFoto, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivFoto.setImageBitmap(imageBitmap);
            if (imageBitmap != null) {
                encodeBitmapGuardarFirebase(imageBitmap);
            }
        }
    }

    private void encodeBitmapGuardarFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();


        final StorageReference fotosRef = mStorage.getReference().child("fotos/" + System.currentTimeMillis());

        UploadTask uploadTask = fotosRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Foto guardada correctamente", Toast.LENGTH_SHORT).show();
                fotosRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri fotoUrl = uri;
                        getFotoUrl(fotoUrl.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
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
                obtenerDatos();
                break;
            case R.id.fab_foto:
                tomarFoto();
                break;
        }
    }

    private void getFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    private void obtenerDatos() {
        String nombres = String.valueOf(etNombre.getText());
        String apellidos = String.valueOf(etApellidos.getText());
        String dni = String.valueOf(etDni.getText());
        String telefono = String.valueOf(etTelefono.getText());
        String experiencia = String.valueOf(etExperiencia.getText());
        actualizarPefil(nombres, apellidos, dni, telefono, experiencia, this.fotoUrl);
    }

    private void actualizarPefil(String nombres, String apellidos, String dni, String telefono, String experiencia, String fotoUrl) {

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = mFirestore.collection("usuarios").document(uid);
        docRef.update("nombres", nombres);
        docRef.update("apellidos", apellidos);
        docRef.update("dni", dni);
        docRef.update("experiencia", experiencia);
        docRef.update("telefono", telefono);
        docRef.update("fotoUrl", fotoUrl)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Datos actualizados correctamente",
                        Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ProveedorDetalleActivity.class);
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
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("deID", currentUser.getUid());
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

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
