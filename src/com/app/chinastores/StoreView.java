package com.app.chinastores;

import java.io.File;

import com.app.chinastores.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;
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
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_view);
        setTitle(R.string.view_store);
        mContext= this;
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
        view_all.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
                comentarios();
        	}
        });
        send.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
        	    envio();
        	}

        });
        confirmar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
           	 if (isChecked){
           	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
       		builder.setTitle(R.string.ask_confirm);
       		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
       	           public void onClick(DialogInterface dialog, int id) {
       	                   confirmar();
       	                   populateFields();
       	                	Toast.makeText(mContext,R.string.ad_confirm , Toast.LENGTH_SHORT).show();
       	                
       	           }
       	       });
       	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
       	           public void onClick(DialogInterface dialog, int id) {
       	               return;
       	           }
       });builder.create().show();
              
               }
           }

        });
        
    }
    
    private void editar(){
        Intent i = new Intent(this, StoreEdit.class);
        i.putExtra(StoresDbAdapter.KEY_ROWID, mRowId);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void envio(){
    	if(comentar.getText().toString().length()>=3){
    	mDbHelper.open();
    	store.addComent(comentar.getText().toString());
    	mDbHelper.updateNote(mRowId, store);
    	mDbHelper.close();
    	Toast.makeText(this,R.string.ad_sent , Toast.LENGTH_SHORT).show();
    	populateFields();
    	}
    }
    
    private void comentarios(){
        Intent i = new Intent(this, CommentActivity.class);
        i.putExtra(StoresDbAdapter.KEY_ROWID, mRowId);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if	(resultCode != RESULT_OK) {
    		finish();
    	}else populateFields();
    	super.onActivityResult(requestCode, resultCode, data);
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
            comentar.setText("");
            char type = store.getType();
            if (type=='B')  tipo.setChecked(true);
            else tipo.setChecked(false);
            if(!store.getFoto().equals(Store.pordefecto)){
    		File image = new File(getFilesDir()+"/"+store.getFoto());
    		Uri uri= Uri.fromFile(image);
    		foto.setImageURI(uri);}
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