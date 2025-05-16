package com.juanpablogarzon.reproductor_movil;
import com.juanpablogarzon.reproductor_movil.utils.MetodosCompartidos;

import java.util.List;
import android.app.Activity; 
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.core.app.ActivityCompat; 
import android.database.Cursor;
import android.widget.LinearLayout;
import java.util.List;
import java.util.ArrayList;
import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.Manifest;
import android.content.pm.PackageManager;

import android.util.Log;


public class App extends Activity {
    Cursor info_Canciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int idXML = getResources().getIdentifier("activity_main", "layout", getPackageName());
        setContentView(idXML);

        //Barra de Busqueda
        TextView btn_Bar = findViewById(getResources().getIdentifier("input_button","id", getPackageName()));
        btn_Bar.setOnClickListener(v->{
            MetodosCompartidos.openActivity(this, Search.class, false);
        });

        MetodosCompartidos.navigation_Bar(this);
        
        //Permisos
        ActivityCompat.requestPermissions(this, new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO
        }, 1);

        info_Canciones = MetodosCompartidos.consult_DB_Android(this);

        //Redireccion a Albumes
        sectionPrint("albumes_Recientes", Album.class);

        //Redireccion a Artistias
        sectionPrint("artistas_Recientes",Artist.class);

        //Redireccion a Canciones Gustadas
        sectionPrint("canciones_MeGusta", Reproductor.class);

        //Ciere del Cursor de la DB
        info_Canciones.close();
    }

    public void sectionPrint(String id_Layout, Class<?> clase){
        LinearLayout layout = findViewById(getResources().getIdentifier(id_Layout, "id", getPackageName()));
        List<String> Lista_de_Artistas = new ArrayList<>();
        if (info_Canciones != null){
            info_Canciones.moveToFirst();
            Boolean impresion = false;
            SQLiteDatabase db = MetodosCompartidos.conectDB(this);
            Cursor cursor = null;
            if (id_Layout.equals("albumes_Recientes") || id_Layout.equals("artistas_Recientes")){
                cursor = db.rawQuery("SELECT * FROM historial_canciones ORDER BY id_history DESC",null);
            }else if(id_Layout.equals("canciones_MeGusta") ){
                cursor = db.rawQuery("SELECT c.nombre \n" + //
                    "FROM canciones c\n" + //
                        "\tINNER JOIN relaciones r\n" + //
                        "\t\tON c.id_Cancion = r.id_Cancion\n" + //
                        "\tINNER JOIN listas l\n" + //
                        "\t\tON r.id_lista = l.id_lista\n" + //
                    "WHERE l.nombre = 'Me Gusta';", null);
            }
            if (cursor != null && cursor.moveToFirst()){
                do{
                    Log.d("DEBUG","Iteraccion de cursor");
                    do{
                        int colum = (id_Layout.equals("canciones_MeGusta")) ? 0: 1;
                        if (cursor.getString(colum).equals(info_Canciones.getString(0))){
                            impresion = true;
                            break;
                        }
                    }while(info_Canciones.moveToNext());
                    if (impresion){
                        int posicionActual = info_Canciones.getPosition(); 
                        LinearLayout layout_interno = new LinearLayout(this);
                        layout_interno.setOrientation(LinearLayout.VERTICAL);
                        String ruta_img = (!id_Layout.equals("artistas_Recientes")) ?  info_Canciones.getString(2) : "@drawable/ic_user_singer";
        
                        ImageButton caratula_Cancion = (ImageButton) MetodosCompartidos.configuracion_XML_Elemento(this,"imgButton", ruta_img, "layout_main");
                        caratula_Cancion.setOnClickListener(v -> {
                            MetodosCompartidos.openActivity(this, clase, posicionActual);
                        });
                        layout_interno.addView(caratula_Cancion);
        
                        switch (id_Layout) {
                            case "canciones_MeGusta":
                                TextView nombre_Cancion = (TextView) MetodosCompartidos.configuracion_XML_Elemento(this, "txtView", info_Canciones.getString(0), "cancion");
                                layout_interno.addView(nombre_Cancion);
                                break;
                            case "albumes_Recientes":
                                TextView nombre_Album = (TextView) MetodosCompartidos.configuracion_XML_Elemento(this,"txtView",info_Canciones.getString(3),"cancion");
                                layout_interno.addView(nombre_Album);
                                break;
                        }if (!id_Layout.equals("albumes_Recientes")){
                            TextView nombre_Artista = (TextView) MetodosCompartidos.configuracion_XML_Elemento(this, "txtView", info_Canciones.getString(1),"artista");
                            layout_interno.addView(nombre_Artista);
                        }
                        if (!Lista_de_Artistas.contains(info_Canciones.getString(1)) || id_Layout.equals("canciones_MeGusta")){ //Filtro de Repeticion en Albumes
                            layout.addView(layout_interno);
                            Lista_de_Artistas.add(info_Canciones.getString(1));
                        }
                        impresion = false;
                    }
                    info_Canciones.moveToFirst();
                }while(cursor.moveToNext());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permisos, int[] resultados) { //Peticion de Permisos 
        if (requestCode == 1) {
            boolean permisosConcedidos = true;
            
            for (int resultado : resultados) {
                if (resultado != PackageManager.PERMISSION_GRANTED) {
                    permisosConcedidos = false;
                    break;
                }
            }
        }
    }
}
