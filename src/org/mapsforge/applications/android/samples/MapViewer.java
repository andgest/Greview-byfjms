/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.applications.android.samples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.map.reader.header.FileOpenResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * An application which demonstrates how to use overlays.
 */

public class MapViewer extends MapActivity implements LocationListener, TextToSpeech.OnInitListener {
	private static final File MAP_FILE = new File(Environment.getExternalStorageDirectory().getPath(),
			"rhone-alpes.map");
	public LocationManager locationManager;
	public Marker myPosition = null;
	public MapView mapView;

	private ArrayList<POI> listPOIs;
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private POI poiNerest = null;

	private TextToSpeech mTts;

	private boolean resumeHasRun = false;
	private OnMarkerClickListener onMarkerClickListener;

	private Marker createMarker(int resourceIdentifier, GeoPoint geoPoint) {
		Drawable drawable = getResources().getDrawable(resourceIdentifier);
		return new Marker(geoPoint, Marker.boundCenterBottom(drawable));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.listPOIs = (ArrayList<POI>) this.getIntent().getExtras().get("POI");
		mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		FileOpenResult fileOpenResult = mapView.setMapFile(MAP_FILE);

		if (!fileOpenResult.isSuccess()) {
			Toast.makeText(this, fileOpenResult.getErrorMessage(), Toast.LENGTH_LONG).show();
			finish();
		}
		setContentView(mapView);

		myPosition = createMarker(R.drawable.marker_green, new GeoPoint(0, 0));			//Position GPS par la puce GPS
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);	//Position GPS selon Wifi et réseau;

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);

		ListOverlay listOverlay = new ListOverlay();
		List<OverlayItem> overlayItems = listOverlay.getOverlayItems();

		overlayItems.add(myPosition);
		setPOIOnMap(overlayItems);
		mapView.getOverlays().add(listOverlay);
	}

	public void setPOIOnMap(List<OverlayItem> overlayItems) {

		for(int i=0; i<listPOIs.size(); i++) {
			Marker marker = createMarker(R.drawable.marker_red, new GeoPoint(listPOIs.get(i).getLat(), listPOIs.get(i).getLon()));
			overlayItems.add(marker);
			markers.add(marker);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onLocationChanged(Location location) {
		//Si aucune geolocalisation n'était définit avant alors on centre la vue sur notre position
		if(myPosition.getGeoPoint().latitude == 0.0 && myPosition.getGeoPoint().longitude == 0.0) {
			mapView.getMapViewPosition().setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
		}
		//Si on constate un changement de position alors on vérifie le POI le plus proche et on lance une nouvelle dictée vocale dans le cas d'un changement de POI le plus proche.
		if(location.getLatitude() != myPosition.getGeoPoint().latitude || location.getLongitude() != myPosition.getGeoPoint().longitude) {
			//On met la nouvelle position à jours
			myPosition.setGeoPoint(new GeoPoint(location.getLatitude(), location.getLongitude()));

			POI oldPoiNerest = poiNerest;
			poiNerest = getNearestPOI(1000);
			//Si le POI le plus proche change, alors on lance un nouveau texte et une nouvelle vue ainsi qu'une notification
			if(oldPoiNerest != poiNerest) {
				System.out.println("Le point le plus proche est le n°"+poiNerest.getId()+" "+poiNerest.getTitle());

				//On lit le texte du POI le plus proche
				mTts = new TextToSpeech(this, this);

				//On créer une nouvelle notification
				Intent intent = new Intent(this, NotificationReceiverActivity.class);
				PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

				Notification notif = new Notification.Builder(this)
		        .setContentTitle("POI "+poiNerest.getId()+" : "+poiNerest.getTitle())
		        .setContentText("Appuyer ici pour avoir plus d'information").setSmallIcon(R.drawable.ic_launcher)
		        .setContentIntent(pIntent)
		        .build();

				NotificationManager notificationManager = 
						  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notif.flags |= Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(0, notif);
				
				Intent webViewIntent = new Intent(this, PointWebView.class);
				webViewIntent.putExtra("Titre", poiNerest.getTitle());
				webViewIntent.putExtra("Texte", poiNerest.getText());
				this.startActivity(webViewIntent);
				
				
				/*Toast toast = Toast.makeText(getBaseContext(), poiNerest.getTitle()+"\n"+poiNerest.getText(), Toast.LENGTH_LONG);
				toast.show();*/
			}

		}
		mapView.redraw();
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!resumeHasRun) {
			resumeHasRun = true;
			return;
		}

	}

	public POI getNearestPOI(double seuil) {

		double minDist = 9999999;
		int id = -1;

		System.out.println("(Double) null : "+(Double) null);
		for(int i=0; i<listPOIs.size(); i++) {
			double tmp = getDistance(listPOIs.get(i));
			if(minDist > tmp) {
				minDist = tmp;
				id = i;
			}
		}

		System.out.println("POI numero : "+id+" est le plus proche de nous, a une distance de : "+minDist);
		POI ret = null;
		if(minDist<seuil) {
			ret = listPOIs.get(id);
		}
		return ret;
	}

	public double getDistance(POI aPOI) {
		return Math.sqrt(Math.pow(myPosition.getGeoPoint().latitude - aPOI.getLat(), 2) + Math.pow((myPosition.getGeoPoint().longitude - aPOI.getLon()), 2));
	}

	@Override
	public void onInit(int status) {
		// vérification de la disponibilité  de la synthèse vocale.
		if (status == TextToSpeech.SUCCESS) {
			//le choix de la langue ici français
			int result = mTts.setLanguage(Locale.FRANCE);
			// vérification ici si cette langue est supporté par le terminal et si elle existe
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				//renvoi une erreur sur la console logcat.
				Log.e("Greview", "Language is not available.");
			} else {
				mTts.speak(poiNerest.getTitle()+".\n"+poiNerest.getText(), TextToSpeech.QUEUE_FLUSH,  null);
				//ParleandroidPhone();
			}
		} else {
			// si la synthèse vocal n'est pas disponible
			Log.e("Greview", "Could not initialize TextToSpeech.");
		}
	}


	public class NotificationReceiverActivity extends Activity {
		  @Override
		  protected void onCreate(Bundle savedInstanceState) {
			  super.onCreate(savedInstanceState);
			  setContentView(R.layout.activity_samples);
		  }
	} 
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		System.out.println("LLELELELE");
		if(onMarkerClickListener == null) {
			return true;
		}
		
		if(event.getAction() != MotionEvent.ACTION_DOWN) {
			return true;
		}
		
		int width, height, left, top;
		
		for(Marker marker : markers) {

	        width = marker.getDrawable().getIntrinsicWidth();
	        height = marker.getDrawable().getIntrinsicHeight();
	        
			left = marker.getPixelX() - width/2;
	        top = marker.getPixelY() - height/2;

			if((event.getX(0) >= left) && (event.getY(0) >= top) && 
					(event.getX(0) <= left + width) && (event.getY(0) <= top + height))
	        {
				onMarkerClickListener.onMarkerClick(marker);
	        }
		}
		
		return true;
	}
	
	public void setOnMarkerClickListener(OnMarkerClickListener onMarkerClickListener) {
		this.onMarkerClickListener = onMarkerClickListener;
	}

}