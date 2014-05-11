package org.mapsforge.applications.android.samples;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;

public class GPSTracker extends Service implements LocationListener {
	
	private final Context mContext;
    
    boolean isGPSEnabled = false; 
 
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude	
    
    //POIs
    private POI poiNerest = null;
    private ArrayList<POI> listPOI;
    
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; //20m
    
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
 
    protected LocationManager locationManager;
    
	public GPSTracker(Context context, ArrayList<POI> listPOI) 
	{
		this.mContext = context;
		getLocation();
		this.listPOI = listPOI;
	}

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
	
	public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
 
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            if (!isGPSEnabled) {
                if (!isGPSEnabled)
                {
                	this.showSettingsAlert();
                }
            } else {

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                this.latitude = location.getLatitude();
                                this.longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }

	 public void showSettingsAlert(){
	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
	      
	        // Setting Dialog Title
	        alertDialog.setTitle("GPS désactivé");
	  
	        // Setting Dialog Message
	        alertDialog.setMessage("Le GPS n'est pas activé. Voulez-vous accéder aux paramètres ?");
	  

	        alertDialog.setPositiveButton("Paramètres", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                mContext.startActivity(intent);
	            }
	        });
	  
	        // on pressing cancel button
	        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            }
	        });
	  
	        // Showing Alert Message
	        alertDialog.show();
	    }
	 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		Location newLocation = this.getLocation();
		//Si on constate un changement de position alors on vérifie le POI le plus proche et on lance une nouvelle dictée vocale dans le cas d'un changement de POI le plus proche.
		if(location.getLatitude() != latitude || location.getLongitude() != longitude) {
			//On met la nouvelle position à jours
			this.location = newLocation;
			this.latitude = newLocation.getLatitude();
			this.longitude = newLocation.getLongitude();

			POI oldPoiNerest = poiNerest;
			poiNerest = getNearestPOI(1000);
			//Si le POI le plus proche change, alors on lance un nouveau texte et une nouvelle vue ainsi qu'une notification
			if(oldPoiNerest != poiNerest) {

				// On broadcast le changement
				Intent intent = new Intent(this, GPSLocationReceiver.class);
				LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);

				
			}

		}
	}
	
	public POI getNearestPOI(double seuil) {

		double minDist = 9999999;
		int id = -1;

		System.out.println("(Double) null : "+(Double) null);
		for(int i=0; i<listPOI.size(); i++) {
			double tmp = getDistance(listPOI.get(i));
			if(minDist > tmp) {
				minDist = tmp;
				id = i;
			}
		}

		System.out.println("POI numero : "+id+" est le plus proche de nous, à une distance de : "+minDist);
		POI ret = null;
		if(minDist<seuil) {
			ret = listPOI.get(id);
		}
		return ret;
	}
	
	public double getDistance(POI aPOI) {
		return Math.sqrt(Math.pow(latitude - aPOI.getLat(), 2) + Math.pow((longitude - aPOI.getLon()), 2));
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
