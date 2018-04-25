package br.com.paulosalvatore.codelab_android_a8_gps_lista_24_04_18;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALUNO on 23/04/2018.
 */

public class DatabaseManager {
    private static DatabaseManager instancia;
    private static DatabaseHelper helper;

    private boolean conexaoAberta = false;
    private SQLiteDatabase db;

    public static void inicializarInstancia(DatabaseHelper helper) {
        if (instancia == null) {
            instancia = new DatabaseManager();
            instancia.helper = helper;
        }
    }

    public static DatabaseManager getInstancia() {
        if (instancia == null) {
            throw new IllegalStateException("Declare 'inicializarInstancia(...) primeiro.");
        }

        return instancia;
    }

    public void abrirConexao() {
        if (!conexaoAberta) {
            db = helper.getWritableDatabase();
            conexaoAberta = true;
        }
    }

    public void fecharConexao() {
        if (conexaoAberta) {
            db.close();
            conexaoAberta = false;
        }
    }

    public List<Posicao> obterPosicoes() {
        List<Posicao> posicoes = new ArrayList<Posicao>();

        abrirConexao();

        String sql = "SELECT * FROM posicoes";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                String data_hora = cursor.getString(cursor.getColumnIndex("data_hora"));

                Posicao posicao = new Posicao(
                        id,
                        latitude,
                        longitude,
                        data_hora
                );

                posicoes.add(posicao);
            } while (cursor.moveToNext());
        }

        return posicoes;
    }

    public void inserirPosicao(Posicao posicao) {
        abrirConexao();

        ContentValues contentValues = new ContentValues();
        contentValues.put("latitude", posicao.getLatitude());
        contentValues.put("longitude", posicao.getLongitude());
        contentValues.put("data_hora", posicao.getData_hora());

        db.insert("posicoes", null, contentValues);

        fecharConexao();
    }

    public void editarPosicao(Posicao posicao) {
        String sql = "UPDATE posicoes SET latitude = '" + posicao.getLatitude() + "'," +
                "longitude = '" + posicao.getLongitude() + "'," +
                "data_hora = '" + posicao.getData_hora() + "' " +
                "WHERE (id = '" + posicao.getId() + "')";

        abrirConexao();

        db.execSQL(sql);

        fecharConexao();
    }

    public void removerPosicao(Posicao posicao) {
        String sql = "DELETE FROM posicoes WHERE (id = '" + posicao.getId() + "')";

        abrirConexao();

        db.execSQL(sql);

        fecharConexao();
    }
}
