package com.example.contactame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.util.Util;
import com.example.contactame.R;
import com.example.contactame.models.Proveedor;
import com.example.contactame.utils.Utils;

import java.util.ArrayList;

public class AdapterGridProveedores extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Proveedor> proveedores;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Proveedor proveedor, int position);
    }

    public void setItemClickListener(final OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public AdapterGridProveedores(Context context, ArrayList<Proveedor> proveedores) {
        this.proveedores = proveedores;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProveedorFoto, more;
        TextView nombre;
        TextView ciudad;
        TextView descripcion;
        View lyt_parent;
        RatingBar rbCalificacion;
        TextView tvPromedioCalificacion;
        TextView tvCantidadCalificacion;

        OriginalViewHolder(View v) {
            super(v);
            ivProveedorFoto = itemView.findViewById(R.id.iv_proveedor_foto);
            nombre = itemView.findViewById(R.id.tv_nombre_proveedor);
            descripcion = itemView.findViewById(R.id.tv_proveedor_descripcion);
            ciudad = itemView.findViewById(R.id.tv_servicio_ciudad);
            lyt_parent = itemView.findViewById(R.id.placeHolderCardView);

            rbCalificacion = itemView.findViewById(R.id.rb_calificacion);
            tvPromedioCalificacion = itemView.findViewById(R.id.tv_promedio_calificacion);
            tvCantidadCalificacion = itemView.findViewById(R.id.tv_cantidad_calificacion);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder vh;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_proveedor_card, viewGroup,
            false);
        vh = new OriginalViewHolder(view);
        return vh;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {

        if (viewHolder instanceof  OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) viewHolder;

            Proveedor proveedor = proveedores.get(i);

            Utils.setImageToImageView(this.ctx, view.ivProveedorFoto, R.drawable.baseline_image_black_24);

            view.nombre.setText(proveedor.getProveedor_nombre());
            view.descripcion.setText(proveedor.getServicio_descripcion());
            view.ciudad.setText(proveedor.getServicio_ciudad());

            if (proveedor.getProveedor_cantidadCalificacion() > 0) {
                view.tvCantidadCalificacion.setText(String.valueOf(proveedor.getProveedor_cantidadCalificacion()));
                view.tvPromedioCalificacion.setText(String.format("%.02f", proveedor.getProveedor_promedioCalificacion()));
                view.rbCalificacion.setRating(proveedor.getProveedor_promedioCalificacion());
            } else {
                view.tvCantidadCalificacion.setText("0");
                view.tvPromedioCalificacion.setText("0");
            }

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, proveedores.get(i), i);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return proveedores.size();
    }
}
