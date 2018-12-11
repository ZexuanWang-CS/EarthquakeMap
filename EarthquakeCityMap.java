
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.EsriProvider;
import de.fhpotsdam.unfolding.providers.GeoMapApp;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.Yahoo;
import de.fhpotsdam.unfolding.utils.MapUtils;
import zwang.CityMarker;
import zwang.CommonMarker;
import zwang.EarthquakeMarker;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PGraphics;

public class EarthquakeCityMap extends PApplet {

	private static final long serialVersionUID = 1L;
	private static final boolean offline = false;
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	private UnfoldingMap map;
	private List<Marker> cityMarkers;
	private List<Marker> quakeMarkers;
	private List<Marker> countryMarkers;
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	private EarthquakeMarker lastClicked_a;

	public void setup() {		
		size(1400, 800, OPENGL);
		if (offline) {
			map = new UnfoldingMap(this, 300, 50, 700, 700, new MBTilesMapProvider(mbTilesString));
			earthquakesURL = "2.5_week.atom";
		}
		else {
			map = new UnfoldingMap(this, 300, 50, 900, 700, new Microsoft.RoadProvider());
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		earthquakesURL = "test2.atom";
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature cities_ele : cities) {
			cityMarkers.add(new CityMarker(cities_ele)); // see CityMarker.java
		}
		List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
		quakeMarkers = new ArrayList<Marker>();
		for(PointFeature earthquakes_ele : earthquakes) {
			if(isLand(earthquakes_ele)) {
				quakeMarkers.add(new LandQuakeMarker(earthquakes_ele)); // see LandQuakeMarker.java
			}
			else {
				quakeMarkers.add(new OceanQuakeMarker(earthquakes_ele)); // see OceanQuakeMarker.java
			}
		}
		printQuakes();
		map.addMarkers(quakeMarkers);
		map.addMarkers(cityMarkers);
		sortAndPrint(100);
	}

	public void draw() {
		background(0);
		map.draw();
		addKey();
	}

	private void sortAndPrint(int numToPrint) {
		ArrayList<EarthquakeMarker> MagSort = new ArrayList();
		for (Marker quakeMarkers_ele : quakeMarkers) {
			MagSort.add((EarthquakeMarker) quakeMarkers_ele);
		}
		Collections.sort(MagSort);
		if(numToPrint <= MagSort.size()) {
			int i;
			for(i=0; i < numToPrint; i++) {
				System.out.println(MagSort.get(i).getMagnitude()+" "+MagSort.get(i).getTitle()+" "+MagSort.get(i).getLocation());
			}
		}
		else {
			int i;
			for(i=0; i < MagSort.size(); i++) {
				System.out.println(MagSort.get(i).getMagnitude()+" "+MagSort.get(i).getTitle()+" "+MagSort.get(i).getLocation());
			}
		}
	}

	@Override
	public void mouseMoved() {
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
	}

	private void selectMarkerIfHover(List<Marker> markers) {
		for(Marker quakeMarkers_ele : quakeMarkers) {
			if(quakeMarkers_ele.isInside((UnfoldingMap) map, mouseX, mouseY)) {
				lastSelected = (CommonMarker) quakeMarkers_ele;
				quakeMarkers_ele.isSelected();
				quakeMarkers_ele.setSelected(true);
				return;
			}
		}
		for(Marker cityMarkers_ele : cityMarkers) {
			if(cityMarkers_ele.isInside((UnfoldingMap) map, mouseX, mouseY)) {
				lastSelected = (CommonMarker) cityMarkers_ele;
				lastSelected.isSelected();
				lastSelected.setSelected(true);
				cityMarkers_ele.isSelected();
				cityMarkers_ele.setSelected(true);
				return;
			}
		}
	}

	@Override
	public void mouseClicked() {
		if(lastClicked != null) {
			lastClicked.setSelected(false);
			lastClicked = null;
			unhideMarkers();
		}
		else {
			selectMarkerIfClick(quakeMarkers);
			selectMarkerIfClick(cityMarkers);
		}
	}

