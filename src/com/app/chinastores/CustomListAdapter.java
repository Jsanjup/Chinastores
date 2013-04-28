package com.app.chinastores;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
 
public class CustomListAdapter extends ArrayAdapter implements Filterable {
 
	private int distance;
	private LayoutInflater inflater;
	private Context context;
	private List<Store> lista;
	
    public CustomListAdapter(Context context, List<Store> list, int distance, boolean bazar){
       super(context,0,list);
       this.distance=distance;
       this.context= context;
       lista=list;
       inflater = LayoutInflater.from(context);
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
    	StoresDbAdapter mDbHelper = new StoresDbAdapter(context);
        mDbHelper.open();
        View retView = inflater.inflate(R.layout.notes_row, parent, false);
        bindView(retView, context, lista.get(position));
        mDbHelper.close();
        return retView;
    }
    /**
	@Override
    public void bindView(View view, Context context, Cursor cursor) {        
			
	        TextView direccion= (TextView) view.findViewById(R.id.direccion);
	        direccion.setText(cursor.getString(cursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_ADDRESS)));	    	
	        TextView distancia =(TextView) view.findViewById(R.id.row_distancia);
	        distancia.setText(""+distance);
	        ImageView confirmed = (ImageView) view.findViewById(R.id.row_tick);
	        confirmed.setVisibility(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_CONFIRMED))));        
	        RatingBar valoracion = (RatingBar) view.findViewById(R.id.row_valoracion);
	        valoracion.setRating(Float.parseFloat(cursor.getString(cursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_VALOR))));
		
		}*/
    
	private View bindView(View view, Context context, Store store) {        
		
        TextView direccion= (TextView) view.findViewById(R.id.direccion);
        direccion.setText(store.getAddress());	    	
        TextView distancia =(TextView) view.findViewById(R.id.row_distancia);
        distancia.setText(""+store.getDistancia());
        ImageView confirmed = (ImageView) view.findViewById(R.id.row_tick);
        if (store.isConfirmed())
        confirmed.setVisibility(View.VISIBLE);       
        else confirmed.setVisibility(View.INVISIBLE);
        RatingBar valoracion = (RatingBar) view.findViewById(R.id.row_valoracion);
        valoracion.setRating(store.getVal());
        return view;
	}
}
