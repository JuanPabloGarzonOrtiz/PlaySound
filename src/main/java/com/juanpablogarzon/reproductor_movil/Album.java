package com.juanpablogarzon.reproductor_movil;
import com.juanpablogarzon.reproductor_movil.utils.MetodosCompartidos;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.content.Intent;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup;

import android.util.Log;

public class Album extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int idXML = getResources().getIdentifier("activity_album", "layout", getPackageName());
        setContentView(idXML);

        ImageButton btnRetun = findViewById(getResources().getIdentifier("btn_return_to_Album","id", getPackageName()));
        btnRetun.setOnClickListener(v -> finish());

        //Asginacion de Datos de Album
        MetodosCompartidos.vistaCompartida(this,3,"album_Artista"); //El numero es la Posicion del Dato del Album en Consulta a la DB Android
    }
}