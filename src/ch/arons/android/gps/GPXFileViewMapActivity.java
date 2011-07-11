package ch.arons.android.gps;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;

import ch.arons.android.gps.io.file.GPXWayPoint;
import ch.arons.android.gps.io.file.GpxReader;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GPXFileViewMapActivity extends MapActivity {
	private static final String COMPONENT = "GPXFileViewMapActivity";
    
	Bitmap bmp;
	LinearLayout linearLayout;
	MapView mapView;
	MapController mc;
	
	public static String fileName = null;
	List<GPXWayPoint> wayPoints = null;
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.file_view_map);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
		
		parseWayPoints();
		
		
//		//...
//		MapController mc = mapView.getController();
//		mc.animateTo(p);
//		mc.setZoom(17); 
 
        //---Add a location marker---
        MapOverlay mapOverlay = new MapOverlay();
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);        
 
        mapView.invalidate();
	}

	
	
	private void parseWayPoints() {
		wayPoints = null;
		
		if(fileName == null) 
			return;
		
		File root = Environment.getExternalStorageDirectory();
		File dir = new File(root, "/GPSTrac/");
		File file = new File(dir, fileName);
		
		try {
            wayPoints = GpxReader.readTrack(file);
        } catch (IOException e) {
            Log.e(COMPONENT,e.getMessage(),e);
            return ;
        }
    }



	class MapOverlay extends com.google.android.maps.Overlay {
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
			super.draw(canvas, mapView, shadow);
			
			if(wayPoints == null) 
				return true;
			
			
			for(GPXWayPoint wp : wayPoints){
			     
			     GeoPoint p = new GeoPoint(
				            (int) (wp.lat * 1E6), 
				            (int) (wp.lon * 1E6));
			     
			     	// ---translate the GeoPoint to screen pixels---
					Point screenPts = new Point();	
					mapView.getProjection().toPixels(p, screenPts);
					
					// ---add the marker---
					canvas.drawBitmap(bmp, screenPts.x - (bmp.getWidth() /2), screenPts.y - bmp.getHeight(), null);
			}
			
			
			return true;
		}
	}
}
