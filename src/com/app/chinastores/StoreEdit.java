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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.app.chinastores.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Toast;
import android.widget.ToggleButton;

public class StoreEdit extends Activity {

    private EditText direccion;
    private CheckBox confirmar;
    private RatingBar valorar;
    private Button foto;
    private ToggleButton tipo;
    private TextView info;
    private ImageView imagen;
    private EditText comentar;
    private Button send;
    private Button accept;
    private Button remove;
    private int nValor;
    private boolean confirmed;
    private char type;
    private final static int TAKE_FOTO=1337;
    private final static int PICK_FROM_GALLERY=1;
    private File image;
    private final static int RESULT_DELETE=3;
    
    private Store store;
    
    private Long mRowId;
    private StoresDbAdapter mDbHelper;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_edit);
        mContext=this;
        setTitle(R.string.edit_store);
        confirmed=false;
        type='A';
        direccion = (EditText) findViewById(R.id.edit_title);
        confirmar = (CheckBox) findViewById(R.id.edit_confirm);
        tipo= (ToggleButton) findViewById(R.id.edit_tipo);
        valorar = (RatingBar) findViewById(R.id.edit_valoracion);
        foto = (Button) findViewById(R.id.load_foto);
        info = (EditText) findViewById(R.id.edit_info);
        comentar = (EditText) findViewById(R.id.edit_comentar);
        imagen = (ImageView) findViewById(R.id.foto_view);
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
            remove.setEnabled(false);
            confirmar.setEnabled(false);
        }else {
        	remove.setEnabled(true);
        	confirmar.setEnabled(true);
        }
           
        populateFields();
		image= new File(getFilesDir()+"/"+store.getFoto());
        accept.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
        	    setResult(RESULT_OK);
				store.valorar(valorar.getRating());
        	    saveState();
        	    Toast.makeText(mContext,"La tienda ha sido modificada" , Toast.LENGTH_SHORT).show();
        	    finish();
        	}

        });
        
        remove.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        		builder.setTitle("ÀSeguro que desea eliminar la tienda?");
        		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   setResult(RESULT_DELETE);
        	        	    mDbHelper.open();
        	                if (mRowId == null) {
        	                	return;
        	                } else {
        	                	mDbHelper.deleteNote(mRowId);
        	                	Toast.makeText(mContext,"La tienda ha sido borrada" , Toast.LENGTH_SHORT).show();
        	                }
        	                mDbHelper.close();
        	        	    finish();
        	           }
        	       });
        	builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	               return;
        	           }

        });builder.create().show();
        	}

        });
        tipo.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
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
            	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        		builder.setTitle("ÀConfirma la veracidad de los datos?");
        		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                   store.confirmar();
        	                   saveState();
        	                   populateFields();
        	                	Toast.makeText(mContext,"Tienda confirmada" , Toast.LENGTH_SHORT).show();
        	                
        	           }
        	       });
        	builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	               confirmar.setChecked(false);
        	           }
        });builder.create().show();
               
                }
            }

        });
        send.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
        	    envio();
        	}

        });
        foto.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        		builder.setPositiveButton("Importar de galer’a", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	             pickFromGallery();
        	           }
        	       });
        	builder.setNegativeButton("Hacer nueva foto", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	               dispatchTakePictureIntent();
        	           }

        });builder.create().show();
        	}
        });
        
    }
    
    private void envio(){
    	if(comentar.getText().toString().length()>=3){store.addComent(comentar.getText().toString());
    	saveState();
    	Toast.makeText(mContext,"Comentario enviado" , Toast.LENGTH_SHORT).show();
    	populateFields();}
    }
    
    private void dispatchTakePictureIntent() {
    	
    			Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    			//i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,Uri.fromFile(image));
    			startActivityForResult(i, TAKE_FOTO);
    }
    
    private void pickFromGallery(){
             Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
             photoPickerIntent.setType("image/jpg");
             //photoPickerIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/Pictures/image.jpg"));
             startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY);
      }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if	(resultCode!= RESULT_OK) {
    		Toast.makeText(this, "La importaci—n fall—",Toast.LENGTH_SHORT).show();
    	}
       switch(requestCode){
       case TAKE_FOTO:
    	handleSmallCameraPhoto(data);
       	saveState();
       	break;
       case PICK_FROM_GALLERY:
    	   Uri selectedImageUri = data.getData();
           imagen.setImageURI(selectedImageUri);
           copyFile(selectedImageUri);
           foto.setVisibility(View.INVISIBLE);
           imagen.setVisibility(View.VISIBLE);
           foto.setEnabled(false);
           saveState();
           break;
       }
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void copyFile(Uri uri){
    	File src = new File(getRealPathFromURI(uri));
    	try{
    	FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(image); 

        byte[] buffer = new byte[1024];

        int length;
        //copy the file content in bytes 
        while ((length = in.read(buffer)) > 0){
           out.write(buffer, 0, length);
        }

        in.close();
        out.close();
    	}catch(Exception e){
    		Log.e("Excepcion", e.getMessage());
    	}
    }
    
    
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
    private void handleSmallCameraPhoto(Intent intent) {
        Bitmap mImageBitmap = (Bitmap) intent.getExtras().get("data");
        try {
            FileOutputStream out = openFileOutput(image.getPath(), Context.MODE_PRIVATE);
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 150, out);
             } catch (Exception e) {
            e.printStackTrace();
             }
		Uri uri= Uri.fromFile(image);
        foto.setVisibility(View.INVISIBLE);
        foto.setEnabled(false);
        imagen.setImageURI(uri);
        imagen.setVisibility(View.VISIBLE);
        saveState();
    }
    
    private void populateFields() {
    	mDbHelper.open();
        if (mRowId != null) {
        	remove.setEnabled(true);
        	confirmar.setEnabled(true);
            store= mDbHelper.getStore(mRowId);
            direccion.setText(store.getAddress());
            info.setText(store.getInfo());
            valorar.setRating(store.getVal());
            comentar.setText("");
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
        //saveState();
        outState.putSerializable(StoresDbAdapter.KEY_ROWID, mRowId);
    }
    
    protected void onPause() {
        super.onPause();
        //saveState();
    }
    
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {    	
        mDbHelper.open();
        if (mRowId == null) {
        	createStore();
        	long id = mDbHelper.createStore(store);
    		if (id > 0) mRowId = id;
        } else {
        	store.setAddress(direccion.getText().toString());
            store.setInfo(info.getText().toString());
            mDbHelper.updateNote(mRowId, store);
        }
        mDbHelper.close();
    }
    
	private void createStore(){
		confirmar.setEnabled(false);
		store = new Store(type, direccion.getText().toString(), valorar.getRating(), info.getText().toString());
	}
}
