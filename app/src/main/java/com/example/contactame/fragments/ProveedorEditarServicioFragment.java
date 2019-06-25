package com.example.contactame.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.models.Servicio;
import com.example.contactame.models.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class ProveedorEditarServicioFragment extends Fragment implements View.OnClickListener {

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

    public ProveedorEditarServicioFragment() {
    }

    public static ProveedorEditarServicioFragment newInstance() {
        ProveedorEditarServicioFragment fragment = new ProveedorEditarServicioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proveedor_editar_servicio, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        etHorarioAtencion = view.findViewById(R.id.et_horarioAtencion);
        etPrecioReferencial = view.findViewById(R.id.et_precioReferencial);
        etDescripcion = view.findViewById(R.id.et_descripcion);
        etCategoria = view.findViewById(R.id.et_categoria);
        etPais = view.findViewById(R.id.et_pais);
        etCiudad = view.findViewById(R.id.et_ciudad);
        lyt_guardar = view.findViewById(R.id.lyt_guardar);
        etExperiencia = view.findViewById(R.id.et_experiencia);
        scUbicacion = view.findViewById(R.id.sc_ubicacion);

        etPais.setOnClickListener(this);
        etCiudad.setOnClickListener(this);
        etCategoria.setOnClickListener(this);
        lyt_guardar.setOnClickListener(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
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
                                    etCiudad.setText(servicio.getCiudad());
                                    etDescripcion.setText(servicio.getDescripcion());
                                    etHorarioAtencion.setText(servicio.getHorarioAtencion());
                                    etPais.setText(servicio.getPais());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        GeoPoint geoPoint = new GeoPoint(0, 0);
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
                    docRef.update("pais", servicio.getPais())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                                ProveedorEditarServicioFragment fragment;
                                fragment = ProveedorEditarServicioFragment.newInstance();
                                getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                            }
                        });
                }
            }
        });
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
}
