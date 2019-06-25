package com.example.contactame.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.contactame.R;
import com.example.contactame.models.Contacto;
import com.example.contactame.models.Mensaje;
import com.example.contactame.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ENVIA = 0;
    private final int RECIBE = 1;
    private final int HORA = 2;

    private ArrayList<Mensaje> mensajes;
    private Contacto contacto;
    private String uid;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public ChatAdapter(ArrayList<Mensaje> mensajes) {
        this.mensajes = mensajes;
        //        this.uid = uid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == ENVIA) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_right, viewGroup, false);
            return new ChatEnviaViewHolder(itemView);
        } else if (i == RECIBE) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_left, viewGroup,
                false);
            return new ChatRecibeViewHolder(itemView);
        } /*else {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_chat_general_chat2_time, parent, false);
return new ChatDetailTimeViewHolder(itemView);
        }*/

        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (mensajes.get(i).getDeID().equals(mAuth.getCurrentUser().getUid())) {
            if (viewHolder instanceof ChatEnviaViewHolder) {

                ChatEnviaViewHolder enviaViewHolder = (ChatEnviaViewHolder) viewHolder;
                Mensaje mensaje = mensajes.get(i);
                Context context = enviaViewHolder.profileImageView.getContext();

//                                int imageId = Utils.getDrawableInt(context, mensaje.getProfileImage());
                //                int sentImageId = Utils.getDrawableInt(context, "baseline_done_black_24");
                //                int readImageId = Utils.getDrawableInt(context, "baseline_done_all_blue_24");

//                Utils.setCircleImageToImageViewFromFirebase(context, enviaViewHolder.profileImageView, contacto.getFotoUrl(), 0,
//                    0);


                DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
                final String horaChat = df.format(mensaje.getTimestamp());

                enviaViewHolder.messageTextView.setText(mensaje.getMensaje());
                enviaViewHolder.timeTextView.setText(horaChat);

                //                if (mensaje.getState().equals("enviado")) {
                //                    Utils.setImageToImageView(context, enviaViewHolder.stateImageView, sentImageId);
                //                } else if (mensaje.getState().equals("leido")) {
                //                    Utils.setImageToImageView(context, enviaViewHolder.stateImageView, readImageId);
                //                }
            }

        } else if (mensajes.get(i).getParaID().equals(mAuth.getCurrentUser().getUid())) {
            if (viewHolder instanceof ChatRecibeViewHolder) {

                ChatRecibeViewHolder recibeViewHolder = (ChatRecibeViewHolder) viewHolder;
                Mensaje mensaje = mensajes.get(i);
                Context context = recibeViewHolder.profileImageView.getContext();

                DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
                final String horaChat = df.format(mensaje.getTimestamp());

                //                int imageId = Utils.getDrawableInt(context, chatDetails.getProfileImage());

                //                Utils.setCircleImageToImageView(context, enviaViewHolder.profileImageView, imageId, 0, 0);
                recibeViewHolder.messageTextView.setText(mensaje.getMensaje());
                recibeViewHolder.timeTextView.setText(horaChat);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public class ChatEnviaViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView messageTextView;
        TextView timeTextView;
        ImageView stateImageView;

        public ChatEnviaViewHolder(View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileImageView);
            messageTextView = itemView.findViewById(R.id.tv_mensaje);
            timeTextView = itemView.findViewById(R.id.tv_timestamp);
            //            stateImageView = itemView.findViewById(R.id.stateImageView);
        }
    }

    public class ChatRecibeViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView messageTextView;
        TextView timeTextView;

        public ChatRecibeViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            messageTextView = itemView.findViewById(R.id.tv_mensaje);
            timeTextView = itemView.findViewById(R.id.tv_timestamp);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mensajes.get(position).getDeID().equals(mAuth.getCurrentUser().getUid())) return ENVIA;
        else if (mensajes.get(position).getParaID().equals(mAuth.getCurrentUser().getUid())) return RECIBE;
        else return HORA;
    }
}
