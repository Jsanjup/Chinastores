package com.app.chinastores;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import com.app.chinastores.R;

public class CommentActivity extends ListActivity{
	private StoresDbAdapter mDbHelper;
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.all_comments);
	     setTitle(R.string.comments);
	     StoresDbAdapter mDbHelper = new StoresDbAdapter(this);
	     mDbHelper.open();
	     
	     //SimpleAdapter ListadoAdapter=new SimpleAdapter(this, comentarios, R.layout.comment_row, );
	     //setListAdapter(ListadoAdapter);
	}
}
