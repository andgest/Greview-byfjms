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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

public class SplashScreenTask extends AsyncTask<Void, Void, ArrayList<POI>>
{
	private static final File POIS_FILE = new File(Environment.getExternalStorageDirectory().getPath(),
			"POIs.osm");
	private Context context;
	
	public SplashScreenTask(Context ct)
	{
		context = ct;
	}
	
	protected void onPostExecute(ArrayList<POI> listPOIs) {
		Intent i = new Intent(context, MapViewer.class);
		i.putExtra("POI", listPOIs);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.startActivity(i);
	}

	@Override
	protected ArrayList<POI> doInBackground(Void... params)
	{
		ReadXMLFile reader = new ReadXMLFile();
		ArrayList<POI> listPOIs = new ArrayList<POI>(reader.readXMLFile(POIS_FILE));
		return listPOIs;
	}
}
