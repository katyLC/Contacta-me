package com.example.contactame.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.contactame.R;
import com.example.contactame.models.Servicio;
import com.example.contactame.models.Usuario;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ProveedorServicioActivity extends AppCompatActivity implements View.OnClickListener,
    CompoundButton.OnCheckedChangeListener, LocationListener {

    private Context context;
    private EditText etPais;
    private EditText etCiudad;
    private EditText etHorarioAtencion;
    private EditText etPrecioReferencial;
    private EditText etCategoria;
    private EditText etExperiencia;
    private String single_choice_selected;
    private EditText etDescripcion;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private LinearLayout lyt_guardar;
    private String uid;
    private SwitchCompat scUbicacion;
    private double latitude;
    private double longitude;

    private LocationManager mLocationManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private TextView header_nombre;
    private TextView header_direccion;
    private CircleImageView header_foto;
    private Toolbar toolbar;
    private ActionBar actionBar;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor_servicio);

        requestPermision();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initToolbar();
        initNavigationMenu();
        initComponents();
    }

    private void initComponents() {
        etHorarioAtencion = findViewById(R.id.et_horarioAtencion);
        etPrecioReferencial = findViewById(R.id.et_precioReferencial);
        etDescripcion = findViewById(R.id.et_descripcion);
        etCategoria = findViewById(R.id.et_categoria);
        etPais = findViewById(R.id.et_pais);
        etCiudad = findViewById(R.id.et_ciudad);
        lyt_guardar = findViewById(R.id.lyt_guardar);
        etExperiencia = findViewById(R.id.et_experiencia);
        scUbicacion = findViewById(R.id.sc_ubicacion);

        etPais.setOnClickListener(this);
        etCiudad.setOnClickListener(this);
        etCategoria.setOnClickListener(this);
        lyt_guardar.setOnClickListener(this);

        scUbicacion.setOnCheckedChangeListener(this);
    }

    private void updateUI(FirebaseUser usuario) {
        if (usuario != null) {
            uid = usuario.getUid();
            DocumentReference docRef = mFirestore.collection("usuarios").document(uid);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    DocumentReference servicioRef = null;
                    if (usuario != null) {
                        servicioRef = mFirestore.collection("servicios").document(usuario.getServicio_uid());
                    }
                    if (usuario != null) {
                        servicioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Servicio servicio = documentSnapshot.toObject(Servicio.class);
                                if (servicio != null) {
                                    etCategoria.setText(String.valueOf(servicio.getCategoria()));
                                    etCiudad.setText(String.valueOf(servicio.getCiudad()));
                                    etDescripcion.setText(servicio.getDescripcion());
                                    etHorarioAtencion.setText(servicio.getHorarioAtencion());
                                    etPais.setText(String.valueOf(servicio.getPais()));
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void mostrarPaisDialog(final View v) {
        final String[] array = new String[]{
            "Peru"
        };
        single_choice_selected = array[0];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pais");
        builder.setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
            }
        });
        builder.setPositiveButton("Confirmar", null);
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarCiudadDialog(final View v) {
        final String[] array = new String[]{
            "Amazonas", "Ancash", "Apurimac", "Arequipa", "Ayacucho", "Cajamarca", "Callao", "Cusco", "Huancavelica", "Huanuco", "Ica", "Junin", "La Libertad", "Lambayeque", "Lima", "Loreto", "Madre De Dios", "Moquegua", "Pasco", "Piura", "Puno", "San Martin", "Tacna", "Tumbes", "Ucayali"
        };
        single_choice_selected = array[22];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ciudad");
        builder.setSingleChoiceItems(array, 21, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
            }
        });
        builder.setPositiveButton("Confirmar", null);
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDistritoDialog(final View v) {
        final String[] array = new String[]{
            "Alto de la Alianza", "Calana", "Ciudad Nueva", "Gregorio Albarracin", "Pocollay"
        };
        single_choice_selected = array[0];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Distrito");
        builder.setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
            }
        });
        builder.setPositiveButton("Confirmar", null);
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarCategoriaDialog(final View v) {
        final String[] array = new String[]{
            "Electricidad", "Carpintería", "Cerrajería", "Mecánico", "Pintura", "Albañilería", "Limpieza"
        };
        single_choice_selected = array[0];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Categoria");
        builder.setSingleChoiceItems(array, 0,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((EditText) v).setText(array[i]);
                }
            });
        builder.setPositiveButton("Confirmar", null);
        builder.setNegativeButton("Cancelar", null);
        builder.show();

    }

    private Servicio leerDatos() {
        String categoria = String.valueOf(etCategoria.getText());
        String descripcion = String.valueOf(etDescripcion.getText());
        String horario = String.valueOf(etHorarioAtencion.getText());
        String pais = String.valueOf(etPais.getText());
        String ciudad = String.valueOf(etCiudad.getText());
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        return new Servicio(categoria, "", "", descripcion, pais, ciudad, horario, geoPoint);
    }

    private void guardarServicio() {
        final Servicio servicio = leerDatos();
        DocumentReference usuarioRef = mFirestore.collection("usuarios").document(uid);

        usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                DocumentReference docRef = null;
                if (usuario != null) {
                    docRef = mFirestore.collection("servicios").document(usuario.getServicio_uid());
                }
                if (docRef != null) {
                    docRef.update("categoria", servicio.getCategoria());
                    docRef.update("ciudad", servicio.getCiudad());
                    docRef.update("descripcion", servicio.getDescripcion());
                    docRef.update("horarioAtencion", servicio.getHorarioAtencion());
                    docRef.update("pais", servicio.getPais());
                    docRef.update("ubicacion", servicio.getUbicacion())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        });
                }
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
            case R.id.et_pais:
                mostrarPaisDialog(v);
                break;
            case R.id.et_ciudad:
                mostrarCiudadDialog(v);
                break;
            case R.id.et_categoria:
                mostrarCategoriaDialog(v);
                break;
            case R.id.lyt_guardar:
                guardarServicio();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.sc_ubicacion:
                if (isChecked) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(ProveedorServicioActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.d("Ubicacion", String.format("%s %s", latitude, longitude));
                            }
                        }
                    });
                }

        }
    }

    private void requestPermision() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("Wiwi", "Long: " + location.getLongitude() + " - Lat: " + location.getLatitude());
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

}
