package com.example.contactame.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.contactame.R;
import com.example.contactame.adapter.FavoritosGridAdapter;
import com.example.contactame.models.Proveedor;
import com.example.contactame.models.Servicio;
import com.example.contactame.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Map;

public class FavoritosFragment extends Fragment {

    private View view;
    private ArrayList<Usuario> usuarios;
    private ArrayList<Servicio> servicios;
    private ArrayList<Proveedor> proveedores;
    private FirebaseFirestore mFirestore;
    private FavoritosGridAdapter mAdapter;
    private RecyclerView ivRecyclerFavorito;

    int numeroColumnas = 2;

    public FavoritosFragment() {
        // Required empty public constructor
    }

    public static FavoritosFragment newInstance() {
        FavoritosFragment fragment = new FavoritosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        initComponent(view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            getFavoritos(uid);
        }

    }

    private void initComponent(View view) {

        ivRecyclerFavorito = view.findViewById(R.id.rv_item_favorito);
        ivRecyclerFavorito.setLayoutManager(new GridLayoutManager(getContext(), numeroColumnas));
        ivRecyclerFavorito.setItemAnimator(new DefaultItemAnimator());

    }

    private void updateUI(ArrayList<Proveedor> proveedores) {
        mAdapter = new FavoritosGridAdapter(proveedores);
        ivRecyclerFavorito.setAdapter(mAdapter);
    }

    private void getFavoritos(String uid) {
        DocumentReference docFavoritos = mFirestore.collection("favoritos").document(uid);
        final ArrayList<String> uidList = new ArrayList<>();
        docFavoritos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Map<String, Object> map = task.getResult().getData();
                        for (Map.Entry<String, Object> resultado : map.entrySet()) {
                            uidList.add(resultado.getKey());
                            getServicios(uidList);
                        }
                    }
                }
            }
        });
    }

    private void getServicios(final ArrayList<String> uidList) {
        final ArrayList<Servicio> servicios = new ArrayList<>();
        final ArrayList<Proveedor> proveedores = new ArrayList<>();

        //        for (String uid : uidList) {
        //            DocumentReference docServicios = mFirestore.collection("servicios").document(uid);
        //                docServicios.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        //                    @Override
        //                    public void onSuccess(DocumentSnapshot documentSnapshot) {
        //                        Servicio servicio = documentSnapshot.toObject(Servicio.class);
        //                        if (servicio != null) {
        //                            Proveedor proveedor = new Proveedor();
        //                            proveedor.setServicio_uid(servicio.getServicio_uid());
        //                            proveedor.setUsuario_uid(servicio.getUsuario_uid());
        //                            proveedor.setServicio_categoria(servicio.getCategoria());
        //                            proveedor.setServicio_ciudad(servicio.getCiudad());
        //                            proveedor.setServicio_descripcion(servicio.getDescripcion());
        //                            proveedor.setServicio_horarioAtencion(servicio.getHorarioAtencion());
        //                            proveedor.setServicio_ubicacion(servicio.getUbicacion());
        //                            proveedores.add(proveedor);
        //                        }
        //                        getUsuarios(proveedores);
        //                    }
        //                });
        //        }
        mFirestore.collection("servicios")
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Servicio> servicios = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        for (String uid : uidList) {
                            if (document.getId().equals(uid)) {
                                Servicio servicio = document.toObject(Servicio.class);
                                Proveedor proveedor = new Proveedor();
                                proveedor.setServicio_uid(servicio.getServicio_uid());
                                proveedor.setUsuario_uid(servicio.getUsuario_uid());
                                proveedor.setServicio_categoria(servicio.getCategoria());
                                proveedor.setServicio_ciudad(servicio.getCiudad());
                                proveedor.setServicio_descripcion(servicio.getDescripcion());
                                proveedor.setServicio_horarioAtencion(servicio.getHorarioAtencion());
                                proveedor.setServicio_ubicacion(servicio.getUbicacion());
                                proveedores.add(proveedor);
                            }
                        }
                    }
                    getUsuarios(proveedores);
                }
            }
        });

    }

    private void getUsuarios(final ArrayList<Proveedor> proveedores) {
        for (final Proveedor proveedor : proveedores) {
            DocumentReference docUsuarios = mFirestore.collection("usuarios").document(proveedor.getUsuario_uid());
            docUsuarios.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    if (usuario != null) {
                        for (Proveedor proveedor : proveedores) {
                            if (usuario.getServicio_uid().equals(proveedor.getServicio_uid())) {
                                proveedor.setProveedor_dni(usuario.getDni());
                                proveedor.setProveedor_email(usuario.getEmail());
                                proveedor.setProveedor_nombre(String.format("%s %s", usuario.getNombres(), usuario.getApellidos()));
                                break;
                            }
                        }
                    }
                    updateUI(proveedores);
                }
            });
        }
    }


}
