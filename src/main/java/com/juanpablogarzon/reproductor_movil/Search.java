package com.juanpablogarzon.reproductor_movil;
import com.juanpablogarzon.reproductor_movil.utils.MetodosCompartidos;

import android.app.Activity;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

public class Search extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int idXML = getResources().getIdentifier("activity_searchmusic","layout", getPackageName());
        setContentView(idXML);

        //Manejo de EditText
        EditText inputBar = findViewById(getResources().getIdentifier("inputBar","id", getPackageName()));
        inputBar.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence string, int start, int count, int after) {}// Acción antes de que el texto cambie
            @Override
            public void onTextChanged(CharSequence string, int start, int before, int count) { //Accion a cada cambio de texto
                LinearLayout layout_Canciones = (findViewById(getResources().getIdentifier("layout_Songs", "id", getPackageName())));
                layout_Canciones.removeAllViews();
                MetodosCompartidos.vistaCompartida(Search.this, 0, string.toString());
            }
            @Override
            public void afterTextChanged(Editable string) {}// Acción después de que el texto cambie
        });

        //Salir del Layout
        ImageButton btnRetun = findViewById(getResources().getIdentifier("btn_return_to_Search","id", getPackageName()));
        btnRetun.setOnClickListener(v -> finish());
    }
}