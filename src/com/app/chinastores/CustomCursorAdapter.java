package com.app.chinastores;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
 
public class CustomCursorAdapter extends CursorAdapter {
 
	private int distance;
	private LayoutInflater inflater;
	private boolean bazar;
	
    public CustomCursorAdapter(Context context, Cursor c, int distance, boolean bazar){
       super(context, c,0);
       this.distance=distance;
       this.bazar=bazar;
       inflater = LayoutInflater.from(context);
    }
 
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        View retView = inflater.inflate(R.layout.notes_row, parent, false);
        bindView(retView, context, cursor);
        return retView;
    }
 
	@Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views
		
		if(bazar){
		aux(view, cursor, 'B');
		}else aux(view, cursor, 'A');
    }
		
		private void aux(View view, Cursor cursor, char clase){
			char tipo = cursor.getString(cursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_TYPE)).charAt(0);
			if(tipo ==clase){
	        TextView direccion= (TextView) view.findViewById(R.id.direccion);
	        direccion.setText(cursor.getString(cursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_ADDRESS)));
	    	
	        TextView distancia =(TextView) view.findViewById(R.id.row_distancia);
	        distancia.setText(""+distance);
	        ImageView confirmed = (ImageView) view.findViewById(R.id.row_tick);
	        confirmed.setVisibility(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_CONFIRMED))));        
	        RatingBar valoracion = (RatingBar) view.findViewById(R.id.row_valoracion);
	        valoracion.setRating(Float.parseFloat(cursor.getString(cursor.getColumnIndexOrThrow(StoresDbAdapter.KEY_VALOR))));
			}
		}
}
