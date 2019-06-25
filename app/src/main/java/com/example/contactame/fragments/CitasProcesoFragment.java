package com.example.contactame.fragments;


import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v13.view.DragStartHelper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.adapter.AdapterGridCitas;
import com.example.contactame.models.Cita;
import com.example.contactame.utils.OnStartDragListener;
import com.example.contactame.utils.RecyclerItemTouchHelper;
import com.example.contactame.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.core.OrderBy;

import javax.annotation.Nullable;
import java.util.*;

public class CitasProcesoFragment extends Fragment implements OnStartDragListener,
    RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    private ItemTouchHelper mItemTouchHelper;
    private AdapterGridCitas adapterGridCitas;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private String deID;

    CoordinatorLayout coordinatorLay;
    CardView cardView;
    private List<Cita> citas;

    public CitasProcesoFragment() {
        // Required empty public constructor
    }

    public static CitasProcesoFragment newInstance() {
        CitasProcesoFragment fragment = new CitasProcesoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas_proceso, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        deID = firebaseAuth.getCurrentUser().getUid();
        if (deID != null) {
            getCitas();
        }
        initComponents(view);


        cardView = view.findViewById(R.id.cardView41);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);

        return view;
    }

    private void getCitas() {
        final List<Cita> citas = new ArrayList<>();
        final Date fecha = Utils.getCurrentDate();

        FirebaseFirestore.getInstance().collection("citas")
            .whereEqualTo("deID", deID)
            .whereEqualTo("fecha", fecha)
            //            .whereGreaterThanOrEqualTo("fecha", fecha)
            //            .whereLessThanOrEqualTo("fecha", fecha)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                    for (DocumentChange documentChange : documentChanges) {
                        Cita cita = documentChange.getDocument().toObject(Cita.class);
                        switch (documentChange.getType()) {
                            case ADDED:
                                Log.d("CitaHOY", cita.getFecha().toString());
                                if (cita.getEstado().equals("Pendiente") || cita.getEstado().equals("Proceso")) {
                                    citas.add(cita);
                                }
                                break;
                            case MODIFIED:
                                citas.clear();
                                FirebaseFirestore.getInstance().collection("citas")
                                    .whereEqualTo("deID", deID)
                                    .whereEqualTo("fecha", fecha)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                    Cita cita = documentSnapshot.toObject(Cita.class);
                                                    Log.d("CitaHOY", cita.getFecha().toString());
                                                    if (cita.getEstado().equals("Pendiente") || cita.getEstado().equals("Proceso")) {
                                                        citas.add(cita);
                                                    }
                                                }
                                                Collections.sort(citas, new Comparator<Cita>() {
                                                    @Override
                                                    public int compare(Cita o1, Cita o2) {
                                                        return o1.getHora().compareTo(o2.getHora());
                                                    }
                                                });
                                                initData(citas);
                                            }
                                        }
                                    });
                                break;
                            case REMOVED:
                                Log.d("CitaHOY", cita.getFecha().toString());
                                if (cita.getEstado().equals("Pendiente") || cita.getEstado().equals("Proceso")) {
                                    citas.remove(cita);
                                }
                                break;
                        }
                    }
                    Collections.sort(citas, new Comparator<Cita>() {
                        @Override
                        public int compare(Cita o1, Cita o2) {
                            return o1.getHora().compareTo(o2.getHora());
                        }
                    });
                    initData(citas);
                }
            });
    }

    private void initComponents(View view) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.videoRecyclerView);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,
            (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT),
            this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    private void initData(List<Cita> citas) {
        this.citas = citas;
        adapterGridCitas = new AdapterGridCitas(citas);
        recyclerView.setAdapter(adapterGridCitas);

        adapterGridCitas.setOnItemClickListener(new AdapterGridCitas.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Cita cita, int position) {
                //                Toast.makeText(getContext(), "Selected : " + cita.getHora(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {

        if (viewHolder instanceof AdapterGridCitas.PendienteViewHolder) {
            final Cita deletedItem = citas.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            final String citaID = citas.get(position).getCitaID();
            if (direction == ItemTouchHelper.RIGHT) {
                String name = citas.get(viewHolder.getAdapterPosition()).getUsuario_nombre();

                FirebaseFirestore.getInstance()
                    .collection("citas")
                    .document(citaID)
                    .update("estado", "Cancelado")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                adapterGridCitas.removeItem(viewHolder.getAdapterPosition());

                Snackbar snackbar = Snackbar.make(cardView, name + " cita terminada!", Snackbar.LENGTH_LONG);
                snackbar.setAction("REGRESAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseFirestore.getInstance()
                            .collection("citas")
                            .document(citaID)
                            .update("estado", "Pendiente")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });

                        adapterGridCitas.restoreItem(deletedItem, deletedIndex);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(cardView, "No se puede realizar esta acción", Snackbar.LENGTH_LONG);
                adapterGridCitas.removeItem(viewHolder.getAdapterPosition());
                adapterGridCitas.restoreItem(deletedItem, deletedIndex);
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        } else if (viewHolder instanceof AdapterGridCitas.ProcesoViewHolder) {
            final Cita deletedItem = citas.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            final String citaID = citas.get(position).getCitaID();
            String name = citas.get(viewHolder.getAdapterPosition()).getUsuario_nombre();

            if (direction == ItemTouchHelper.LEFT) {

                FirebaseFirestore.getInstance()
                    .collection("citas")
                    .document(citaID)
                    .update("estado", "Terminado")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                adapterGridCitas.removeItem(viewHolder.getAdapterPosition());

                Snackbar snackbar = Snackbar.make(cardView, name + " cita terminada!", Snackbar.LENGTH_LONG);
                snackbar.setAction("REGRESAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore.getInstance()
                            .collection("citas")
                            .document(citaID)
                            .update("estado", "Proceso")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        adapterGridCitas.restoreItem(deletedItem, deletedIndex);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(cardView, "No se puede realizar esta acción", Snackbar.LENGTH_LONG);
                adapterGridCitas.removeItem(viewHolder.getAdapterPosition());
                adapterGridCitas.restoreItem(deletedItem, deletedIndex);
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }

}