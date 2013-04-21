package com.app.chinastores;

import com.app.chinastores.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends Activity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int ACTIVITY_READ=2;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int MAPA_ID= INSERT_ID+1;
    private static final int HELP_ID= MAPA_ID+1;
    private static final int EXIT_ID= HELP_ID+1;
    private static final int DELETE_ID = EXIT_ID + 1;
    private static final int EDIT_ID = DELETE_ID+1;

    private StoresDbAdapter mDbHelper;
    private ListView list;
    private CustomCursorAdapter stores;
    private boolean bazar;
    private Button alim;
    private Button baz;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bazar=false;
        setContentView(R.layout.activity_main);
        alim= (Button) findViewById(R.id.ButtonA);
        baz= (Button) findViewById(R.id.ButtonB);
        alim.setEnabled(bazar);
        baz.setEnabled(!bazar);
        mDbHelper = new StoresDbAdapter(this);
        list= (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(new OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
               verItem(position, id, false);
            }
        });
        alim.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	bazar=false;
                fillData(bazar);
                baz.setEnabled(true);
                alim.setEnabled(false);
            }
        });
        baz.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
             bazar=true;
             fillData(bazar);
             alim.setEnabled(true);
             baz.setEnabled(false);
            }
        });
        
        fillData(bazar);
        registerForContextMenu(list);
    }

    private void fillData(boolean bazar) {
        // Get all of the rows from the database and create the item list
    	mDbHelper.open();
        Cursor mNotesCursor = mDbHelper.fetchByType(bazar);

        // Now create a simple cursor adapter and set it to display
       stores =  new CustomCursorAdapter(this, mNotesCursor, (int) distancia(), bazar);
       list.setAdapter(stores);
       mDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        menu.add(0, MAPA_ID, 0, R.string.menu_map);
        menu.add(0, HELP_ID, 0, R.string.menu_help);
        menu.add(0, EXIT_ID, 0, R.string.menu_exit);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case MAPA_ID:
            	vermapa();
            	return true;
            case  HELP_ID:
            	ayuda();
            	return true;
            case EXIT_ID:
            	exit();
            	return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
    
    public void vermapa(){

        Intent i = new Intent(this, MapActivity.class);
        startActivityForResult(i, ACTIVITY_READ);
    }
    
    
    public void ayuda(){
        Intent i = new Intent(this, StoreEdit.class);
        startActivityForResult(i, ACTIVITY_READ);
        }
    
    public void exit(){
    	finish();
    }
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, EDIT_ID, 0, R.string.edit_store);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                delete(item);
                return true;
            case EDIT_ID:
            	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            	verItem(0, info.id, true);
            	return true;
        }
        return false;
    }
    
    private void delete(MenuItem item){
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	mDbHelper.open();
        mDbHelper.deleteNote(info.id);
        mDbHelper.close();
        fillData(bazar);
    }
    
    private void createNote() {
        Intent i = new Intent(this, StoreEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
   

    
    protected void onItemClick(ListView list, View v, int position, long id) {
        verItem(position, id, false);
    }
    
    public void verItem(int position, long id, boolean edit){
            Intent i = new Intent(this, StoreView.class);
            if (edit) i=new Intent(this, StoreEdit.class);
            i.putExtra(StoresDbAdapter.KEY_ROWID, id);
            startActivityForResult(i, ACTIVITY_READ);
        }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData(bazar);
    }
    
    public double distancia(){
    	return 0;
    }
}
