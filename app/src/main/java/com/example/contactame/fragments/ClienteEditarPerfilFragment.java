package com.example.contactame.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.balysv.materialripple.MaterialRippleLayout;
import com.example.contactame.R;
import com.example.contactame.models.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClienteEditarPerfilFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private ImageView ivFoto;
    private EditText etNombre;
    private EditText etApellidos;
    private EditText etEmail;
    private EditText etDni;
    private EditText etTelefono;
    private LinearLayout lytForm;
    private LinearLayout lytGuardar;
    private MaterialRippleLayout lytNext;
    private FloatingActionButton fabFoto;

    public ClienteEditarPerfilFragment() {}

    public static ClienteEditarPerfilFragment newInstance() {
        ClienteEditarPerfilFragment fragment = new ClienteEditarPerfilFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cliente_editar_perfil, container, false);
        initComponents(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    private void initComponents(View view) {
        ivFoto = view.findViewById(R.id.iv_foto);
        etNombre = view.findViewById(R.id.et_nombre);
        etApellidos = view.findViewById(R.id.et_apellidos);
        etEmail = view.findViewById(R.id.et_email);
        etDni = view.findViewById(R.id.et_dni);
        etTelefono = view.findViewById(R.id.et_telefono);
        lytForm = view.findViewById(R.id.lyt_form);
        lytGuardar = view.findViewById(R.id.lyt_guardar);
        lytNext = view.findViewById(R.id.lyt_next);
        fabFoto = view.findViewById(R.id.fab_foto);


        lytGuardar.setOnClickListener(this);
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
                    Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                    ClienteEditarPerfilFragment fragment;
                    fragment = ClienteEditarPerfilFragment.newInstance();
                    getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
                }
            });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
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

}
