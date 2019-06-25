package com.example.contactame.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.contactame.R;
import com.example.contactame.adapter.ChatAdapter;
import com.example.contactame.models.Contacto;
import com.example.contactame.models.Mensaje;
import com.example.contactame.models.Proveedor;
import com.example.contactame.models.Usuario;
import com.example.contactame.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Mensaje> mensajes = new ArrayList<>();
    ChatAdapter chatAdapter;
    private RecyclerView rvChatDetalle;
    private String deID;
    private String paraID;
    private ImageView ivEnviarMensaje;
    private EditText ietMensaje;
    private ImageView ivAgendar;

    private Usuario usuario;
    private Proveedor proveedor;
    private Contacto contacto;
    private boolean isCliente;
    private String usuario_nombre;
    private String proveedor_uid;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseAuth = FirebaseAuth.getInstance();

        deID = firebaseAuth.getCurrentUser().getUid();

        // Lista de contactos - mensajes
        contacto = getIntent().getParcelableExtra("contacto");

        // Lista de proveedores
        proveedor = getIntent().getParcelableExtra("proveedor");


        if (contacto != null) {
            paraID = contacto.getUuid();
            proveedor_uid = contacto.getProveedor_uid();
        } else if(proveedor != null) {
            paraID = proveedor.getUsuario_uid();
            proveedor_uid = proveedor.getProveedor_uid();
        } else {
            paraID = getIntent().getStringExtra("deID");
            usuario_nombre = getIntent().getStringExtra("usuario_nombre");
            proveedor_uid = getIntent().getStringExtra("proveedor_uid");
        }

        Log.d("deID", deID);
        Log.d("paraID", paraID);

        initComponents();
    }

    private void initComponents() {
        rvChatDetalle = findViewById(R.id.rv_chat_detalle);
        ivEnviarMensaje = findViewById(R.id.iv_enviar_mensaje);
        ietMensaje = findViewById(R.id.iet_mensaje);
        ivAgendar = findViewById(R.id.iv_agendar);

        ivAgendar.setOnClickListener(this);
        ivEnviarMensaje.setOnClickListener(this);

        initToolbar();
        getMensajes();
        getUsuario();
    }

    public void getUsuario() {

        FirebaseFirestore.getInstance().collection("usuarios")
            .document(deID)
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    isCliente(usuario);
                }
            });
    }

    public void isCliente(Usuario usuario) {
        isCliente = false;
        if (usuario.isEstado()) {
            isCliente = true;
            ivAgendar.setImageResource(R.drawable.ic_camera_24dp);
        }
    }

    private void getMensajes() {
        if (deID != null) {
            FirebaseFirestore.getInstance().collection("chats")
                .document(deID)
                .collection(paraID)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                        if (documentChanges != null) {
                            for (DocumentChange doc : documentChanges) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Mensaje mensaje = doc.getDocument().toObject(Mensaje.class);
                                    mensajes.add(mensaje);
                                }
                            }
                            setUpMensajes(mensajes);
                        }
                    }
                });
        }
    }

    private void enviarMensaje() {
        String texto = ietMensaje.getText().toString();
        ietMensaje.setText(null);

        long timestamp = System.currentTimeMillis();

        final Mensaje mensaje = new Mensaje();
        mensaje.setDeID(deID);
        mensaje.setParaID(paraID);
        mensaje.setTimestamp(timestamp);
        mensaje.setMensaje(texto);


        if (!mensaje.getMensaje().isEmpty()) {
            FirebaseFirestore.getInstance().collection("chats")
                .document(deID)
                .collection(paraID)
                .add(mensaje)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("MENSAJE", documentReference.getId());

                        DocumentReference usuarioRef = FirebaseFirestore.getInstance().collection("usuarios")
                            .document(paraID);
                        usuarioRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                    Contacto contacto = new Contacto();
                                    contacto.setUuid(paraID);
                                    contacto.setUsuario_nombre(String.format("%s %s", usuario.getNombres(), usuario.getApellidos()));
                                    contacto.setFotoUrl(usuario.getFotoUrl());
                                    contacto.setTimeStamp(mensaje.getTimestamp());
                                    contacto.setUltimoMensaje(mensaje.getMensaje());
                                    contacto.setProveedor_uid(usuario.getProveedor_uid());

                                    FirebaseFirestore.getInstance().collection("ultimos-mensajes")
                                        .document(deID)
                                        .collection("contactos")
                                        .document(paraID)
                                        .set(contacto);
                                }
                            });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Teste", e.getMessage(), e);
                    }
                });

            FirebaseFirestore.getInstance().collection("chats")
                .document(paraID)
                .collection(deID)
                .add(mensaje)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("MENSAJE", documentReference.getId());
                        final Contacto contacto = new Contacto();

                        DocumentReference usuarioRef = FirebaseFirestore.getInstance().collection("usuarios")
                            .document(deID);
                        usuarioRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                    contacto.setUuid(deID);
                                    contacto.setUsuario_nombre(usuario.getNombres());
                                    contacto.setFotoUrl(usuario.getFotoUrl());
                                    contacto.setTimeStamp(mensaje.getTimestamp());
                                    contacto.setUltimoMensaje(mensaje.getMensaje());
                                    contacto.setProveedor_uid(usuario.getProveedor_uid());
                                    FirebaseFirestore.getInstance().collection("ultimos-mensajes")
                                        .document(paraID)
                                        .collection("contactos")
                                        .document(deID)
                                        .set(contacto);
                                }
                            });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Teste", e.getMessage(), e);
                    }
                });
        }
    }

    private void setUpMensajes(ArrayList<Mensaje> mensajes) {
        if (mensajes != null) {
            chatAdapter = new ChatAdapter(mensajes);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rvChatDetalle.setLayoutManager(mLayoutManager);
            rvChatDetalle.setAdapter(chatAdapter);
            if (rvChatDetalle.getAdapter().getItemCount() > 0) {
                rvChatDetalle.smoothScrollToPosition(rvChatDetalle.getAdapter().getItemCount() - 1);
            }
        }
    }

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);

        //        toolbar.setNavigationIcon(R.drawable.b);

        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP);
        }

        if (contacto != null) {
            toolbar.setTitle(contacto.getUsuario_nombre());
            //toolbar.setLogo(Utils contacto.getFotoUrl());
        } else if(proveedor != null) {
            toolbar.setTitle(proveedor.getProveedor_nombre());
        } else {
            toolbar.setTitle(usuario_nombre);
        }


        try {
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        } catch (Exception e) {
            Log.e("TEAMPS", "Can't set color.");
        }

        try {
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            Log.e("TEAMPS", "Error in set support action bar.");
        }

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e("TEAMPS", "Error in set display home as up enabled.");
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.iv_enviar_mensaje:
                enviarMensaje();
                break;
            case R.id.iv_agendar:
                if (isCliente) {

                } else {
                    Intent intent = new Intent(this, CitaActivity.class);
                    intent.putExtra("paraID", paraID);
                    intent.putExtra("proveedor_uid", proveedor_uid);
                    startActivity(intent);
                    break;
                }
        }
    }
}
