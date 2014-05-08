package org.mapsforge.applications.android.samples;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class PointWebView extends Activity
{
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_point_web_view);
		webView = (WebView) findViewById(R.id.webView1);
		
		String titre = (String) this.getIntent().getExtras().get("Titre");
		String texte = (String) this.getIntent().getExtras().get("Texte");
		
		String html = "<html><body><h1>" + titre + 
					"</h1><p>" + texte  +"</p></body></html>";
		webView.loadData(html, "text/html", "UTF-8");
	}
}
