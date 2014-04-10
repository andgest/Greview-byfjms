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

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.map.reader.header.FileOpenResult;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

/**
 * An application which demonstrates how to use overlays.
 */
public class OverlayMapViewer extends MapActivity implements LocationListener {
	// private static final GeoPoint BRANDENBURG_GATE = new GeoPoint(52.516273, 13.377725);
	// private static final GeoPoint CENTRAL_STATION = new GeoPoint(52.52498, 13.36962);
	// private static final GeoPoint VICTORY_COLUMN = new GeoPoint(52.514505, 13.350111);

	private static final File MAP_FILE = new File(Environment.getExternalStorageDirectory().getPath(),
			"rhone-alpes.map");// "berlin.map");
	private static final File POIS_FILE = new File(Environment.getExternalStorageDirectory().getPath(),
			"POIs.osm");
	public LocationManager locationManager;
	public Marker myPosition;
	public MapView mapView;

	public ArrayList<POI> listPOIs;

	private boolean resumeHasRun = false;

	// ListOverlay mOverlayList = new ListOverlay();
	// List<OverlayItem> overlayItems = mOverlayList.getOverlayItems();
	// mapView.getOverlays().add(mOverlayList);

	private Marker createMarker(int resourceIdentifier, GeoPoint geoPoint) {
		Drawable drawable = getResources().getDrawable(resourceIdentifier);
		return new Marker(geoPoint, Marker.boundCenterBottom(drawable));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		ReadXMLFile reader = new ReadXMLFile();
		listPOIs = new ArrayList<POI>(reader.readXMLFile(POIS_FILE));
		
		for(int i=0; i<listPOIs.size(); i++) {
			overlayItems.add(createMarker(R.drawable.marker_red, new GeoPoint(listPOIs.get(i).getLat(), listPOIs.get(i).getLon())));
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		myPosition.setGeoPoint(new GeoPoint(location.getLatitude(), location.getLongitude()));
		mapView.redraw();
		// Pour centrer la map
		// mapView.getMapViewPosition().setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
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
}
