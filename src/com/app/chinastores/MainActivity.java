package com.app.chinastores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.app.chinastores.R;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
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
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends Activity implements LocationListener{
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int ACTIVITY_READ=2;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int MAPA_ID= INSERT_ID+1;
    private static final int HELP_ID= MAPA_ID+1;
    private static final int EXIT_ID= HELP_ID+1;
    private static final int DELETE_ID = EXIT_ID + 1;
    private static final int EDIT_ID = DELETE_ID+1;
    
    private static final int DISTANCIA_MAX=50;
    
    private LocationManager locationManager;
	private String provider;
	
	private List<Store> tiendas;

    private StoresDbAdapter mDbHelper;
    private ListView list;
    private CustomCursorAdapter stores;
    private boolean bazar;
    private Button alim;
    private Button baz;
    private List<Float> distancias;
    
    private double lat = 40.45;
    private double lon = -3.65;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniciarLocalizador();
        bazar=false;
        distancias= new ArrayList<Float>();
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
                alim.setPressed(true);
                baz.setPressed(false);
            }
        });
        baz.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
             bazar=true;
             fillData(bazar);
             alim.setEnabled(true);
             baz.setEnabled(false);
             baz.setPressed(true);
             alim.setPressed(false);
            }
        });
        
        fillData(bazar);
        registerForContextMenu(list);
    }
    
    public void addTienda(Store store){
    	tiendas.add(store);
    }
    
    private void iniciarLocalizador(){
    	// Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			lat = (location.getLatitude());
			lon = (location.getLongitude());
			sacarDistancias();
		} else {
			sacarDistancias();
			Toast.makeText(this, R.string.provider_not_avalaible,
					Toast.LENGTH_SHORT).show();
		}
		
    }

    private void fillData(boolean bazar) {
        // Get all of the rows from the database and create the item list
    	mDbHelper.open();
    	Cursor mNotesCursor = mDbHelper.fetchByType(bazar);
    	//List<Store> lista = mDbHelper.fetchStoresByCursor(mDbHelper.fetchByType(bazar));
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

        Intent i = new Intent(this, MyMapActivity.class);
        startActivityForResult(i, ACTIVITY_READ);
    }
    
    
    public void ayuda(){
        Intent i = new Intent(this, InfoActivity.class);
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
	    Toast.makeText(this,R.string.ad_delete , Toast.LENGTH_SHORT).show();
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
    
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}
	
	public Address getAddressForLocation(Context context, Location location) throws IOException {

        if (location == null) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        int maxResults = 1;

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

        if (addresses.size() == 1) {
            return addresses.get(0);
        } else {
            return null;
        }
    }
	
    public void onLocationChanged(Location location) {
        // Bypass reverse-geocoding if the Geocoder service is not available on the
        // device. The isPresent() convenient method is only available on Gingerbread or above.
    	lat =  (location.getLatitude());
		lon = (location.getLongitude());
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent()) {
            (new ReverseGeocodingTask(this)).execute();
        //}
    }
    
    public void sacarDistancias(){
    	List<String> direcciones = new ArrayList<String>();//mDbHelper.getDirecciones();
    	String direccion1 = "C/ Hernandez de Tejada, 10";
    	String direccion2 ="C/Napoles, 21";
    	direcciones.add(direccion1);
    	direcciones.add(direccion2);
    	for (String direccion : direcciones){
    		direccion += ",Madrid, SPAIN";
    	}
    	new GeocodingTask(this).execute(direcciones);
    }

// AsyncTask encapsulating the reverse-geocoding API.  Since the geocoder API is blocked,
// we do not want to invoke it from the UI thread.
    private class ReverseGeocodingTask extends AsyncTask<Void, Void, Void> {
    Context mContext;

    public ReverseGeocodingTask(Context context) {
        super();
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addresses = null;
        try {
            // Call the synchronous getFromLocation() method by passing in the lat/long values.
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            // Format the first line of address (if available), city, and country name.
            String addressText = String.format("%s, %s, %s",
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getLocality(),
                    address.getCountryName());
            Toast.makeText(mContext, addressText,
					Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
    private class GeocodingTask extends AsyncTask<List<String>, Void, List<Float>> {
        Context mContext;

        public GeocodingTask(Context context) {
            super();
            mContext = context;
        }
        
        @Override
        protected List<Float> doInBackground(List<String>... addresses) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            if (geocoder== null) Log.e("geocoder", "no funciona el geocoder");
            List<String> ad = addresses[0];
            LatLng [] loc = new LatLng[ad.size()];
            distancias = new ArrayList<Float>();           
            try {
            	
                // Call the synchronous getFromLocation() method by passing in the lat/long values.
           	for (int i=0; i<loc.length; i++){
                Address direccion = geocoder.getFromLocationName(ad.get(i), 1).get(0);
                if (direccion== null) Log.e("direccion", "no funciona el geocoder");
                double latn= direccion.getLatitude();
                double lonn= direccion.getLongitude();
                loc[i] = new LatLng(latn, lonn);
                Log.w("myposition", lat+" "+ lon);
                Log.w("latlng", loc[i].toString());
                float[] distancia= new float[3];
                Location.distanceBetween(lat, lon, latn, lonn, distancia);
                Log.w("distancia", direccion.toString() + distancia);
                if(distancia[0] != 0 && distancia[0]<DISTANCIA_MAX) distancias.add((float) Math.round(distancia[0]/10)/100);
            	}
            } catch (IOException e) {
                Log.e("excepcion severa", e.getMessage());
            }
            if (distancias != null && distancias.size() > 0) {
            	Log.w("devuelve distancias", "esta linea se ejecuta");
              return distancias;
            }
            return null;
        
            }
    }
}
