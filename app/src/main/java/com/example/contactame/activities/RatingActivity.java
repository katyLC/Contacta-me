package com.example.contactame.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.contactame.R;
import com.example.contactame.models.Calificacion;
import com.example.contactame.models.Proveedor;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private TextView tvDescripcionServicio;
    private RatingBar rbCalificacion;
    private EditText etTitulo;
    private EditText etDescripcion;
    private Button btEnviar;
    private TextView tvCalificacion;

    private String paraID;
    private String usuario_nombre;
    private String proveedor_uid;
    private int cantidadEstrellas;
    private float cantidadRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        paraID = getIntent().getStringExtra("deID");
        usuario_nombre = getIntent().getStringExtra("usuario_nombre");
        proveedor_uid = getIntent().getStringExtra("proveedor_uid");

        Log.d("usuario", paraID);
        Log.d("usuario", usuario_nombre);
        Log.d("usuario", proveedor_uid);

        initView();
        initData();
    }

    private void initView() {
        tvDescripcionServicio = findViewById(R.id.tv_descripcion_servicio);
        tvCalificacion = findViewById(R.id.tv_calificacion_promedio);
        rbCalificacion = findViewById(R.id.rb_calificacion);
        etTitulo = findViewById(R.id.et_titulo);
        etDescripcion = findViewById(R.id.et_descripcion);
        btEnviar = findViewById(R.id.bt_enviar);

        btEnviar.setOnClickListener(this);
        rbCalificacion.setOnRatingBarChangeListener(this);
    }

    private void initData() {
        tvDescripcionServicio.setText(String.format("Hola %s gracias por confiar en nosotros.\nPor favor califica " +
                "nuestro servicio.",
            usuario_nombre));
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_enviar) {
            agregarCalificacion();
        }
    }

    private Task<Void> agregarCalificacion() {

        final DocumentReference docReference = FirebaseFirestore.getInstance().collection("proveedores").document(proveedor_uid);

        final DocumentReference ratingReference = docReference.collection("ratings").document();
        return FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Proveedor proveedor = transaction.get(docReference).toObject(Proveedor.class);
                int nuevaCantidad = proveedor.getProveedor_cantidadCalificacion() + 1;
                float viejoRating =
                    proveedor.getProveedor_promedioCalificacion() * proveedor.getProveedor_cantidadCalificacion();
                float nuevoRating = (viejoRating + cantidadRating) / nuevaCantidad;

                proveedor.setProveedor_cantidadCalificacion(nuevaCantidad);
                proveedor.setProveedor_promedioCalificacion(nuevoRating);

                transaction.set(docReference, proveedor);

//                Map<String, Object> data = new HashMap<>();
//                data.put("rating", cantidadRating);

                Calificacion calificacion = new Calificacion();
                calificacion.setCalificacion(cantidadRating);
                calificacion.setTitulo(etTitulo.getText().toString());
                calificacion.setDescripcion(etDescripcion.getText().toString());

                transaction.set(ratingReference, calificacion, SetOptions.merge());
                return null;
            }
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        cantidadEstrellas = ratingBar.getNumStars();
        cantidadRating = ratingBar.getRating();
        tvCalificacion.setText(String.format("Calificación: %s/%d", cantidadRating, cantidadEstrellas));
    }
}
