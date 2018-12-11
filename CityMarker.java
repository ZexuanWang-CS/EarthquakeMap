
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

public class CityMarker extends CommonMarker {

	public static final int TRI_SIZE = 5;  

	public CityMarker(Location location) {
		super(location);
	}

	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	}

	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.pushStyle();

		pg.triangle(x,y-TRI_SIZE,x-TRI_SIZE,y+TRI_SIZE,x+TRI_SIZE,y+TRI_SIZE);

		pg.popStyle();
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		String CityName = getStringProperty("name");
		String CountryName = getStringProperty("country");
		String Pop = getStringProperty("population");
		String City_all = CityName + ", " + CountryName + ", " + Pop + "M";
		pg.fill(20);
		pg.rect(1.01f*x, y-radius, 20*radius, 1.5f*radius);
		pg.fill(250);
		pg.text(City_all, 1.01f*x, y);
	}

	public String getCity()
	{
		return getStringProperty("name");
	}

	public String getCountry()
	{
		return getStringProperty("country");
	}

	public float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}	
}