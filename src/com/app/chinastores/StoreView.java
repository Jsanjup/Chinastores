package com.app.chinastores;

import java.io.File;

import com.app.chinastores.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class StoreView extends Activity {

    private TextView direccion;
    private ImageView confirmed;
    private CheckBox confirmar;
    private RatingBar valoracion;
    private ImageView foto;
    private ToggleButton tipo;
    private TextView info;
    private EditText comentar;
    private Button send;
    private Button view_all;
    private Button edit;
    private static final int ACTIVITY_CREATE=1;
    private Long mRowId;
    private StoresDbAdapter mDbHelper;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_view);
        setTitle(R.string.view_store);

        direccion = (TextView) findViewById(R.id.title);
        confirmed = (ImageView) findViewById(R.id.imageconfirmed);
        confirmar = (CheckBox) findViewById(R.id.checkBox);
        valoracion = (RatingBar) findViewById(R.id.ver_valoracion);
        tipo = (ToggleButton) findViewById(R.id.ver_tipo);
        foto = (ImageView) findViewById(R.id.foto);
        info = (TextView) findViewById(R.id.info);
        comentar = (EditText) findViewById(R.id.view_comentar);
        send = (Button) findViewById(R.id.but_enviar);
        view_all = (Button) findViewById(R.id.but_ver);
        
        mDbHelper = new StoresDbAdapter(this);
        edit = (Button) findViewById(R.id.but_edit);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(StoresDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(StoresDbAdapter.KEY_ROWID)
                                    : null;
        }
           
        populateFields();
       
        edit.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
        	    setResult(RESULT_OK);
                editar();
        	}

        });
        confirmar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked){
                   confirmar();
                   populateFields();
                }
            }

        });
        
    }
    
    private void editar(){
        Intent i = new Intent(this, StoreEdit.class);
        i.putExtra(StoresDbAdapter.KEY_ROWID, mRowId);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void populateFields() {
    	mDbHelper.open();
        if (mRowId != null) {
            store= mDbHelper.getStore(mRowId);
            direccion.setText(store.getAddress());
            info.setText(store.getInfo());
            valoracion.setRating(store.getVal());
            if (store.isConfirmed()){
            	confirmed.setVisibility(View.VISIBLE);
            	confirmar.setVisibility(View.INVISIBLE);            	
            }
            else{
            	confirmed.setVisibility(View.INVISIBLE);
            	confirmar.setVisibility(View.VISIBLE);
            }
            char type = store.getType();
            if (type=='B')  tipo.setChecked(true);
            else tipo.setChecked(false);
            String imageFilePath = store.getFoto();
    		File image = new File(imageFilePath);
    		Uri uri= Uri.fromFile(image);
    		if(!uri.equals(Uri.EMPTY)) Log.e("Uri", "is empty");
    		foto.setImageURI(uri);
        } 
        mDbHelper.close();
    }
    
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saveState();
        outState.putSerializable(StoresDbAdapter.KEY_ROWID, mRowId);

    }
    
    protected void onPause() {
        super.onPause();

        //saveState();
    }
    
    protected void onStop() {
        super.onStop();
        //saveState();
    }
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void confirmar() {
    	store.confirmar();
        mDbHelper.open();
        if (mRowId == null) {
            return;
        } else {
            mDbHelper.updateNote(mRowId, store);
        }
        mDbHelper.close();
    }
}