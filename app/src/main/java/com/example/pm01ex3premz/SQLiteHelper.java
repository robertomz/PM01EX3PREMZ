package com.example.pm01ex3premz;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {


    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String desc, String cantidad, String tiempo, String periodo, byte[] image) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO MEDICAMENTOS VALUES (NULL, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, desc);
        statement.bindString(2, cantidad);
        statement.bindString(3, tiempo);
        statement.bindString(4, periodo);
        statement.bindBlob(5, image);

        statement.executeInsert();
    }

    public void updateData(String desc, String cantidad, String tiempo, String periodo, byte[] image, int id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE MEDICAMENTOS SET descp = ?, cantidad = ?, tiempo = ?, periodo = ?, image = ? WHERE id = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, desc);
        statement.bindString(2, cantidad);
        statement.bindString(3, tiempo);
        statement.bindString(4, periodo);
        statement.bindBlob(5, image);
        statement.bindDouble(6, (double) id);

        statement.executeInsert();
        database.close();
    }

    public  void deleteData(int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM MEDICAMENTOS WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double) id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
