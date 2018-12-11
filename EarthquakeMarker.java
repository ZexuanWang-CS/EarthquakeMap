
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;
import java.util.*;

public abstract class EarthquakeMarker extends CommonMarker implements Comparable<EarthquakeMarker>
{

	protected boolean isOnLand;
	protected float radius;
	protected static final float kmPerMile = 1.6f;
	public static final float THRESHOLD_MODERATE = 5;
	public static final float THRESHOLD_LIGHT = 4;
	public static final float THRESHOLD_INTERMEDIATE = 70;
	public static final float THRESHOLD_DEEP = 150;
	public abstract void drawEarthquake(PGraphics pg, float x, float y);
	public EarthquakeMarker (PointFeature feature) 
	{
		super(feature.getLocation());
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2*magnitude );
		setProperties(properties);
		this.radius = 1.75f*getMagnitude();
	}

	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.pushStyle();
		colorDetermine(pg);		
		drawEarthquake(pg, x, y);
		String age = getStringProperty("age");
		if ("Past Hour".equals(age) || "Past Day".equals(age)) {
			pg.strokeWeight(2);
			int buffer = 2;
			pg.line(x-(radius+buffer), 
					y-(radius+buffer), 
					x+radius+buffer, 
					y+radius+buffer);
			pg.line(x-(radius+buffer), 
					y+(radius+buffer), 
					x+radius+buffer, 
					y-(radius+buffer));
		}
		pg.popStyle();	
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y)
	{
		String QuakeTitle = (String) getTitle();
		String QuakeLoc = getLocation().toString();
		String Quake_all = QuakeTitle + ", " + QuakeLoc;
		pg.fill(20);
		pg.rect(1.01f*x, y-1.3f*radius, 50*radius, 2f*radius);
		pg.fill(250);
		pg.text(Quake_all, 1.01f*x, y);
	}

	public double threatCircle() {	
		double miles = 20.0f * Math.pow(1.8, 2*getMagnitude()-5);
		double km = (miles * kmPerMile);
		return km;
	}

	private void colorDetermine(PGraphics pg) {
		float depth = Float.parseFloat(properties.get("depth").toString());
		if (depth<70) {
			pg.fill(255, 255, 0);
		}
		else if (depth>=150) {
			pg.fill(255, 0, 0);
		}
		else {
			pg.fill(0, 0, 255);
		}
	}

	public int compareTo(EarthquakeMarker marker) {
		int diff = (int) ((this.getMagnitude() - marker.getMagnitude())*10000);
		return (-1)*diff;
	}

	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}

	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}

	public String getTitle() {
		return (String) getProperty("title");	

	}

	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}

	public boolean isOnLand()
	{
		return isOnLand;
	}	
}