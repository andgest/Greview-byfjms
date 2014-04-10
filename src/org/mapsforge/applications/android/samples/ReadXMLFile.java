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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 
public class ReadXMLFile {
	public ArrayList<POI> readXMLFile(File fXmlFile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
		 
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
		 
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName()); //osm
		 
			NodeList listePOIs = doc.getElementsByTagName("node");
			
			ArrayList<POI> listPOIs = new ArrayList<POI>();
		 
			for (int i = 0; i < listePOIs.getLength(); i++) {
				
				Node nNode = listePOIs.item(i);
		 
				System.out.println("\n" + nNode.getNodeName() + " : POI n°"+i);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					System.out.println("id : " + eElement.getAttribute("id"));
					System.out.println("title: " + eElement.getAttribute("title"));
					System.out.println("lat : " + eElement.getAttribute("lat"));
					System.out.println("lon : " + eElement.getAttribute("lon"));
					String text = "";
					if(eElement.getElementsByTagName("text").item(0) != null) {
						text = eElement.getElementsByTagName("text").item(0).getTextContent();
						System.out.println("text : " + text);
					}
					POI poi = new POI(Integer.parseInt(eElement.getAttribute("id")), eElement.getAttribute("title"), Double.parseDouble(eElement.getAttribute("lat")), Double.parseDouble(eElement.getAttribute("lon")), text);
					listPOIs.add(poi);
				}
			}
			return listPOIs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
  
}