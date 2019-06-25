package com.example.contactame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.models.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail;
    private EditText etPassword;
    private Button btIngresar;
    private Button btRegistarGoogle;
    private Button btRegistarEmail;
    private FrameLayout lyt_progress;

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();

        FirebaseApp.initializeApp(getApplicationContext());

        configGoogle();

        setClicks();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initComponents() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btIngresar = findViewById(R.id.bt_ingresar);
        //        btRegistarGoogle = findViewById(R.id.bt_registar_google);
        btRegistarEmail = findViewById(R.id.bt_registar_email);
        lyt_progress = findViewById(R.id.progress_view);
    }

    private void setClicks() {
        //        btRegistarGoogle.setOnClickListener(this);
        btRegistarEmail.setOnClickListener(this);
        btIngresar.setOnClickListener(this);
    }

    private void configGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntentWithGoogle = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntentWithGoogle, RC_SIGN_IN);

        mostrarProgress();
    }

    private void signInWithEmail() {

        String email;
        String password;

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor ingresa tu email...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor ingresa tu contraseña...", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarProgress();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Logueado correctamente.",
                            Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Autenticación fallida.",
                            Toast.LENGTH_SHORT).show();
                    }
                    lyt_progress.setVisibility(View.GONE);
                }
            });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//
//            }
//        }
//    }

//    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//
//        mAuth.signInWithCredential(credential)
//            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//
//                    if (task.isSuccessful()) {
//
//                        String uid = mAuth.getCurrentUser().getUid();
//                        Usuario usuario = new Usuario(mAuth.getUid(),
//                            mAuth.getCurrentUser().getDisplayName(), "", "",
//                            mAuth.getCurrentUser().getEmail(), "", true, "", "", "");
//
//                        db.collection("usuarios").document(uid)
//                            .set(usuario)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText(getApplicationContext(), "Bienvenido!", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                    startActivity(intent);
//                                }
//                            });
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Registro fallido! Por favor intente de nuevo",
//                            Toast.LENGTH_SHORT).show();
//                    }
//                    lyt_progress.setVisibility(View.GONE);
//
//                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                    startActivity(intent);
//                }
//            });
//    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        /*if (i == R.id.bt_registar_google) {
            signInWithGoogle();
        } else*/
        if (i == R.id.bt_registar_email) {
            mostrarSelector();
        } else if (i == R.id.bt_ingresar) {
            signInWithEmail();
        }
    }

    private void mostrarProgress() {
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
    }

    private void mostrarSelector() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alert_cliente_proveedor);
        View v = getLayoutInflater().inflate(R.layout.alert_cliente_proveedor, null);
        Button proveedor, cliente;
        proveedor = v.findViewById(R.id.bt_proveedor);
        cliente = v.findViewById(R.id.bt_cliente);

        proveedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra("estado", false);
                startActivity(intent);
            }
        });

        cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra("estado", true);
                startActivity(intent);
            }
        });

        builder.setView(v);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}