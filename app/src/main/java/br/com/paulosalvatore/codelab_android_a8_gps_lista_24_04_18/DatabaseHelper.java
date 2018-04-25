package br.com.paulosalvatore.codelab_android_a8_gps_lista_24_04_18;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ALUNO on 23/04/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, "codelab_db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE posicoes (id integer PRIMARY KEY, latitude double, longitude double, data_hora varchar(255));");
        }
        catch (Exception e) {
            Log.e("BANCO_DADOS", e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
