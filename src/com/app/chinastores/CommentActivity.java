package com.app.chinastores;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import com.app.chinastores.R;
import android.widget.ListView;
	
	public class CommentActivity extends Activity{
		
	private StoresDbAdapter mDbHelper;
	private ListView lista;
	private String coment;
	private String[] comentarios ;
	private Long mRowId;
	private Store store;
	
	public void onCreate(Bundle savedInstanceState) {
	
		 super.onCreate(savedInstanceState);
		 Log.w("activity coment", "acti");
	     setContentView(R.layout.all_comments);
	     setTitle(R.string.comment);
	     //ListView lista = getListView();
	     lista= (ListView) findViewById(R.id.android_lista);
	     if(lista!= null)
	    	 Log.w("lista", "bn");
	     StoresDbAdapter mDbHelper = new StoresDbAdapter(this);
	     mDbHelper.open();
	     mRowId = (savedInstanceState == null) ? null :
	    	 (Long) savedInstanceState.getSerializable(StoresDbAdapter.KEY_ROWID);
	     if (mRowId == null) {
	           Bundle extras = getIntent().getExtras();
	           mRowId = extras != null ? extras.getLong(StoresDbAdapter.KEY_ROWID)
	                                    : null;
	     }
	     //comentarios =new String [] {"coment1", "coment2"};
	     comentarios = mDbHelper.fetchComments(mRowId);
	     Log.w("activity coment", "act comen  "+ comentarios);
	     lista.setAdapter(new ArrayAdapter<String>(this,R.layout.comment_row ,comentarios));
	     setResult(RESULT_OK);
	}
}
