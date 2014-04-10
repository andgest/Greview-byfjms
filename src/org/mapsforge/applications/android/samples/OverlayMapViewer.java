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
			"pois.osm");
	public LocationManager locationManager;
	public Marker myPosition;
	public MapView mapView;

	public ArrayList<Marker> listPOI = new ArrayList();

	private boolean resumeHasRun = false;

	/*
	private static Circle createCircle() {
		Paint paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintFill.setStyle(Paint.Style.FILL);
		paintFill.setColor(Color.BLUE);
		paintFill.setAlpha(64);
		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(Color.BLUE);
		paintStroke.setAlpha(128);
		paintStroke.setStrokeWidth(3);
		return new Circle(CENTRAL_STATION, 200, paintFill, paintStroke);
	}

	private static Polygon createPolygon() {
		List<GeoPoint> geoPoints = Arrays.asList(VICTORY_COLUMN, CENTRAL_STATION, BRANDENBURG_GATE);
		PolygonalChain polygonalChain = new PolygonalChain(geoPoints);
		Paint paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintFill.setStyle(Paint.Style.FILL);
		paintFill.setColor(Color.YELLOW);
		paintFill.setAlpha(96);
		paintFill.setStrokeCap(Cap.ROUND);
		paintFill.setStrokeJoin(Paint.Join.ROUND);
		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(Color.GRAY);
		paintStroke.setAlpha(192);
		paintStroke.setStrokeWidth(5);
		paintStroke.setStrokeCap(Cap.ROUND);
		paintStroke.setStrokeJoin(Paint.Join.ROUND);
		return new Polygon(Arrays.asList(polygonalChain), paintFill, paintStroke);
	}

	private static Polyline createPolyline() {
		List<GeoPoint> geoPoints = Arrays.asList(BRANDENBURG_GATE, VICTORY_COLUMN);
		PolygonalChain polygonalChain = new PolygonalChain(geoPoints);
		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(Color.MAGENTA);
		paintStroke.setAlpha(128);
		paintStroke.setStrokeWidth(7);
		paintStroke.setPathEffect(new DashPathEffect(new float[] { 25, 15 }, 0));
		return new Polyline(polygonalChain, paintStroke);
	}
	*/

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

		// INUTILE ! ----
		/*
		 * Circle circle = createCircle(); Polygon polygon = createPolygon(); Polyline polyline = createPolyline();
		 */
		// --------------

		// Marker marker2 = createMarker(R.drawable.marker_green, BRANDENBURG_GATE);
		// GeoPoint INIT = new GeoPoint(45.184042, 5.725933);

		myPosition = createMarker(R.drawable.marker_green, new GeoPoint(0, 0));
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// LocationManager.GPS_PROVIDER; = Position GPS par la puce GPS
		// LocationManager.NETWORK_PROVIDER; = Position GPS selon Wifi et réseau;

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this); // <-- inutile car la
		// nexus 7 n'a pas de SIM et pas de wifi dans la rue

		ListOverlay listOverlay = new ListOverlay();
		List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
		// INUTILE ! ----
		/*
		 * overlayItems.add(circle); overlayItems.add(polygon); overlayItems.add(polyline);
		 */
		// --------------
		// overlayItems.add(marker1);

		overlayItems.add(myPosition);
		mapView.getOverlays().add(listOverlay);
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
