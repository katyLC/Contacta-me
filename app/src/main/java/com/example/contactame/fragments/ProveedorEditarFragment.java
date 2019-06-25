package com.example.contactame.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import java.io.ByteArrayOutputStream;


public class ProveedorEditarFragment extends Fragment implements View.OnClickListener {

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

    private ImageView mImageLabel;

    private EditText etDescripcion;
    private MaterialRippleLayout lyt_next;
    private LinearLayout lyt_guardar;
    private String url;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public ProveedorEditarFragment() {
    }

    public static ProveedorEditarFragment newInstance() {
        ProveedorEditarFragment fragment = new ProveedorEditarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_proveedor_editar, container, false);
        initComponent(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    private void initComponent(View view) {
        ivFoto = view.findViewById(R.id.iv_foto);
        etNombre = view.findViewById(R.id.et_nombre);
        etApellidos = view.findViewById(R.id.et_apellidos);
        etEmail = view.findViewById(R.id.et_email);
        etDni = view.findViewById(R.id.et_dni);
        etTelefono = view.findViewById(R.id.et_telefono);
        etExperiencia = view.findViewById(R.id.et_experiencia);

        fabFoto = view.findViewById(R.id.fab_foto);
        lyt_next = view.findViewById(R.id.lyt_next);
        lyt_guardar = view.findViewById(R.id.lyt_guardar);

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
        if (tomarFoto.resolveActivity(getContext().getApplicationContext().getPackageManager()) != null) {
            ProveedorEditarFragment.this.startActivityForResult(tomarFoto, REQUEST_IMAGE_CAPTURE);
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
        //        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        byte[] data = baos.toByteArray();


        final StorageReference fotosRef = mStorage.getReference().child("fotos/" + System.currentTimeMillis());

        UploadTask uploadTask = fotosRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Foto guardada correctamente", Toast.LENGTH_SHORT).show();
//                url = String.valueOf(fotosRef.getDownloadUrl());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
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
            case R.id.fab_foto:
                tomarFoto();
                break;
        }
    }

    private void actualizarPerfil() {
        String nombres = String.valueOf(etNombre.getText());
        String apellidos = String.valueOf(etApellidos.getText());
        String dni = String.valueOf(etDni.getText());
        String telefono = String.valueOf(etTelefono.getText());
        String experiencia = String.valueOf(etExperiencia.getText());

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = mFirestore.collection("usuarios").document(uid);
        docRef.update("nombres", nombres);
        docRef.update("apellidos", apellidos);
        docRef.update("dni", dni);
        docRef.update("experiencia", experiencia);
        docRef.update("telefono", telefono)
//        docRef.update("fotoUrl", url);
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
