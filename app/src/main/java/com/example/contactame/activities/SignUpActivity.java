package com.example.contactame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.example.contactame.R;
import com.example.contactame.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText etNombre;
    private EditText etTelefono;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvRegistrar;
    private CircleImageView ivGoogle;

    private FrameLayout lyt_progress;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        estado = getIntent().getBooleanExtra("estado", true);

        initComponents();
    }

    private void initComponents() {
        etNombre = findViewById(R.id.et_nombre);
        etTelefono = findViewById(R.id.et_telefono);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tvRegistrar = findViewById(R.id.tv_registrar);
        //        ivGoogle = findViewById(R.id.iv_google);

        lyt_progress = findViewById(R.id.progress_view);

        tvRegistrar.setOnClickListener(this);
    }

    private void registrarNuevoUsuarioCorreo() {
        final String email;
        String password;
        final String nombre;
        final String telefono;

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        nombre = etNombre.getText().toString();
        telefono = etTelefono.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor ingresa tu email...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor ingresa tu contraseña...", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarProgress();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final String uid = mAuth.getCurrentUser().getUid();

                        FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (task.isSuccessful()) {
                                        String token = task.getResult().getToken();
                                        Usuario usuario = new Usuario(uid, nombre, "", "", email, telefono, estado,
                                            "", "", "", token, "");
                                        db.collection("usuarios").document(uid)
                                            .set(usuario)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Registro satisfactorio!", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                    }
                                }
                            });

                    } else {
                        Toast.makeText(getApplicationContext(), "Registro fallido! Por favor intente de nuevo",
                            Toast.LENGTH_LONG).show();
                    }
                    lyt_progress.setVisibility(View.GONE);
                }
            });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_registrar) {
            registrarNuevoUsuarioCorreo();
        }
    }

    private void mostrarProgress() {
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
    }
}
