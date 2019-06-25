package com.example.contactame.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.contactame.R;
import com.example.contactame.activities.ProveedoresListActivity;
import com.example.contactame.adapter.AdapterGridCategoria;
import com.example.contactame.data.CategoriaData;
import com.example.contactame.models.Categoria;
import com.example.contactame.utils.Utils;
import com.example.contactame.widget.SpacingItemDecoration;

import java.util.List;

public class CategoriaFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private AdapterGridCategoria mAdapter;

    public CategoriaFragment() {
        // Required empty public constructor
    }

    public static CategoriaFragment newInstance() {
        CategoriaFragment fragment = new CategoriaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_categoria, container, false);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Utils.dpToPx(getContext(), 8), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        List<Categoria> categorias = CategoriaData.getCategoria(getContext());

        mAdapter = new AdapterGridCategoria(getContext(), categorias);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterGridCategoria.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Categoria obj, int position) {
                Intent intent = new Intent(getContext(), ProveedoresListActivity.class);
                intent.putExtra("categoria", obj.getDescripcion());
                startActivity(intent);
            }
        });
    }
}
