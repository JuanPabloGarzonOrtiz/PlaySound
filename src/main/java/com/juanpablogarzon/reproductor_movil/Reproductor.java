package com.juanpablogarzon.reproductor_movil;
import com.juanpablogarzon.reproductor_movil.utils.MetodosCompartidos;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.SeekBar;

import android.widget.ImageButton;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import org.json.JSONObject;
import org.json.JSONException;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.FrameLayout;
import android.widget.Button;
import android.view.ViewGroup;
import android.view.Gravity;
import android.graphics.Color;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;

public class Reproductor extends Activity implements MediaPlayer.OnCompletionListener{
    private static MediaPlayer mediaPlayer; // Variable de MediaPlayer Statica para que se mantenga cuando se sale del layout
    Cursor info_Canciones; // Cursor de la DB Android
    int num_songJSON = 0; //Variable de posicion de cancion actual en el JSON
    String cancionPath; // Variable donde se guarda la ruta de la cancion
    Boolean showLog_Initial = true; // Variable para mostrar el Log DataSong al inicio
    Boolean is_favorite = false;
    int id_cancion = 0;
    int id_origin = 0; // Es la variable que se le asigna el ID de la cancion iriginal recibida.
    ArrayList<Integer> list_IDs_Song = new ArrayList<>();
    SQLiteDatabase db; // Bd de Aplicacion

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int idXML = getResources().getIdentifier("activity_playmusic", "layout", getPackageName());
        setContentView(idXML);
        MetodosCompartidos.navigation_Bar(this);
        extract_infoSong();

        //Reproduccion de Cancion con Argumento
        Intent intent = getIntent();
        id_cancion = intent.getIntExtra("COLUMNA_CANCION", 0); //ID de cancion a Reproducir
        if (id_cancion != -1){
            select_Button("play_music", null, false); 
            ImageButton btnStart = findViewById(getResources().getIdentifier("play_music", "id", getPackageName()));
            btnStart.performClick();
            Log.e("DEBUG","Este es el id:" + id_cancion);
            id_origin = id_cancion;
            id_cancion = -1;
            btnStart.setOnClickListener(null);
            Log.e("DEBUG","Funcion de reproduccion automatica");
        }
        if (intent.getIntegerArrayListExtra("list_Songs") != null){
            list_IDs_Song = intent.getIntegerArrayListExtra("list_Songs"); //IDs de las Canciones de la Lista
        }

