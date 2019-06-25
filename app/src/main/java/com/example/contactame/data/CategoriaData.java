package com.example.contactame.data;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.content.res.AppCompatResources;
import com.example.contactame.R;
import com.example.contactame.models.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaData {

    public static List<Categoria> getCategoria(Context ctx) {
        List<Categoria> items = new ArrayList<>();
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.categoria_icon);
        TypedArray drw_arr_bg = ctx.getResources().obtainTypedArray(R.array.categoria_foto);
        String title_arr[] = ctx.getResources().getStringArray(R.array.categoria_descripcion);
        String brief_arr[] = ctx.getResources().getStringArray(R.array.shop_category_brief);
        for (int i = 0; i < drw_arr.length(); i++) {
            Categoria obj = new Categoria();
            obj.setImage(drw_arr.getResourceId(i, -1));
            obj.setImage_bg(drw_arr_bg.getResourceId(i, -1));
            obj.setDescripcion(title_arr[i]);
            obj.setDescripcionCorta(brief_arr[i]);
            obj.setImageDrw(AppCompatResources.getDrawable(ctx, obj.getImage()));
            items.add(obj);
        }
        return items;
    }
}
