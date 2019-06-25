package com.example.contactame.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import com.example.contactame.R;
import com.example.contactame.adapter.AdapterGridCitas;
import com.example.contactame.models.Cita;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class CitasCanceladasFragment extends Fragment {

    private AdapterGridCitas adapterGridCitas;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private String deID;

    public CitasCanceladasFragment() {
        // Required empty public constructor
    }

    public static CitasCanceladasFragment newInstance() {
        CitasCanceladasFragment fragment = new CitasCanceladasFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas_canceladas, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        deID = firebaseAuth.getCurrentUser().getUid();
        if (deID != null) {
            getCitas();
        }
        initComponents(view);
        //initActions();
        return view;
    }

    private void getCitas() {
        final List<Cita> citas = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("citas")
            .whereEqualTo("deID", deID)
            .whereEqualTo("estado", "Cancelado")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                    for (DocumentChange documentChange : documentChanges) {
                        Cita cita = documentChange.getDocument().toObject(Cita.class);
                        citas.add(cita);
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

        //            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        //                @Override
        //                public void onComplete(@NonNull Task<QuerySnapshot> task) {
        //                    if (task.isSuccessful()) {
        //                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
        //                            Cita cita = documentSnapshot.toObject(Cita.class);
        //                            Log.d("CitaHOY", cita.getFecha().toString());
        //                            citas.add(cita);
        //                        }
        //                        Collections.sort(citas, new Comparator<Cita>() {
        //                            @Override
        //                            public int compare(Cita o1, Cita o2) {
        //                                return o1.getHora().compareTo(o2.getHora());
        //                            }
        //                        });
        //                        initData(citas);
        //                    }
        //                }
        //            });
    }

    private void initComponents(View view) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.videoRecyclerView);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void initData(List<Cita> citas) {
        adapterGridCitas = new AdapterGridCitas(citas);
        recyclerView.setAdapter(adapterGridCitas);
    }

    private void initActions() {
        adapterGridCitas.setOnItemClickListener(new AdapterGridCitas.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Cita cita, int position) {
                Toast.makeText(getContext(), "Selected : " + cita.getHora(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
