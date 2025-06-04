package com.juanpablogarzon.reproductor_movil;
import com.juanpablogarzon.reproductor_movil.utils.MetodosCompartidos;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import java.io.File;

import android.content.res.Resources;
import android.view.ViewGroup;



import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

import android.util.Log;



public class Lists extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        SQLiteDatabase db = MetodosCompartidos.conectDB(this);
        super.onCreate(savedInstanceState);
        int idXML = getResources().getIdentifier("activity_list_reproduction", "layout", getPackageName());
        setContentView(idXML);
        MetodosCompartidos.navigation_Bar(this);

        //Añadir Listas de Reproducción
        ImageButton btn_CreateList = findViewById(getResources().getIdentifier("btnCreateList", "id", getPackageName()));
        btn_CreateList.setOnClickListener(v ->{
            FrameLayout layout = findViewById(getResources().getIdentifier("Layoutinterno", "id", getPackageName()));
            LinearLayout layout_Create = new LinearLayout(this);
            int size = ViewGroup.LayoutParams.WRAP_CONTENT; // Ancho de Pantalla de Dispositivo
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.gravity = Gravity.CENTER;
            layout_Create.setLayoutParams(params);
            layout_Create.setPadding(10,10,10,10);
            layout_Create.setOrientation(LinearLayout.VERTICAL);
            layout_Create.setBackgroundColor(Color.parseColor("#F0000000"));

            TextView title = (TextView) MetodosCompartidos.configuracion_XML_Elemento(this, "txtView", "Crear Lista de Reproducción", "cancion_Artist");
            layout_Create.addView(title);
            
            EditText inputNameList = (EditText) MetodosCompartidos.configuracion_XML_Elemento(this, "editText", "Nombre de PlayList", "");
            layout_Create.addView(inputNameList);
            
            
            LinearLayout layout_Buttons = new LinearLayout(this);
            Button buttonCancelar = (Button) MetodosCompartidos.configuracion_XML_Elemento(this, "textButton", "Cancelar", "");
            
            layout_Buttons.addView(buttonCancelar);
            Button buttonAccept = (Button) MetodosCompartidos.configuracion_XML_Elemento(this, "textButton", "Aceptar", "");
            layout_Buttons.addView(buttonAccept);
            layout_Create.addView(layout_Buttons);

            layout.addView(layout_Create);

            buttonCancelar.setOnClickListener(vv -> {
                MetodosCompartidos.openActivity(this, Lists.class, -1);
            });
            buttonAccept.setOnClickListener(vv ->{
                String nombrePlayList = inputNameList.getText().toString();
                if (!nombrePlayList.isEmpty()){
                    db.execSQL("INSERT INTO listas (nombre) VALUES (?)", new String[] {nombrePlayList});
                }
                MetodosCompartidos.openActivity(this, Lists.class, -1); 
            });
        });

        //Barra de Busqueda
        TextView btn_Bar = findViewById(getResources().getIdentifier("input_button","id", getPackageName()));
        btn_Bar.setOnClickListener(v->{
            MetodosCompartidos.openActivity(this, Search.class, false);
        });


        //Extraccion de Ubicaciones del GridLayout
        GridLayout layout = findViewById(getResources().getIdentifier("gridLayout","id",getPackageName()));
        //Consulta a la DB
        Cursor cursor = db.rawQuery("SELECT nombre FROM listas",null);
        if (cursor.moveToFirst()){
            do{
                try{
                    String name_List = cursor.getString(0);
                    LinearLayout layout_interno = new LinearLayout(this);
                    layout_interno.setOrientation(LinearLayout.VERTICAL);
                    ImageButton img_list = (ImageButton) MetodosCompartidos.configuracion_XML_Elemento(this, "imgButton", "@drawable/ic_user_singer", "layout_Lists");
                    TextView nombreList = (TextView) MetodosCompartidos.configuracion_XML_Elemento(this, "txtView", name_List, "list_Songs");
                    layout_interno.addView(img_list);
                    layout_interno.addView(nombreList);
                    layout.addView(layout_interno);

                    //Ingresar a Lista de Reproduccion
                    Log.e("DEBUG","antes de selecion añadiendo lista a listas");
                    img_list.setOnClickListener(v->{
                        Log.e("DEBUG","Se seleciono una lista");
                        MetodosCompartidos.openActivity(this, List.class, name_List); //AQUI
                    });
                }catch (Exception e){}
            }while(cursor.moveToNext());
        }
    }
}

