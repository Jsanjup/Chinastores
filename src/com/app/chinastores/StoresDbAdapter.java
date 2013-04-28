/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.app.chinastores;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class StoresDbAdapter {

    public static final String KEY_TYPE = "type";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_VALOR = "valoration";
    public static final String KEY_NVALOR = "numvaloration";
    public static final String KEY_FOTO = "foto";
    public static final String KEY_INFO = "info";
    public static final String KEY_COMENTS = "coments";
    public static final String KEY_CONFIRMED= "confirmation";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DISTANCE ="distance";
    public static final int CONFIRMED = 0;
    public static final int NOTCONFIRMED = 4;

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table stores (_id integer primary key autoincrement, "
        + "type text not null, address text not null, valoration float not null, numvaloration integer, foto text not null, info text not null, coments text not null, confirmation integer, distance double);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "stores";
    private static final int DATABASE_VERSION = 1;
    public static final String SEP_COMENT ="_fin_coment_";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	
            db.execSQL(DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public StoresDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public StoresDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createNote(char A, String address, float valor,int nval, String foto, String info, String coments, boolean confirmed, double distance) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TYPE, ""+A);
        initialValues.put(KEY_ADDRESS, address);
        initialValues.put(KEY_VALOR, valor);
        initialValues.put(KEY_NVALOR, nval);
        initialValues.put(KEY_FOTO, foto);
        initialValues.put(KEY_INFO, info);
        initialValues.put(KEY_COMENTS, coments);
        if(confirmed)
            initialValues.put(KEY_CONFIRMED, CONFIRMED);
        else 
            initialValues.put(KEY_CONFIRMED, NOTCONFIRMED);
        initialValues.put(KEY_DISTANCE, distance+"");

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public long createStore(Store store){
    	return createNote(store.getType(), store.getAddress(),store.getVal(),store.getNumval(),store.getFoto(),store.getInfo(),store.getComments(), store.isConfirmed(), 0.0);  
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TYPE,
                KEY_ADDRESS, KEY_VALOR, KEY_NVALOR, KEY_FOTO, KEY_INFO, KEY_COMENTS, KEY_CONFIRMED, KEY_DISTANCE}, null, null, null, null, null);
    }
    
    public List<Store> fetchStoresByCursor(Cursor mCursor){
    	if(mCursor!=null){
    	List<Store> list = new ArrayList<Store>();
    	mCursor.moveToFirst();
    	while(!mCursor.isLast()){
    		list.add(CursorToStore(mCursor));
    		mCursor.moveToNext();
    	}
    	return list;
    	}else return null;
    }
    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all oneType note
     */
    public Cursor fetchByType(boolean type) throws SQLException {
    	String[] tip={""+'A'};
    	if(type) tip=new String[]{""+'B'};
    	Cursor mCursor=null;
    	try{
        mCursor= mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TYPE,
                KEY_ADDRESS, KEY_VALOR, KEY_CONFIRMED},  KEY_TYPE + "=?" , tip, null, null, null);
    	}catch(Exception e){
    		Log.w("cursor", "ha saltado excepcion");
    	}
    	
        if (mCursor != null) {
            mCursor.moveToFirst();  
        }
        return mCursor;
    }
    
    

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TYPE,
                    KEY_ADDRESS, KEY_VALOR, KEY_NVALOR, KEY_FOTO, KEY_INFO, KEY_COMENTS, KEY_CONFIRMED, KEY_DISTANCE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Store getStore(long rowId) throws SQLException {

        Cursor mCursor = fetchNote(rowId);
        Log.e("RowId", rowId+"");
        if (mCursor!=null) {
        char tipo = mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_TYPE)).charAt(0);
        String address = mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_ADDRESS));	    	
        boolean confirmed =(Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_CONFIRMED)))==CONFIRMED);
        float valoracion= Float.parseFloat(mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_VALOR)));
        int nvaloraciones= Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_NVALOR)));
        String info = mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_INFO));
        String coments = mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_COMENTS));
        String foto = mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_FOTO));
        double distance=Double.parseDouble(mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_DISTANCE)));
        Store store = new Store(tipo, address, valoracion, nvaloraciones, foto, info, coments, confirmed);
        store.setDistancia(distance);
        return store;}
        else return null;
    }
    public List<String> getDirecciones(){
    	List<String> direcciones = new ArrayList<String>();
    	Cursor mCursor =mDb.query(true, DATABASE_TABLE, new String[] {KEY_ADDRESS}, null, null, null, null, null, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                direcciones.add(mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_ADDRESS)));
        		mCursor.moveToNext();
            }
            return direcciones;
    }
    
    public Store CursorToStore(Cursor mCursor) throws SQLException {
        if (mCursor!=null) {
        char tipo = mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_TYPE)).charAt(0);
        String address = mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_ADDRESS));	    	
        boolean confirmed =(Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_CONFIRMED)))==CONFIRMED);
        float valoracion= Float.parseFloat(mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_VALOR)));
        int nvaloraciones= Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_NVALOR)));
        String info = mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_INFO));
        String coments = mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_COMENTS));
        String foto = mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_FOTO));
        double distance=Double.parseDouble(mCursor.getString(mCursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_DISTANCE)));
        Store store = new Store(tipo, address, valoracion, nvaloraciones, foto, info, coments, confirmed);
        store.setDistancia(distance);
        return store;}
        else return null;
    }
    /**
     * Return a Cursor positioned at the comments that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public String[] fetchComments(long rowId) throws SQLException {
    	

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_COMENTS}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
         String[] comentarios = mCursor.getString(mCursor.getColumnIndex(StoresDbAdapter.KEY_COMENTS)).split(SEP_COMENT);
        return comentarios ;

    }
    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, char A, String address, float valor, int nValor, String foto, String info, String coments, boolean confirmed, double distance) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TYPE, ""+A);
        initialValues.put(KEY_ADDRESS, address);
        initialValues.put(KEY_VALOR, valor);
        initialValues.put(KEY_NVALOR, nValor);
        initialValues.put(KEY_FOTO, foto);
        initialValues.put(KEY_INFO, info);
        initialValues.put(KEY_COMENTS, coments);
        if(confirmed)
            initialValues.put(KEY_CONFIRMED, CONFIRMED);
        else 
            initialValues.put(KEY_CONFIRMED, NOTCONFIRMED);
        initialValues.put(KEY_DISTANCE, distance+"");
        return mDb.update(DATABASE_TABLE, initialValues, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updateNote(long rowId, Store store) {
    	return updateNote(rowId, store.getType(), store.getAddress(),store.getVal(),store.getNumval(),store.getFoto(),store.getInfo(),store.getComments(), store.isConfirmed(), 0.0);  
    }
    

    public boolean updateNote(long rowId, Store store, double distance) {
    	return updateNote(rowId, store.getType(), store.getAddress(),store.getVal(),store.getNumval(),store.getFoto(),store.getInfo(),store.getComments(), store.isConfirmed(), distance);  
    }
}
