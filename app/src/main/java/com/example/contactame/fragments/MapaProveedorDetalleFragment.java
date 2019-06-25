package com.example.contactame.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.contactame.R;
import com.example.contactame.activities.ProveedorMostrarPerfilClienteActivity;
import com.example.contactame.models.Proveedor;
import com.example.contactame.utils.Utils;
import org.w3c.dom.Text;

public class MapaProveedorDetalleFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private View view;
    private ImageView ivFotoProveedor;
    private TextView tvNombreProveedor;
    private TextView tvCalificacionProveedor;
    private TextView tvCalificacionCantidad;
    private Button btVerPerfil;
    private Proveedor proveedor;

    public MapaProveedorDetalleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mapa_proveedor_detalle, container, false);

        initComponents(view);

        proveedor = null;
        if (getArguments() != null) {
            proveedor = getArguments().getParcelable("proveedor");
            if (proveedor != null) {
                mostrarProveedor(proveedor);
            }
        }
        return view;

    }

    private void initComponents(View view) {
        ivFotoProveedor = view.findViewById(R.id.iv_foto_proveedor);
        tvNombreProveedor = view.findViewById(R.id.tv_nombre_proveedor);
        tvCalificacionProveedor = view.findViewById(R.id.tv_calificacion_promedio);
        tvCalificacionCantidad = view.findViewById(R.id.tv_calificacion_cantidad);

        btVerPerfil = view.findViewById(R.id.bt_ver_perfil);

        btVerPerfil.setOnClickListener(this);
    }

    @SuppressLint("DefaultLocale")
    public void mostrarProveedor(Proveedor proveedor) {
        tvNombreProveedor.setText(proveedor.getProveedor_nombre());
        tvCalificacionProveedor.setText(String.format("%.02f", proveedor.getProveedor_promedioCalificacion()));
        tvCalificacionCantidad.setText(String.format("en %s calificaciones",
            proveedor.getProveedor_cantidadCalificacion()));
        Utils.setImageToImageView(getContext(), ivFotoProveedor, R.drawable.baseline_person_outline_black_24);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.bt_ver_perfil:
                Intent intentProveedor = new Intent(getContext(), ProveedorMostrarPerfilClienteActivity.class);
                intentProveedor.putExtra("proveedor", proveedor);
                startActivity(intentProveedor);
                break;
        }
    }
}
