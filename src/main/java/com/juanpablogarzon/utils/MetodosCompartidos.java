package com.juanpablogarzon.reproductor_movil.utils;
import com.juanpablogarzon.reproductor_movil.App;
import com.juanpablogarzon.reproductor_movil.Reproductor;
import com.juanpablogarzon.reproductor_movil.Lists;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.graphics.Typeface;
import android.view.Gravity;

import android.content.Context;
import android.app.Activity;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Color;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.File;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

import android.view.ViewGroup;
import android.view.View;

import android.util.Log;


public class MetodosCompartidos{
    private Context context;
    private static ArrayList<Integer> list_IDs_Song = new ArrayList<>();
    public static void openActivity(Context context, Class<?> clase, Object row) {
        Intent intent = new Intent(context, clase);
        if (row instanceof Integer){
            intent.putExtra("COLUMNA_CANCION", (Integer) row);
        }else if (row instanceof String){
            intent.putExtra("NAME_LIST", (String) row);
        }
        if (!list_IDs_Song.isEmpty()){
            intent.putIntegerArrayListExtra("list_Songs", list_IDs_Song);
        }
        context.startActivity(intent);
    }

    public static Bitmap asignacion_Imagen(Context context, String url){
        if (url.contains("@drawable")){
            String resourceName = url.replace("@drawable/", "");
            int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            if (resourceId != 0) { 
                return BitmapFactory.decodeResource(context.getResources(), resourceId);
            }
        }else{
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(url);
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null){
                return BitmapFactory.decodeByteArray(art, 0, art.length);
            }
        }
        return null;
    }

    public static Object configuracion_XML_Elemento(Context context, String typeElement ,String data, String subType){
        switch (typeElement){
            case "textButton":
                Button buton = new Button(context);
                buton.setText(data);
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(120, 50);
                switch (subType) {
                    case "btn_Normal":
                        parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40);
                        buton.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                        buton.setBackground(null);
                        break;
                    case "btn_Close":
                        parms.gravity = Gravity.CENTER_HORIZONTAL;
                }
                buton.setLayoutParams(parms);
                return buton;
            case "imgButton":
                ImageButton button = new ImageButton(context);
                LinearLayout.LayoutParams params;
                button.setBackground(null);
                button.setImageBitmap(asignacion_Imagen(context, data));
                button.setScaleType(ImageView.ScaleType.CENTER_CROP);
                switch (subType) {
                    case "layout_main":
                        button.setLayoutParams(new LinearLayout.LayoutParams(145, 145));
                        break;
                    case "layout_Artist_IMG":
                        button.setLayoutParams(new LinearLayout.LayoutParams(70, 70));
                        break;
                    case "layout_Lists":
                        button.setBackgroundColor(Color.parseColor("#a9aaab"));
                        button.setPadding(5,5,5,5);
                        button.setLayoutParams((new LinearLayout.LayoutParams(150, 150)));
                        params = new LinearLayout.LayoutParams(150, 150);
                        params.setMargins(3, 3, 3, 3);
                        button.setLayoutParams(params);
                        break;
                    case "layout_Artist_BTN":
                        params = new LinearLayout.LayoutParams(55, 55);
                        params.gravity = Gravity.END; 
                        button.setLayoutParams(params);
                        button.setPadding(0, 15, 0, 0);
                        button.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        break;
                    case "layout_List":
                        params = new LinearLayout.LayoutParams(40, 40);
                        params.gravity = Gravity.TOP; 
                        button.setLayoutParams(params);
                        button.setPadding(0, 0, 10, 0);
                }
                return button;
            case "txtView":
                TextView text = new TextView(context);
                text.setText(data);
                text.setPadding(10, 15, 0, 0);
                switch (subType) {
                    case "cancion":
                        text.setTextSize(12);
                        text.setMaxWidth(120);
                        text.setTypeface(null, Typeface.BOLD);
                        break;
                    case "cancion_Artist":
                        text.setTextSize(14);
                        text.setTypeface(null, Typeface.BOLD);
                        break;
                    case "list_Songs":
                        text.setPadding(0,0,0,0);
                        text.setTextSize(14);
                        break;
                    case "artista":
                        text.setPadding(10, 0, 0, 0);
                        text.setTextSize(10);
                        break;
                    case "artist_Artist":
                        text.setPadding(5, 2, 0, 0);
                        text.setTextSize(14);
                }
                return text;
            case "editText":
                EditText editText = new EditText(context);
                editText.setHint(data);
                editText.setFocusable(true);
                editText.setClickable(true);
                return editText;
            case "view":
                View view = new View(context);
                LinearLayout.LayoutParams parmmss = new LinearLayout.LayoutParams(0, 0, 1.0f );
                view.setLayoutParams(parmmss);
                return view;
        }
        return null;
    }

    public static void asignacion_de_Datos(Context context, String id, String value, String typeDate){
        int ElementViewId = context.getResources().getIdentifier(id, "id", context.getPackageName());
        switch (typeDate) {
            case "txtView":
                TextView txtView = (TextView) ((Activity) context).findViewById(ElementViewId);
                txtView.setText(value);
                break;
            case "imgView":
                ImageView imageView = (ImageView) ((Activity) context).findViewById(ElementViewId);
                imageView.setImageBitmap(asignacion_Imagen(context, value));
        }
    }

    public static Cursor consult_DB_Android(Context context){
        Uri db = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // Base de datos de música
        String[] columnas = { 
            MediaStore.Audio.Media.TITLE, // Nombre de la canción
            MediaStore.Audio.Media.ARTIST,// Nombre del artista
            MediaStore.Audio.Media.DATA, //Ruta de Archivo
            MediaStore.Audio.Media.ALBUM //Nombre de Album
        };
        return context.getContentResolver().query(db, columnas, null, null, null);
    }

    public static void guardadoJson(Context context, JSONObject JSONObject, Boolean showReadJson){
        try{
            FileOutputStream refFile = context.openFileOutput("json_keepData", Context.MODE_PRIVATE);
            refFile.write(JSONObject.toString().getBytes());
            refFile.flush();
            refFile.close();
            File file = context.getFileStreamPath("json_keepData");
        }catch (IOException e) {
            e.printStackTrace();
            Log.e("DEBUG", "Error al guardar datos en JSON: " + e.getMessage());
        }
        if (showReadJson){
            leerJson(context, true);
        }
    }

    public static String leerJson(Context context, Boolean showLog ){
        try{
            File file = context.getFileStreamPath("json_keepData");
            FileInputStream refFile = context.openFileInput("json_keepData");
            StringBuilder jsonData = new StringBuilder();
            int Char;
            while ((Char = refFile.read()) != -1) {
                jsonData.append((char) Char);
            }
            if(showLog){
                Log.d("DEBUG", "Datos leidos en JSON: " + jsonData.toString());
            }
            refFile.close();
            return jsonData.toString();
        }catch(IOException e){
            e.printStackTrace();
            Log.e("DEBUG", "Error al extreer datos en JSON: " + e.getMessage());
            return null;
        }
    }

    public static void vistaCompartida(Context context, int id_dato_Asignado, String TextViewPrincipal){
        //Asignacion de Argumentos Recibidos
        Cursor info_Canciones = consult_DB_Android(context);
        String nombre_Principal = "";
        Intent intent = ((Activity) context).getIntent();
        String name_List = "";
        SQLiteDatabase db = conectDB(context);

        if (info_Canciones != null){
            if (intent.getStringExtra("NAME_LIST") != null){ //Extraccion de Nombre de lista
                name_List = intent.getStringExtra("NAME_LIST");
                asignacion_de_Datos(context,TextViewPrincipal,name_List,"txtView");
            }else if (intent.hasExtra("COLUMNA_CANCION")){//Asignacion de Informacion de Encabezados para Albumes y Artistas
                int id_fuente = intent.getIntExtra("COLUMNA_CANCION", 0);
                do{
                    if (info_Canciones.getPosition() == id_fuente){
                        nombre_Principal = info_Canciones.getString(id_dato_Asignado);
                        asignacion_de_Datos(context,TextViewPrincipal,nombre_Principal,"txtView");
                        if (id_dato_Asignado == 3){ //Filtro de Asginacion de Imagen solo para el Album
                            asignacion_de_Datos(context,"img_Album",info_Canciones.getString(2),"imgView");
                        }
                        break;
                    }
                }while(info_Canciones.moveToNext());
            }
            
            //Asignacion de Caciones
            LinearLayout layout_Canciones = ((Activity) context).findViewById(context.getResources().getIdentifier("layout_Songs", "id", context.getPackageName()));
            String sql = "SELECT a.nombre AS cancion, l.nombre AS lista\n" + //
                        "FROM canciones a\n" + //
                            "\tINNER JOIN relaciones r\n" + //
                                "\t\tON a.id_Cancion = r.id_Cancion\n" + //
                            "\tINNER JOIN listas l\n" + //
                                "\t\tON l.id_lista = r.id_lista;";
            Cursor cursor = db.rawQuery(sql,null);
            Boolean impresion = false;
            list_IDs_Song.clear();

            info_Canciones.moveToFirst(); 
            do{
                if (nombre_Principal.equals(info_Canciones.getString(id_dato_Asignado))){
                    impresion = true;
                }else if((info_Canciones.getString(0).toLowerCase()).contains(TextViewPrincipal.toLowerCase()) && !TextViewPrincipal.isEmpty()){
                    impresion = true;
                }else if (cursor.moveToFirst() && !name_List.equals("")){
                    do{
                        if (cursor.getString(0).equals(info_Canciones.getString(0)) && cursor.getString(1).equals(name_List)){
                            impresion = true;
                            list_IDs_Song.add(info_Canciones.getPosition()); //Insertar en la lista el id de Cancion
                            break;
                        }
                    }while(cursor.moveToNext());
                }
                if (impresion){
                    LinearLayout layout_cancion = new LinearLayout(context);
                    LinearLayout layout_Data_cacion = new LinearLayout(context);
                    layout_Data_cacion.setOrientation(LinearLayout.VERTICAL);
    
                    ImageView caratula_Cancion = (ImageView) configuracion_XML_Elemento(context,"imgButton", info_Canciones.getString(2), "layout_Artist_IMG");
                    layout_cancion.addView(caratula_Cancion);
    
                    TextView nombre_Song = (TextView) configuracion_XML_Elemento(context, "txtView", info_Canciones.getString(0), "cancion_Artist");
    
                    layout_Data_cacion.addView(nombre_Song);
                    TextView nombre_Artist = (TextView) configuracion_XML_Elemento(context, "txtView", info_Canciones.getString(1), "artist_Artist");
                    layout_Data_cacion.addView(nombre_Artist);
                    LinearLayout.LayoutParams paramsDatosCancion = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    layout_Data_cacion.setLayoutParams(paramsDatosCancion);
                    layout_cancion.addView(layout_Data_cacion);
    
                    ImageButton btn_play = (ImageButton) configuracion_XML_Elemento(context,"imgButton","@drawable/ic_play_music","layout_Artist_BTN");
                    int posicionActual = info_Canciones.getPosition();

                    btn_play.setOnClickListener(v->{
                        openActivity(context, Reproductor.class, posicionActual);
                    });
                    layout_cancion.addView(btn_play);
                    layout_Canciones.addView(layout_cancion);
                    impresion = false;
                    Log.e("DEBUG","Se imprimio");
                }
            }while(info_Canciones.moveToNext());
        }
    }

    public static SQLiteDatabase conectDB(Context context){
        //Extraccion de Datos
        File dbFile = context.getDatabasePath("db_listMusic.databases");
        if(!dbFile.exists()){
            try{
                InputStream fileDB = context.getAssets().open("db_listMusic.databases");
                OutputStream outFile = new FileOutputStream(context.getDatabasePath("db_listMusic.databases"));
                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = fileDB.read(buffer)) > 0) {
                    outFile.write(buffer, 0, bytes);
                }
                fileDB.close();
                outFile.close();
            }catch (Exception e){}
        }
        //Extraccion de Datos
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
            context.getDatabasePath("db_listMusic.databases").getPath(),
            null,
            SQLiteDatabase.OPEN_READWRITE
        );
        return db;
    }
    public static void navigation_Bar(Context context){
        LinearLayout layout = ((Activity) context).findViewById(context.getResources().getIdentifier("layoutPrincipal", "id", context.getPackageName()));
        LinearLayout layoutNav = new LinearLayout(context);
        layoutNav.setBackgroundColor(Color.parseColor("#262424"));
        layoutNav.setPadding(30,5,30,5);

        ImageButton btn_home = (ImageButton) configuracion_XML_Elemento(context, "imgButton", "@drawable/ic_home", "layout_List");
        layoutNav.addView(btn_home);

        View view1 = (View) configuracion_XML_Elemento(context, "view", "", "");
        layoutNav.addView(view1);
        
        ImageButton btn_list = (ImageButton) configuracion_XML_Elemento(context, "imgButton", "@drawable/ic_list_music", "layout_List");
        layoutNav.addView(btn_list);


        View view2 = (View) configuracion_XML_Elemento(context, "view", "", "");
        layoutNav.addView(view2);

        ImageButton btn_player = (ImageButton) configuracion_XML_Elemento(context, "imgButton", "@drawable/ic_songs", "layout_List");
        layoutNav.addView(btn_player);

        layout.addView(layoutNav);

        btn_home.setOnClickListener(v ->{
            openActivity(context, App.class, -1);
        });
        btn_list.setOnClickListener(v ->{
            openActivity(context, Lists.class, -1);
        });
        btn_player.setOnClickListener(v ->{
            openActivity(context, Reproductor.class, -1);
        });
    }
}