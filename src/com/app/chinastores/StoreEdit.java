/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.chinastores;

import com.app.chinastores.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class StoreEdit extends Activity {

    private EditText direccion;
    private CheckBox confirmar;
    private RatingBar valorar;
    private Button foto;
    private ToggleButton tipo;
    private TextView info;
    private EditText comentar;
    private Button send;
    private Button accept;
    private Button remove;
    private int nValor;
    private boolean confirmed;
    private char type;
    
    private Store store;
    
    private Long mRowId;
    private StoresDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_edit);
        setTitle(R.string.edit_store);
        confirmed=false;
        direccion = (EditText) findViewById(R.id.edit_title);
        confirmar = (CheckBox) findViewById(R.id.edit_confirm);
        tipo= (ToggleButton) findViewById(R.id.edit_tipo);
        valorar = (RatingBar) findViewById(R.id.edit_valoracion);
        foto = (Button) findViewById(R.id.load_foto);
        info = (EditText) findViewById(R.id.edit_info);
        comentar = (EditText) findViewById(R.id.edit_comentar);
        send = (Button) findViewById(R.id.but_send);
        mDbHelper = new StoresDbAdapter(this);
        accept = (Button) findViewById(R.id.but_accept);
        remove = (Button) findViewById(R.id.but_remove);
        

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(StoresDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(StoresDbAdapter.KEY_ROWID)
                                    : null;
        }
           
        populateFields();
        
        accept.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
        	    setResult(RESULT_OK);
				store.valorar(valorar.getRating());
        	    saveState();
        	    finish();
        	}

        });
        
        tipo.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
        	    setResult(RESULT_OK);
        	    if(tipo.isChecked()) type='B';
        	    else type='A';
        	    store.setType(type);
        	    saveState();
        	}

        });
        
        confirmar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked){
                   store.confirmar();
                   saveState();
                   populateFields();
                }
            }

        });
        /**
        valorar.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar valorar, float valoracion, boolean fromuser) {
				store.valorar(valoracion);
				saveState();
				populateFields();
			}
        	
        });*/
        
    }
    
    private void populateFields() {
    	mDbHelper.open();
        if (mRowId != null) {
            store= mDbHelper.getStore(mRowId);
            direccion.setText(store.getAddress());
            info.setText(store.getInfo());
            valorar.setRating(store.getVal());
            if (store.isConfirmed()){
            	confirmar.setVisibility(View.INVISIBLE);            	
            }
            else{
            	confirmar.setVisibility(View.VISIBLE);
            }
            char type = store.getType();
            if (type=='B')  tipo.setChecked(true);
            else tipo.setChecked(false);
        } else createStore();
        mDbHelper.close();
       
    }
    
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(StoresDbAdapter.KEY_ROWID, mRowId);
    }
    
    protected void onPause() {
        super.onPause();
    }
    
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {    	
        mDbHelper.open();
        if (mRowId == null) {
        	createStore();
        } else {
        	store.setAddress(direccion.getText().toString());
            store.setFoto("@drawable/store");
            store.setInfo(info.getText().toString());
            store.addComent(comentar.getText().toString());
            mDbHelper.updateNote(mRowId, store);
        }
        mDbHelper.close();
    }
	private void createStore(){
		store = new Store(type, direccion.getText().toString(), valorar.getRating(), info.getText().toString());
		long id = mDbHelper.createStore(store);
		if (id > 0) mRowId = id;
		}
}
