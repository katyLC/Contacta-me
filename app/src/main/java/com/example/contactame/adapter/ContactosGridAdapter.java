package com.example.contactame.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.contactame.R;
import com.example.contactame.models.Contacto;
import com.example.contactame.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ContactosGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Contacto> contactos;
    private OnItemClickListener itemClickListener;


    public interface OnItemClickListener {
        void onItemClick(View view, Contacto contacto, int position);

        //void onDeleteClick(View view, Chat obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public ContactosGridAdapter(Context context, ArrayList<Contacto> contactos) {
        this.context = context;
        this.contactos = contactos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto_card, parent, false);

        return new ContactoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ContactoViewHolder) {

            Contacto contacto = contactos.get(position);

            ContactoViewHolder holder = (ContactoViewHolder) viewHolder;
            holder.tvUsuarioNombre.setText(contacto.getUsuario_nombre());

            Context context = holder.holderCardView.getContext();
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red_300));
            holder.cardView.setVisibility(View.INVISIBLE);

//            int id = Utils.getDrawableInt(context, contacto.getFotoUrl());
            Utils.setImageToImageViewFromFirebase(context, holder.ivUsuarioFoto, contacto.getFotoUrl());
//            Utils.setCircleImageToImageViewFromFirebase(context, holder.ivUsuarioFoto, contacto.getFotoUrl(), 0,0);

            DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
            final String horaChat = df.format(contacto.getTimeStamp());
            holder.tvTimestamp.setText(horaChat);
            //            int number = Integer.parseInt(contacto.Count);

//            holder.Count.setText(contacto.Count);
//            if(number > 0)
//            {
//                holder.Time.setTextColor(ContextCompat.getColor(context,R.color.md_orange_A700));
//                holder.Count.setVisibility(View.VISIBLE);
//                holder.card.setVisibility(View.VISIBLE);
//
//                if (number > 9 )
//                    holder.Count.setText("9+");
//            }

            holder.tvMensaje.setText(contacto.getUltimoMensaje());

            if (itemClickListener != null) {
                holder.holderCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(v, contactos.get(position), position);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public class ContactoViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout holderCardView;
        private ImageView ivUsuarioFoto;
        private TextView tvUsuarioNombre;
        private CardView cardView;
        private TextView messagecount;
        private TextView tvTimestamp;
        private TextView tvMensaje;
        private View view81;

        ContactoViewHolder(View view) {
            super(view);

            holderCardView = view.findViewById(R.id.holderCardView);
            ivUsuarioFoto = view.findViewById(R.id.iv_usuario_foto);
            tvUsuarioNombre = view.findViewById(R.id.tv_usuario_nombre);
            cardView = view.findViewById(R.id.card_view);
            messagecount = view.findViewById(R.id.messagecount);
            tvTimestamp = view.findViewById(R.id.tv_timestamp);
            tvMensaje = view.findViewById(R.id.tv_mensaje);
            view81 = view.findViewById(R.id.view81);

        }
    }
}