        //Acciones con selecion de Botones
        select_Button("play_music", this::reproducir_Cancion,true);
        select_Button("skip_previous", null, false);
        select_Button("skip_next", null, false);
        select_Button("favoriteSong", null, false);
        select_Button("mas", null, false);
    }

    @Override
    public void onCompletion(MediaPlayer mp) { //Cambio Automatico de Cancion
        select_Button("skip_next", null, false);
        ImageButton btnSkipNext = findViewById(getResources().getIdentifier("skip_next", "id", getPackageName()));
        btnSkipNext.performClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            configuracion_SeekBar(mediaPlayer.getDuration());
        }
    }

    private void reproducir_Cancion(){
        int btnId = getResources().getIdentifier("play_music", "id", getPackageName());
        ImageButton btnPlay = findViewById(btnId);
        if (mediaPlayer == null){
            try{
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnCompletionListener(this); //Inicio de Reproduccion
                mediaPlayer.setDataSource(cancionPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                btnPlay.setImageResource(getResources().getIdentifier("ic_stop_music", "drawable", getPackageName()));
                configuracion_SeekBar(mediaPlayer.getDuration());
            }catch(Exception e){
                e.printStackTrace();
            }
        }else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); //Pausar
                btnPlay.setImageResource(getResources().getIdentifier("ic_play_music", "drawable", getPackageName()));
            }else{
                mediaPlayer.start(); //Estaba en pausa, lo reanudamos
                btnPlay.setImageResource(getResources().getIdentifier("ic_stop_music", "drawable", getPackageName()));
            }
        }
        try {
            JSONObject jsonData = new JSONObject(MetodosCompartidos.leerJson(this, false));
            boolean estadoActual = mediaPlayer != null && mediaPlayer.isPlaying();
            jsonData.put("IS_PLAYING", estadoActual);
            MetodosCompartidos.guardadoJson(this, jsonData, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void configuracion_SeekBar(int duration){
        int seekBarId = getResources().getIdentifier("seekBar", "id", getPackageName());
        SeekBar  seekBar = findViewById(seekBarId);
        seekBar.setMax(duration);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 1000); // Se actualiza cada segundo
            }
        }, 1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progreso, boolean modificacion_usuario) {
                if (modificacion_usuario) { 
                    mediaPlayer.seekTo(progreso);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {} //Usuario mieve la barra
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {} //Usuario suelta la Barra
        });
    }

    private void select_Button(String id, Runnable metodo, boolean selecion_Simple){
        int btnId = getResources().getIdentifier(id, "id", getPackageName());
        ImageButton  btn = findViewById(btnId);
        if (selecion_Simple){
            btn.setOnClickListener(v -> metodo.run());
            return;
        }else{
            btn.setOnClickListener(v ->{
                Boolean selectBtnNoTMusic = false;
                int moveto_SONG = 0;
                if (mediaPlayer != null && (!id.equals("favoriteSong") && !id.equals("mas"))){
                    mediaPlayer.pause();
                    mediaPlayer = null;
                    Log.e("DEBUG","Se detiene el MediaPlayer");
                }
                switch (id){
                    case "skip_previous": 
                        moveto_SONG = (num_songJSON == 0) ? info_Canciones.getCount() -1 : num_songJSON -1; 
                        if (list_IDs_Song.size() > 1){
                            int mindList = list_IDs_Song.get(0);
                            moveto_SONG = (num_songJSON == mindList) ?
                                                            list_IDs_Song.get(list_IDs_Song.size() -1):
                                                            list_IDs_Song.get(list_IDs_Song.indexOf(id_origin) - 1);
                            Log.e("DEBUG", "Esta es la posicion actual: " + id_origin);
                            Log.e("DEBUG", "Este es la proxima posicion: " + moveto_SONG);
                            id_origin = moveto_SONG;
                        }
                        break;
                    case "skip_next":
                        moveto_SONG = (num_songJSON == info_Canciones.getCount() -1) ? 0 :num_songJSON + 1;
                        if (list_IDs_Song.size() > 1){
                            int maxidList = list_IDs_Song.get(list_IDs_Song.size() -1);
                            moveto_SONG = (num_songJSON == maxidList) ? 
                                                                list_IDs_Song.get(0): 
                                                                list_IDs_Song.get(list_IDs_Song.indexOf(id_origin) + 1);
                            Log.e("DEBUG", "Esta es la posicion actual: " + id_origin);
                            Log.e("DEBUG", "Este es la proxima posicion: " + moveto_SONG);
                            id_origin = moveto_SONG;
                        }
                        break;
                    case "play_music": 
                        moveto_SONG = id_cancion;
                        break;
                    case "favoriteSong":
                        insertSonginLists("1");
                        break;
                    case "mas": //Vista de Submenu Song
                        FrameLayout layout = findViewById(getResources().getIdentifier("layout_Song", "id", getPackageName()));

                        //Seccion Desplizable
                        LinearLayout layout_Options = new LinearLayout(this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
                        params.gravity = Gravity.BOTTOM;
                        layout_Options.setLayoutParams(params);
                        layout_Options.setOrientation(LinearLayout.VERTICAL);
                        layout_Options.setBackgroundColor(Color.parseColor("#F0000000"));

                        ScrollView scrollView = new ScrollView(this);
                        scrollView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        
                        LinearLayout internalLayout = new LinearLayout(this);
                        internalLayout.setOrientation(LinearLayout.VERTICAL);
                        internalLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)); 

                        Button btn_AddtoList = (Button) MetodosCompartidos.configuracion_XML_Elemento(this, "textButton", "Añadir a Lista de Reproducción", "btn_Normal");
                        internalLayout.addView(btn_AddtoList);

                        Button btn_DeltoList = (Button) MetodosCompartidos.configuracion_XML_Elemento(this, "textButton", "Eliminar de Lista de Reproducción", "btn_Normal");
                        internalLayout.addView(btn_DeltoList);

                        Button btn_OpenAlbum = (Button) MetodosCompartidos.configuracion_XML_Elemento(this, "textButton", "Ir al Album", "btn_Normal");
                        internalLayout.addView(btn_OpenAlbum);

                        Button btn_OpenArtist = (Button) MetodosCompartidos.configuracion_XML_Elemento(this, "textButton", "Ir al Artista", "btn_Normal");
                        internalLayout.addView(btn_OpenArtist);

                        Button btn_Close = (Button) MetodosCompartidos.configuracion_XML_Elemento(this, "textButton", "Cerrar", "btn_Close");
                        internalLayout.addView(btn_Close);
                        
                        scrollView.addView(internalLayout);
                        layout_Options.addView(scrollView);
                        layout.addView(layout_Options);
                        

                        btn_AddtoList.setOnClickListener(vv ->{
                            layout.addView(addANDdelSong("add"));
                        });
                        btn_DeltoList.setOnClickListener(vv ->{
                            layout.addView(addANDdelSong("del"));
                        });
                        btn_OpenAlbum.setOnClickListener(vv -> {
                            MetodosCompartidos.openActivity(this, Album.class, num_songJSON);
                        });
                        btn_OpenArtist.setOnClickListener(vv -> {
                            MetodosCompartidos.openActivity(this, Artist.class, num_songJSON);
                        });
                        btn_Close.setOnClickListener(vv -> {
                            MetodosCompartidos.openActivity(this, Reproductor.class, -1);
                        });
                }
                if (id.equals("favoriteSong") || id.equals("mas")){ //Asignacion de id de Cancion en botones de favirto y mas
                    moveto_SONG = num_songJSON;
                    selectBtnNoTMusic = true;
                }else if (list_IDs_Song.size() == 1){
                    moveto_SONG = id_origin;
                }
                try{
                    info_Canciones.moveToPosition(moveto_SONG);
                    JSONObject songDataNew = new JSONObject();
                    songDataNew.put("TITLE", info_Canciones.getString(0));
                    songDataNew.put("ARTIST", info_Canciones.getString(1));
                    songDataNew.put("DATA", info_Canciones.getString(2));
                    songDataNew.put("NUM_SONG", moveto_SONG);
                    songDataNew.put("IS_PLAYING", mediaPlayer != null && mediaPlayer.isPlaying());
                    Log.e("DEBUG","Este es el log a guardar" + songDataNew.toString());
                    MetodosCompartidos.guardadoJson(this, songDataNew, false);
                    info_Canciones.moveToFirst();
                }catch(JSONException e){
                    e.printStackTrace();
                }
                extract_infoSong();

                if (!selectBtnNoTMusic){
                    reproducir_Cancion();
                }
            });
            return;
        }
    }
    private LinearLayout addANDdelSong(String type){
        LinearLayout layout_Create = new LinearLayout(this);
        FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(250, 150);
        parms.gravity = Gravity.CENTER;
        layout_Create.setLayoutParams(parms);
        layout_Create.setOrientation(LinearLayout.VERTICAL);
        layout_Create.setBackgroundColor(Color.parseColor("#F0000000"));

        TextView title = (TextView) MetodosCompartidos.configuracion_XML_Elemento(this, "txtView", "Elige una Lista de Reproducción", "cancion_Artist");
        layout_Create.addView(title);
        
        String isAdd = (type.equals("add")) ? "NOT":"";
        String sql = "SELECT l.nombre AS lista FROM listas l\n" + //
                    "WHERE l.nombre != 'Me Gusta' AND l.id_lista "+isAdd+" IN" + //
                        "(SELECT r.id_lista FROM relaciones r\n" + //
                            "INNER JOIN canciones c\n" + //
                                "ON r.id_Cancion = c.id_Cancion\n" + //
                        "WHERE c.nombre = ?);";
        Cursor cursor = db.rawQuery(sql, new String[] {info_Canciones.getString(0)});
        if(cursor.moveToFirst()){
            do{
                String nameList = cursor.getString(0);
                Button btnList = (Button) MetodosCompartidos.configuracion_XML_Elemento(this, "textButton", nameList, "btn_Normal");
                layout_Create.addView(btnList);
                btnList.setOnClickListener(vvv ->{
                    Cursor cursorListid = db.rawQuery("SELECT id_lista FROM listas WHERE nombre = ?", new String[]{nameList});
                    String id_list = (!cursorListid.moveToFirst()) ? "1": cursorListid.getString(0);
                    //Boolean boolAddDel = (type.equals("add") ? true : false);
                    insertSonginLists(id_list);
                });
            }while(cursor.moveToNext());
        }else{
            title = (TextView) MetodosCompartidos.configuracion_XML_Elemento(this, "txtView", "No Hay Listas Creadas", "cancion_Artist");
            layout_Create.addView(title);
        }
        
        LinearLayout layout_Buttons = new LinearLayout(this);
        Button buttonCancelar = (Button) MetodosCompartidos.configuracion_XML_Elemento(this, "textButton", "Cancelar", "btn_Close");
        layout_Create.addView(buttonCancelar);

        buttonCancelar.setOnClickListener(vvv -> {
            MetodosCompartidos.openActivity(this, Reproductor.class, -1);
        });
        return layout_Create;
    }
    private void insertSonginLists(String id_List){
        //Objetencion de id de Cancion
        String id_song = "";
        Cursor cursoridsong = db.rawQuery("SELECT id_Cancion FROM canciones WHERE nombre = ?", new String[]{info_Canciones.getString(0)});
        if (cursoridsong.moveToFirst()){
            id_song = cursoridsong.getString(0);
        }else{
            db.execSQL("INSERT INTO canciones (nombre) VALUES (?)", new String[]{info_Canciones.getString(0)});
            // Recuperar el nuevo ID real
            Cursor newCursor = db.rawQuery("SELECT id_Cancion FROM canciones WHERE nombre = ?", new String[]{info_Canciones.getString(0)});
            if (newCursor.moveToFirst()) {
                id_song = newCursor.getString(0);
            }
        }
        // Verificar si ya existe la relación
        Cursor cursorexistinList = db.rawQuery("SELECT * FROM relaciones WHERE id_lista = ? AND id_Cancion = ?", new String[]{id_List, id_song});
        if (!cursorexistinList.moveToFirst()){ //No hay una relacion con la Lista
            db.execSQL("INSERT INTO relaciones (id_Cancion, id_lista) VALUES (?,?);", new String[] {id_song, id_List});
        }else{ // Si hay Relacion Borrarla
            //Eliminacion de Registros
            db.execSQL("DELETE FROM relaciones WHERE id_Cancion = ? AND id_lista = ?",new String[] {id_song, id_List});
            //db.execSQL("DELETE FROM canciones WHHERE id_Cancion = ?", new String[] {id_song});
        }
        MetodosCompartidos.openActivity(this, Reproductor.class, -1);
    }
    
    private void extract_infoSong(){
        info_Canciones = MetodosCompartidos.consult_DB_Android(this);
        JSONObject jsonData = null;
        try{
            jsonData = new JSONObject(MetodosCompartidos.leerJson(this, showLog_Initial));
            Log.e("DEBUG","Este el valor impreso" + jsonData.toString());
            showLog_Initial = (showLog_Initial.equals(true)) ? false: true;
            num_songJSON = Integer.parseInt(jsonData.getString("NUM_SONG").replaceAll("[^\\p{Print}]", ""));
        }catch(JSONException e){ 
            e.printStackTrace();
            Log.e("DEBUG","Error en la asignacion del valor sacado del JSON");
        }
        if (info_Canciones != null && jsonData != null){
            ImageButton btnFavoriteSong = findViewById(getResources().getIdentifier("favoriteSong", "id", getPackageName()));
            db = MetodosCompartidos.conectDB(this);
            String sql = "SELECT c.nombre AS Cancion, l.nombre AS Lista \n" + //
                        "FROM canciones c\n" + //
                        "INNER JOIN relaciones r\n" + //
                            "\tON c.id_Cancion = r.id_Cancion\n" + //
                        "INNER JOIN listas l \n" + //
                            "\tON l.id_lista = r.id_lista\n" + //
                            "\tWHERE l.nombre = 'Me Gusta';";
            Cursor cursor = db.rawQuery(sql,null);

            //Asinacion de Datos de Cancion
            info_Canciones.moveToFirst();
            do{
                try {
                    int posicionDB = info_Canciones.getPosition();
                    if (num_songJSON == posicionDB) {//El valor de la cancion guardada en el JSON tiene conicidencia con una de la DB
                        MetodosCompartidos.asignacion_de_Datos(this, "img_Song", info_Canciones.getString(2), "imgView");
                        MetodosCompartidos.asignacion_de_Datos(this, "txt_nombreCancion", info_Canciones.getString(0),"txtView");
                        MetodosCompartidos.asignacion_de_Datos(this, "txt_nameArtist", info_Canciones.getString(1), "txtView");
                        Boolean status = Boolean.parseBoolean(jsonData.getString("IS_PLAYING").replaceAll("[^\\p{Print}]", ""));
                        if (status.equals(true)){ //Verifica si el estado de la reproduccion es true aplicar el icono de pla_ymusic
                            ImageButton btnPlay = findViewById(getResources().getIdentifier("play_music", "id", getPackageName()));
                            btnPlay.setImageResource(getResources().getIdentifier("ic_stop_music", "drawable", getPackageName()));
                            Log.e("DEBUG","El estado de MediaPlayer es de:" + mediaPlayer);
                        }
                        //Asignacion de Icono Favorite
                        if (cursor.moveToFirst()){
                            do{
                                if (cursor.getString(0).equals(info_Canciones.getString(0))){
                                    btnFavoriteSong.setImageResource(getResources().getIdentifier("ic_favorite", "drawable", getPackageName()));
                                    is_favorite = true;
                                    break;
                                }else{is_favorite= false;}
                            }while (cursor.moveToNext());
                        }if(!is_favorite){
                            btnFavoriteSong.setImageResource(getResources().getIdentifier("ic_notfavorite", "drawable", getPackageName()));
                            is_favorite = false;
                        }
                        cancionPath = info_Canciones.getString(2);
                        break;
                    }
                } catch (Exception e) {
                    Log.e("DEBUG","Error en la comparacion");
                    e.printStackTrace();
                }
            }while (info_Canciones.moveToNext());

            //Registro de Reprduccion de Cancion en el Historial
            Cursor query = db.rawQuery("SELECT * FROM historial_canciones WHERE nombre_Song = ?", new String[] {info_Canciones.getString(0)}); 
            if (query.moveToFirst()){
                Log.d("DEBUG","Se encotro la cancion");
                db.execSQL("DELETE FROM historial_canciones WHERE nombre_Song = ?", new String[] {info_Canciones.getString(0)});
            }
            Log.d("DEBUG","Antes de la insercion");
            db.execSQL("INSERT INTO historial_canciones (nombre_Song) VALUES (?)", new String[] {info_Canciones.getString(0)});
            Log.d("DEBUG","Despues de la insercion");
        }else{
            Log.e("DEBUG","Uno de los retornos de la DB o el JSON estan vacios");
        }
    }
}