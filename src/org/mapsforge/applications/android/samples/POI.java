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

public class POI {
	private int id;
	private String title;
	private double lat;
	private double lon;
	private String text;
	private ArrayList<File> images = new ArrayList();
	
	public POI(int id, String title, double lat, double lon, String text) {
		this.id = id;
		this.title = title;
		this.lat = lat;
		this.lon = lon;
		this.text = text;
	}
	
	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getText() {
		return text;
	}

	public ArrayList<File> getImages() {
		return images;
	}
	
}
