package com.example.contactame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.contactame.R;
import com.example.contactame.models.Favorito;
import com.example.contactame.models.Proveedor;
import com.example.contactame.models.Servicio;
import com.example.contactame.models.Usuario;

import java.util.ArrayList;

public class FavoritosGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Favorito> favoritos;
    private ArrayList<Servicio> servicios;
    private ArrayList<Usuario> usuarios;
    private ArrayList<Proveedor> proveedores;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Proveedor proveedor, int position);

        void onAgregarEliminarFavoritoClick(View view, Favorito favorito, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public FavoritosGridAdapter(ArrayList<Proveedor> proveedores) {
        this.proveedores = proveedores;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorito_card, viewGroup, false);

        return new FavoritoViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof FavoritoViewHolder) {
            FavoritoViewHolder holder = (FavoritoViewHolder) viewHolder;

            Proveedor proveedor = proveedores.get(position);

            Context context = holder.holderCardView.getContext();

            holder.tvCalificacionServicio.setText(String.format("%.02f en Calificaciones", proveedor.getProveedor_promedioCalificacion()));
            holder.tvDescripcionServicio.setText(proveedor.getServicio_descripcion());
            holder.tvNombreProveedor.setText(proveedor.getProveedor_nombre());

            holder.ivFavorito.setImageResource(R.drawable.ic_favorite);

            if (itemClickListener != null) {
                holder.holderCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClick(v, proveedores.get(position), position);
                    }
                });
                holder.ivFavorito.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onAgregarEliminarFavoritoClick(v,
                            favoritos.get(position), position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return proveedores.size();
    }

    public class FavoritoViewHolder extends RecyclerView.ViewHolder {
        CardView holderCardView;

        TextView tvCalificacionServicio;
        TextView tvDescripcionServicio;
        TextView tvNombreProveedor;

        ImageView ivFotoProveedor;
        ImageView ivCalificacion;
        ImageView ivFavorito;


        FavoritoViewHolder(@NonNull View view) {
            super(view);

            holderCardView = view.findViewById(R.id.holderCardView);
            tvCalificacionServicio = view.findViewById(R.id.tv_calificacion_servicio);
            tvDescripcionServicio = view.findViewById(R.id.tv_descripcion_servicio);
            tvNombreProveedor = view.findViewById(R.id.tv_nombre_proveedor);
            ivFotoProveedor = view.findViewById(R.id.iv_foto_proveedor);
            ivCalificacion = view.findViewById(R.id.iv_calificacion);
            ivFavorito = view.findViewById(R.id.iv_favorito);
        }
    }
}
