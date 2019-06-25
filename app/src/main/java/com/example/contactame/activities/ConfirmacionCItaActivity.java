package com.example.contactame.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.example.contactame.R;
import com.example.contactame.utils.Utils;

public class ConfirmacionCItaActivity extends AppCompatActivity {

    private ImageView ivQr;

    private String qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_cita);

        qr = getIntent().getStringExtra("qr");

        initView();
        initUI();
    }

    private void initView() {
        ivQr = findViewById(R.id.iv_qr);
    }

    private void initUI() {
        Utils.setImageToImageViewFromFirebase(this, ivQr, qr);
    }
}
