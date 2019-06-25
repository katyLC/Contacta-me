package com.example.contactame.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.models.Cita;
import com.example.contactame.utils.ItemTouchHelperAdapter;
import com.example.contactame.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdapterGridCitas extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    private List<Cita> citas;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Cita cita, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public AdapterGridCitas(List<Cita> citas) {
        this.citas = citas;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        citas.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 0) {
            View itemView =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cita_pendiente, viewGroup, false);
            return new PendienteViewHolder(itemView);
        } else if (i == 1) {
            View itemView =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cita_proceso, viewGroup, false);
            return new ProcesoViewHolder(itemView);
        } else if (i == 2) {
            View itemView =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cita_pendiente, viewGroup, false);
            return new TerminadaViewHolder(itemView);
        } else {
            View itemView =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cita_pendiente, viewGroup, false);
            return new CanceladoViewHolder(itemView);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof PendienteViewHolder) {
            PendienteViewHolder holder = (PendienteViewHolder) viewHolder;

            Cita cita = citas.get(position);

            holder.tvNombre.setText(cita.getUsuario_nombre());
            holder.tvHora.setText(Utils.dateToHoursMinutes(cita.getHora()));

            long restante = cita.getHora().getTime() - Utils.getCurrentDateNow().getTime();

            String formatMinutes = "%01dmin";
            String formatHour = "%01dh%02dmin";
            String hms;

            if ( restante > 0) {
                if (TimeUnit.MILLISECONDS.toHours(restante) > 0) {
                    hms = String.format(formatHour, TimeUnit.MILLISECONDS.toHours(restante),
                        TimeUnit.MILLISECONDS.toMinutes(restante) % TimeUnit.HOURS.toMinutes(1));
                } else {
                    hms = String.format(formatMinutes,
                        TimeUnit.MILLISECONDS.toMinutes(restante) % TimeUnit.HOURS.toMinutes(1));
                }
            } else {
                hms = "";
            }

            holder.tvTiempoRestante.setText(hms);

            holder.colorImageView.setColorFilter(ContextCompat.getColor(holder.colorImageView.getContext(),
                R.color.md_green_500));

            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, citas.get(position), position);
                    }
                }
            });
        } else if (viewHolder instanceof ProcesoViewHolder) {

            Cita cita = citas.get(position);

            ProcesoViewHolder holder = (ProcesoViewHolder) viewHolder;
            holder.tvNombre.setText(cita.getUsuario_nombre());
            holder.tvHora.setText(Utils.dateToHoursMinutes(cita.getHora()));

            long restante = cita.getHora().getTime() - Utils.getCurrentDateNow().getTime();

            String hms = "";
            if (restante > 0) {

                String formatMinutes = "%01dmin";
                String formatHour = "%01dh%02dmin";

                if (TimeUnit.MILLISECONDS.toHours(restante) > 0) {
                    hms = String.format(formatHour, TimeUnit.MILLISECONDS.toHours(restante),
                        TimeUnit.MILLISECONDS.toMinutes(restante) % TimeUnit.HOURS.toMinutes(1));
                } else {
                    hms = String.format(formatMinutes,
                        TimeUnit.MILLISECONDS.toMinutes(restante) % TimeUnit.HOURS.toMinutes(1));
                }

            } else {
                hms = "En proceso";
            }

            holder.tvTiempoRestante.setText(hms);

            holder.lineView.setBackgroundColor(ContextCompat.getColor(holder.lineView.getContext(),
                R.color.colorPrimary));

            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, citas.get(position), position);
                    }
                }
            });
        } else if (viewHolder instanceof TerminadaViewHolder) {

            Cita cita = citas.get(position);

            TerminadaViewHolder holder = (TerminadaViewHolder) viewHolder;
            holder.tvNombre.setText(cita.getUsuario_nombre());
            holder.tvHora.setText("Realizada el: " + Utils.getFullDate(cita.getFecha()));
            holder.tvTiempoRestante.setText("---");

            holder.colorImageView.setColorFilter(ContextCompat.getColor(holder.colorImageView.getContext(), R.color.md_grey_500));

            if (itemClickListener != null) {
                holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClick(v, citas.get(position), position);
                    }
                });
            }
        } else {
            Cita cita = citas.get(position);

            CanceladoViewHolder holder = (CanceladoViewHolder) viewHolder;
            holder.tvNombre.setText(cita.getUsuario_nombre());
            holder.tvHora.setText("Cita cancelada");
            holder.tvTiempoRestante.setText("---");

            holder.colorImageView.setColorFilter(ContextCompat.getColor(holder.colorImageView.getContext(),
                R.color.md_red_500));

            if (itemClickListener != null) {
                holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClick(v, citas.get(position), position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (citas.get(position).getEstado().equals("Pendiente")) {
            return 0;
        } else if (citas.get(position).getEstado().equals("Proceso")) {
            return 1;
        } else if (citas.get(position).getEstado().equals("Terminado")) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    public class PendienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvHora, tvTiempoRestante;
        ImageView colorImageView;
        ConstraintLayout constraintLayout;

        PendienteViewHolder(View view) {
            super(view);

            tvNombre = view.findViewById(R.id.tv_nombre);
            tvHora = view.findViewById(R.id.tv_hora);
            tvTiempoRestante = view.findViewById(R.id.tv_tiempo_restante);
            colorImageView = view.findViewById(R.id.colorImageView);
            constraintLayout = view.findViewById(R.id.constraintLayout);
        }

    }

    public class ProcesoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvHora, tvTiempoRestante;
        View lineView;
        ConstraintLayout constraintLayout;

        ProcesoViewHolder(View view) {
            super(view);

            tvNombre = view.findViewById(R.id.tv_nombre);
            tvHora = view.findViewById(R.id.tv_hora);
            tvTiempoRestante = view.findViewById(R.id.tv_tiempo_restante);
            lineView = view.findViewById(R.id.lineView);
            constraintLayout = view.findViewById(R.id.constraintLayout);

        }
    }

    public class TerminadaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvHora, tvTiempoRestante;
        ImageView colorImageView;
        ConstraintLayout constraintLayout;

        TerminadaViewHolder(View view) {
            super(view);

            tvNombre = view.findViewById(R.id.tv_nombre);
            tvHora = view.findViewById(R.id.tv_hora);
            tvTiempoRestante = view.findViewById(R.id.tv_tiempo_restante);
            colorImageView = view.findViewById(R.id.colorImageView);
            constraintLayout = view.findViewById(R.id.constraintLayout);

        }
    }

    public class CanceladoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvHora, tvTiempoRestante;
        ImageView colorImageView;
        ConstraintLayout constraintLayout;

        CanceladoViewHolder(View view) {
            super(view);

            tvNombre = view.findViewById(R.id.tv_nombre);
            tvHora = view.findViewById(R.id.tv_hora);
            tvTiempoRestante = view.findViewById(R.id.tv_tiempo_restante);
            colorImageView = view.findViewById(R.id.colorImageView);
            constraintLayout = view.findViewById(R.id.constraintLayout);

        }
    }

    public void removeItem(int position) {
        citas.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Cita item, int position) {
        citas.add(position, item);
        notifyItemInserted(position);
    }

}
