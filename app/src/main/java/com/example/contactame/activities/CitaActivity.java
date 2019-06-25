package com.example.contactame.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.example.contactame.R;
import com.example.contactame.adapter.CustomSpinnerAdapter;
import com.example.contactame.models.Cita;
import com.example.contactame.models.Proveedor;
import com.example.contactame.models.Usuario;
import com.example.contactame.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CitaActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Calendar calendario = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener datePickerDialog = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, month);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendario.set(Calendar.HOUR_OF_DAY, 0);
            calendario.set(Calendar.MINUTE, 0);
            calendario.set(Calendar.SECOND, 0);
            actualizarFecha();
        }
    };

    TimePickerDialog.OnTimeSetListener timePickerDialog = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendario.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendario.set(Calendar.MINUTE, minute);
            actualizarHora();
        }
    };

    Spinner spAlarmaCita;
    ArrayList<String> horasAlarma;
    ArrayAdapter<String> adapter;

    String[] horas = {"10 min", "20 min", "30 min"};
    int[] minutos = {10, 20, 30};
    int[] icons = {R.drawable.ic_alarm_on_24};

    private TextView tvNombreProveedor;
    private TextView tvTipoServicio;
    private EditText edtObservacion;
    private EditText etFechaCita;
    private EditText etHoraCita;
    private TextView tvEmailCliente;
    private TextView tvDniCliente;
    private TextView tvNombreCliente;
    private Button btConfirmarCita;

    private Proveedor proveedor;
    private Cita cita = new Cita();
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage mStorage;

    private String paraID;
    private String qrUrl;
    private String proveedor_uid;
    public final static int QRcodeWidth = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita);
        initView();

        firebaseAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();

        paraID = getIntent().getStringExtra("paraID");
        proveedor_uid = getIntent().getStringExtra("proveedor_uid");

        horasAlarma = new ArrayList<>(Arrays.asList(horas));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, horasAlarma);

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), icons, horas);
        spAlarmaCita.setAdapter(customSpinnerAdapter);

        getProveedor();
        getCliente();
    }

    private void abrirCalendario() {
        new DatePickerDialog(this, datePickerDialog, calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void abrirTimer() {
        new TimePickerDialog(this, timePickerDialog, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE),
            true).show();
    }

    private void getProveedor() {
        FirebaseFirestore.getInstance().collection("proveedores")
            .whereEqualTo("usuario_uid", firebaseAuth.getCurrentUser().getUid())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Proveedor proveedor = new Proveedor();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (documentSnapshot != null) {
                                proveedor = documentSnapshot.toObject(Proveedor.class);
                            }
                        }
                        setDataProveedor(proveedor);
                    }
                }
            });
    }

    private void getCliente() {
        FirebaseFirestore.getInstance().collection("usuarios")
            .document(paraID)
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                        setDataCliente(usuario);
                    }
                }
            });
    }

    private void actualizarFecha() {
        String pattern = "dd-MM-yyyy hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
        String shortTimeStr = sdf.format(calendario.getTime());
        etFechaCita.setText(shortTimeStr);
        cita.setFecha(Utils.getCurrentDate());
    }

    private void actualizarHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa", Locale.US);
        String shortTimeStr = sdf.format(calendario.getTime());
        etHoraCita.setText(shortTimeStr);
        cita.setHora(calendario.getTime());
    }

    private void initView() {
        tvNombreProveedor = findViewById(R.id.tv_nombre_proveedor);
        tvTipoServicio = findViewById(R.id.tv_tipo_servicio);
        edtObservacion = findViewById(R.id.edt_observacion);
        etFechaCita = findViewById(R.id.et_fecha_cita);
        etHoraCita = findViewById(R.id.et_hora_cita);
        spAlarmaCita = findViewById(R.id.sp_alarma_cita);
        tvEmailCliente = findViewById(R.id.tv_email_cliente);
        tvDniCliente = findViewById(R.id.tv_dni_cliente);
        tvNombreCliente = findViewById(R.id.tv_nombre_cliente);
        btConfirmarCita = findViewById(R.id.bt_confirmar_cita);

        etFechaCita.setOnClickListener(this);
        etHoraCita.setOnClickListener(this);
        btConfirmarCita.setOnClickListener(this);

        spAlarmaCita.setOnItemSelectedListener(this);
    }

    private void setDataProveedor(Proveedor proveedor) {
        if (proveedor != null) {
            tvTipoServicio.setText(proveedor.getServicio_categoria());
            tvNombreProveedor.setText(proveedor.getProveedor_nombre());
        }
    }

    private void setDataCliente(Usuario usuario) {
        if (usuario != null) {
            tvDniCliente.setText(usuario.getDni());
            tvEmailCliente.setText(usuario.getEmail());
            tvNombreCliente.setText(String.format("%s %s", usuario.getNombres(), usuario.getApellidos()));
            cita.setUsuario_nombre(String.format("%s %s", usuario.getNombres(), usuario.getApellidos()));
        }
    }

    private Cita getData() {
        cita.setDeID(firebaseAuth.getCurrentUser().getUid());
        cita.setParaID(paraID);
        cita.setEstado("Pendiente");
        cita.setProveedor_uid(proveedor_uid);
        return cita;
    }

    private void guardarCita() {

        final Cita cita = getData();
        FirebaseFirestore.getInstance().collection("citas")
            .add(cita)
            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {

                    String citaID = task.getResult().getId();

                    FirebaseFirestore.getInstance().collection("citas")
                        .document(citaID)
                        .update("citaID", citaID)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                    Toast.makeText(CitaActivity.this, "Cita creada satisfactoriamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ConfirmacionCItaActivity.class);
                    intent.putExtra("qr", cita.getQrUrl());
                    startActivity(intent);
                }
            });
    }

    private void generarQr() {
        Bitmap bitmap;
        try {
            bitmap = TextToImageEncode(paraID);
            encodeBitmapGuardarFirebase(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                Value,
                BarcodeFormat.DATA_MATRIX.QR_CODE,
                QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                    getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 300, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private void calcularHoraNotificacion(int minutos) {
        long milisegundos = TimeUnit.MINUTES.toMillis(minutos);
        long horaNotificacion;
        long horaCita = 0;
        if (cita.getHora() != null) {
            horaCita = cita.getHora().getTime();
            horaNotificacion = horaCita - milisegundos;
            cita.setNotificacion(new Date(horaNotificacion));
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
                fotosRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri qrUrl = uri;
                        cita.setQrUrl(qrUrl.toString());
                        guardarCita();

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

    private void getQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.et_fecha_cita:
                abrirCalendario();
                break;
            case R.id.et_hora_cita:
                abrirTimer();
                break;
            case R.id.bt_confirmar_cita:
                generarQr();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int item = minutos[position];
        calcularHoraNotificacion(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