	private void selectMarkerIfClick(List<Marker> markers) {
		boolean flag_q = false;
		boolean flag_c = false;
		for(Marker quakeMarkers_ele : quakeMarkers) {
			if(quakeMarkers_ele.isInside((UnfoldingMap) map, mouseX, mouseY)) {
				lastClicked = (CommonMarker) quakeMarkers_ele;
				lastClicked_a = (EarthquakeMarker) quakeMarkers_ele;
				lastClicked.isSelected();
				lastClicked.setClicked(true);
				lastClicked.setHidden(false);
				quakeMarkers_ele.isSelected();
				quakeMarkers_ele.setSelected(true);
				quakeMarkers_ele.setHidden(false);
				flag_q = true;
			}
			else {
				quakeMarkers_ele.setHidden(true);
			}
		}
		for(Marker cityMarkers_ele : cityMarkers) {
			if(cityMarkers_ele.isInside((UnfoldingMap) map, mouseX, mouseY)) {
				lastClicked = (CommonMarker) cityMarkers_ele;
				lastClicked.isSelected();
				lastClicked.setClicked(true);
				lastClicked.setHidden(false);
				cityMarkers_ele.isSelected();
				cityMarkers_ele.setSelected(true);
				cityMarkers_ele.setHidden(false);
				flag_c = true;
			}
			else {
				cityMarkers_ele.setHidden(true);
			}
		}
		if(!(flag_q || flag_c)) {
			for(Marker quakeMarkers_ele : quakeMarkers) {
				quakeMarkers_ele.setHidden(false);
			}
			for(Marker cityMarkers_ele : cityMarkers) {
				cityMarkers_ele.setHidden(false);
			}
		}
		if(flag_q) {
			for(Marker cityMarkers_ele : cityMarkers) {
				double Dist = cityMarkers_ele.getDistanceTo(lastClicked.getLocation());
				if(Dist < lastClicked_a.threatCircle()) {
					cityMarkers_ele.setHidden(false);
				}
			}
		}
		if(flag_c) {
			for(Marker quakeMarkers_ele : quakeMarkers) {
				lastClicked_a = (EarthquakeMarker) quakeMarkers_ele;
				double Dist = quakeMarkers_ele.getDistanceTo(lastClicked.getLocation());
				if(Dist < lastClicked_a.threatCircle()) {
					quakeMarkers_ele.setHidden(false);
				}
			}
		}
	}

	private void unhideMarkers() {
		for(Marker quakeMarkers_ele : quakeMarkers) {
			quakeMarkers_ele.setHidden(false);
		}

		for(Marker cityMarkers_ele : cityMarkers) {
			cityMarkers_ele.setHidden(false);
		}
	}

	public void keyPressed() {
		if(lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		}
		if(lastClicked != null) {
			lastClicked.setSelected(false);
			lastClicked = null;
			unhideMarkers();
		}
		citySouthHemi(cityMarkers);
		quakeNorthHemi(quakeMarkers);
	}

	public void citySouthHemi(List<Marker> markers) {
		for(Marker markers_ele : markers) {
			if(markers_ele.getLocation().getLat() > 0f) {
				markers_ele.setHidden(false);
			}
			else {
				markers_ele.setHidden(true);
			}
		}
	}

	public void quakeNorthHemi(List<Marker> markers) {
		for(Marker markers_ele : markers) {
			if(markers_ele.getLocation().getLat() <= 0f) {
				markers_ele.setHidden(false);
			}
			else {
				markers_ele.setHidden(true);
			}
		}
	}

	private void addKey() {
		fill(255, 250, 240);
		rect(25, 50, 160, 250);

		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", 50, 75);

		fill(255, 255, 255);
		triangle(60, 100-CityMarker.TRI_SIZE, 60-CityMarker.TRI_SIZE, 
				100+CityMarker.TRI_SIZE, 60+CityMarker.TRI_SIZE, 
				100+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", 75, 100);

		text("Land Quake", 75, 120);
		text("Ocean Quake", 75, 140);
		text("Size ~ Magnitude", 75, 160);

		fill(255, 255, 255);
		ellipse(60, 120, 10, 10);
		rect(55, 135, 10, 10);

		fill(color(255, 255, 0));
		ellipse(60, 190, 12, 12);
		fill(color(0, 0, 255));
		ellipse(60, 210, 12, 12);
		fill(color(255, 0, 0));
		ellipse(60, 230, 12, 12);

		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", 75, 190);
		text("Intermediate", 75, 210);
		text("Deep", 75, 230);
		text("Past hour", 75, 250);

		fill(255, 255, 255);
		int centerx = 60;
		int centery = 250;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
	}

	private boolean isLand(PointFeature earthquake) {
		boolean flag=false;
		for (Marker countryMarkers_ele : countryMarkers) {
			if(isInCountry((PointFeature) earthquake, (Marker) countryMarkers_ele)) {
				flag=true;
				System.out.println("It is "+flag);
				return flag;
			}
			else {
				flag=false;
			}
		}
		System.out.println("It is "+flag);
		return flag;
	}

	private void printQuakes() {
		int count_land_all = 0;
		for(Marker countryMarkers_ele : countryMarkers) {
			String name = (String) countryMarkers_ele.getProperty("name");
			int count_land = 0;
			for(Marker quakeMarkers_ele : quakeMarkers) {
				if ((String) name == (String) quakeMarkers_ele.getProperty("country")) {
					count_land++;
				}
			}
			if(count_land != 0) {
				System.out.println(name+": "+count_land);
				count_land_all = count_land_all+count_land;
			}
		}
		int count_ocean = quakeMarkers.size()-count_land_all;
		System.out.println("OCEAN QUAKES"+": "+count_ocean);
	}

	private boolean isInCountry(PointFeature earthquake, Marker country) {
		Location checkLoc = earthquake.getLocation();
		if(country.getClass() == MultiMarker.class) {
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
					return true;
				}
			}
		}
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			return true;
		}
		return false;
	}
}