package com.example.contactame.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.contactame.R;
import com.example.contactame.models.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProveedorDetalleFragment extends Fragment implements View.OnClickListener {


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
    private String uid;
    public ProveedorDetalleFragment() {
        // Required empty public constructor

    }

    public static ProveedorDetalleFragment newInstance() {
        ProveedorDetalleFragment fragment = new ProveedorDetalleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void initComponents(View view) {
//        toolbar_main = findViewById(R.id.toolbar_main);
//        container = findViewById(R.id.container);
        civFoto = view.findViewById(R.id.civ_foto);
//        badge2 = view.findViewById(R.id.badge_2);
        tvNombre = view.findViewById(R.id.tv_nombre);
        tvTelefono = view.findViewById(R.id.tv_telefono);
        tvDni = view.findViewById(R.id.tv_dni);
        tvEmail = view.findViewById(R.id.tv_email);
        tvExperiencia = view.findViewById(R.id.tv_experiencia);
        tvCalificacion = view.findViewById(R.id.tv_calificacion_promedio);
        fabEditar = view.findViewById(R.id.fab_editar);

        fabEditar.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proveedor_detalle, container, false);
        initComponents(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
                        tvNombre.setText(String.format("%s %s", usuario.getNombres(), usuario.getApellidos()));
                        tvEmail.setText(usuario.getEmail());
                        tvTelefono.setText(usuario.getTelefono());
                        tvExperiencia.setText(usuario.getExperiencia());
                    }
                }
            });

        }
    }

    @Override
    public void onClick(View v) {
       int i = v.getId();
        if (i == R.id.fab_editar) {
            ProveedorEditarFragment fragment;
            fragment = ProveedorEditarFragment.newInstance();
            getFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        }
    }
}
