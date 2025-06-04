package com.juanpablogarzon.reproductor_movil;
import com.juanpablogarzon.reproductor_movil.utils.MetodosCompartidos;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.os.Bundle;
import android.widget.ImageButton;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import android.util.Log;

public class List extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int idXML = getResources().getIdentifier("activity_list", "layout", getPackageName());
        setContentView(idXML);
        Intent  intent = getIntent();
        String name_List = intent.getStringExtra("NAME_LIST");

        ImageButton btn_Return = findViewById(getResources().getIdentifier("btn_return", "id", getPackageName()));
        btn_Return.setOnClickListener(v -> finish());

        if (!name_List.equals("Me Gusta")){
            LinearLayout layout = findViewById(getResources().getIdentifier("layoutTop", "id", getPackageName()));
            View view1 = (View) MetodosCompartidos.configuracion_XML_Elemento(this, "view", "", "");
            layout.addView(view1);
            ImageButton btn_delete = (ImageButton) MetodosCompartidos.configuracion_XML_Elemento(this, "imgButton", "@drawable/ic_delete", "layout_List");
            layout.addView(btn_delete);
            btn_delete.setOnClickListener(v -> {
                if (name_List != null){
                    SQLiteDatabase db = MetodosCompartidos.conectDB(this);
                    db.execSQL("DELETE FROM relaciones WHERE id_lista = (SELECT id_lista FROM listas WHERE nombre = ?)", new String[] {name_List});
                    db.execSQL("DELETE FROM listas WHERE nombre = ?", new String[] {name_List});
                    MetodosCompartidos.openActivity(this, Lists.class, -1);
                }        
            });
        }
        MetodosCompartidos.vistaCompartida(this, 0, "name_List");
    }
}