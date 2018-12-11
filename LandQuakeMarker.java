
import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

public class LandQuakeMarker extends EarthquakeMarker {

	public LandQuakeMarker(PointFeature quake) {
		super(quake);
		isOnLand = true;
	}

	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.rect(x,y,getRadius(),getRadius());
	}

	public String getCountry() {
		return (String) getProperty("country");
	}		
}