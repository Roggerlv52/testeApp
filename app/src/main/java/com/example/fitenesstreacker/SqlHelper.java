package com.example.fitenesstreacker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SqlHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "fitness_tracker.db";
    private static final int DB_VERSON = 1;

    private static SqlHelper INSTANCIA;

    // executa retorno da variavel Instancia
    static SqlHelper getInstance(Context context) {
        if (INSTANCIA == null)
            INSTANCIA = new SqlHelper(context);
        return INSTANCIA;
    }

    private SqlHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSON);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE calc (id INTEGER primary key, type_calc TEXT, res DECIMAL, created_date DATETIME)"
        );
     // Corrigido erro de escrita  "CREATE TABLE calc (id INTEDER para INTEGER
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("TesteDate", "on Upgrade disparado");

    }
    //Buscar dados


    @SuppressLint("Range")
    List<Registro> getRegisterBy(String type) {
        List<Registro> registers = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM calc WHERE type_calc = ?", new String[]{type});
        try {
            if (cursor.moveToFirst()) {
                do {
                    Registro registro = new Registro();

                    registro.id = cursor.getInt(cursor.getColumnIndex("id"));
                    registro.type = cursor.getString(cursor.getColumnIndex("type_calc"));
                    registro.response = cursor.getDouble(cursor.getColumnIndex("res"));
                    registro.createDate = cursor.getString(cursor.getColumnIndex("created_date"));

                    registers.add(registro);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return registers;
    }

    // Criação de dados
    long additem(String type, double response) {
        SQLiteDatabase db = getWritableDatabase(); //execultar SQL de escrita
        long calcId = 0;
        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();

            values.put("type_calc", type);
            values.put("res", response);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pt", "BR"));
            String now = sdf.format(new Date());

            values.put("created_date", now);
            calcId = db.insertOrThrow("calc", null, values);

            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (db.isOpen())
                db.endTransaction();
        }
        return calcId;
    }
//    Buscar Dados
    long updateItem(String type, double response, int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long calcId = 0;

        try {
            ContentValues values = new ContentValues();
            values.put("type_calc", type);
            values.put("res", response);


            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pt", "BR"))
                    .format(Calendar.getInstance().getTime());
            values.put("created_date", now);
            // Passamos o whereClause para verificar o registro pelo ID e TYPE_CALC
            calcId = db.update("calc", values, "id = ? and type_calc = ?", new String[]{String.valueOf(id), type});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
        return calcId;
    }

    long removeItem(String type, int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long calcId = 0;

        try {
            // Passamos o whereClause para verificar o registro pelo ID e TYPE_CALC
            calcId = db.delete("calc", "id = ? and type_calc = ?", new String[]{String.valueOf(id), type});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
        return calcId;
    }
}
