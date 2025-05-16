package com.juanpablogarzon.reproductor_movil;
import com.juanpablogarzon.reproductor_movil.utils.MetodosCompartidos;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.database.Cursor;
import android.view.Gravity;

import android.util.Log;

public class Artist extends Activity {
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int idXML = getResources().getIdentifier("activity_artist", "layout", getPackageName());
        setContentView(idXML);

        //Selecion de Boton Retorno a Home
        ImageButton btnHome = findViewById(getResources().getIdentifier("btn_home", "id", getPackageName()));
        btnHome.setOnClickListener(v -> finish());

        //Asginacion de Datos de Artista
        MetodosCompartidos.vistaCompartida(this,1, "name_Artist"); //El numero es la Posicion del Dato del Album en Consulta a la DB Android
    }
}