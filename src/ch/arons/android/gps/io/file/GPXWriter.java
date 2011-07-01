package ch.arons.android.gps.io.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.location.Location;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * TODO use one file a day ????
 * 
 */
public class GPXWriter {
	private static final String COMPONENT = "GPXWriter";

	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private final SimpleDateFormat timestampFormatter;
	
	private final NumberFormat elevationFormatter;
	private final NumberFormat coordinateFormatter;

	private static final String FILENAME_FORMAT = "yyyy-MM-dd";
	private final SimpleDateFormat fileFormatter;
	
	private static final String NAME_FORMAT = "HH:mm:ss";
	private final SimpleDateFormat nameFormatter;
	
	File dir;

	public GPXWriter() {
		
		timestampFormatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
		timestampFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		fileFormatter = new SimpleDateFormat(FILENAME_FORMAT);
		
		nameFormatter = new SimpleDateFormat(NAME_FORMAT);

		// GPX readers expect to see fractional numbers with US-style
		// punctuation.
		// That is, they want periods for decimal points, rather than commas.
		elevationFormatter = NumberFormat.getInstance(Locale.US);
		elevationFormatter.setMaximumFractionDigits(1);
		elevationFormatter.setGroupingUsed(false);

		coordinateFormatter = NumberFormat.getInstance(Locale.US);
		coordinateFormatter.setMaximumFractionDigits(5);
		coordinateFormatter.setMaximumIntegerDigits(3);
		coordinateFormatter.setGroupingUsed(false);

		// NOTE that works only with API 7, for api 8 or greather check
		// documentation
		File root = Environment.getExternalStorageDirectory();
		dir = new File(root, "/GPSTrac/");

		if (!dir.exists()) {
			boolean result = dir.mkdirs();
			Log.e(COMPONENT, "Create directory result:" + result);
		}
	}



	private String formatLocation(Location l) {
		return "lat=\"" + coordinateFormatter.format(l.getLatitude()) + "\" lon=\"" + coordinateFormatter.format(l.getLongitude()) + "\"";
	}

	public void writeLocation(Location l) {
		if (l == null)
			return;

		File gpxfile = new File(dir, fileFormatter.format(new Date()) + ".gpx");
		if (!gpxfile.exists()) {
			boolean ok = createFile(gpxfile);
			if (!ok)
				return;
		}

		RandomAccessFile raf = null;
        try {
        	
        	Date time = new Date(l.getTime());
        	
	        raf = new RandomAccessFile(gpxfile, "rw");
	        raf.seek(raf.length() - "</gpx>".length());
			raf.writeBytes(" <wpt  " + formatLocation(l) + ">\n");
			if (l.hasAltitude())
			raf.writeBytes("  <ele>" + elevationFormatter.format(l.getAltitude()) + "</ele>\n");
			raf.writeBytes("  <time>" + timestampFormatter.format(time) + "</time>\n");
			raf.writeBytes("  <name>" + nameFormatter.format(time)+
					                " source:"+l.getProvider()+
					                " accuracy:"+ elevationFormatter.format(l.getAccuracy()) + "</name>\n");
			raf.writeBytes(" </wpt >\n");
			raf.writeBytes("</gpx>");
			raf.close();
        } catch (FileNotFoundException e) {
        	Log.e(COMPONENT, e.getMessage(), e);
        } catch (IOException e) {
        	Log.e(COMPONENT, e.getMessage(), e);
        }finally{
        	if(raf != null)
	            try {
	                raf.close();
                } catch (IOException e) { }
        }
	}

	private boolean createFile(File gpxfile) {
		FileWriter gpxwriter;
		try {
			gpxwriter = new FileWriter(gpxfile);
			BufferedWriter out = new BufferedWriter(gpxwriter);
			out.write("<?xml version=\"1.0\" standalone=\"yes\"?>\n");
			out.write("<gpx \n");
			out.write(" version=\"1.0\"\n");
			out.write(" creator=\"GPSTrac\" \n");
			out.write(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
			out.write(" xmlns=\"http://www.topografix.com/GPX/1/0\"\n");
			out.write(" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">\n");
			out.write("</gpx>");
			out.close();
			return true;
		} catch (IOException e) {
			Log.e(COMPONENT, e.getMessage(), e);
			return false;
		}

	}

}
