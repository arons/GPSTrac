package ch.arons.android.gps.io.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

	private static final String FILENAME_FORMAT = "yyyy-MM-dd_HH-mm_ss";
	private final SimpleDateFormat fileFormatter;
	File dir;
	
	
	int creationDay;
	Calendar creationTime;
	BufferedWriter out;

	public GPXWriter() {

		timestampFormatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
		timestampFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		fileFormatter = new SimpleDateFormat(FILENAME_FORMAT);
		// fileFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

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

		//NOTE that works only with API 7, for api 8 or greather check documentation
		File root = Environment.getExternalStorageDirectory();
		dir = new File(root, "/GPSTrac/");

		if (!dir.exists()) {
			boolean result = dir.mkdirs();
			Log.e(COMPONENT, "Create directory result:" + result);
		}
	}

	public void createNewFile() {
		creationTime = new GregorianCalendar();
		creationDay = creationTime.get(Calendar.DAY_OF_YEAR);
		
		File gpxfile = new File(dir, fileFormatter.format(creationTime.getTime()) + ".gpx");
		try {
			FileWriter gpxwriter = new FileWriter(gpxfile);
			out = new BufferedWriter(gpxwriter);

			writeHeader();

		} catch (IOException e) {

			Log.e(COMPONENT, e.getMessage(), e);
			creationTime = null;
			out = null;
		}

	}

	public void writeHeader() throws IOException {
		out.write("<?xml version=\"1.0\" standalone=\"yes\"?>\n");
//		out.write("<gpx");
//		out.write(" version=\"1.1\"");
//		out.write(" creator=\"GPSTrac\"");
//		out.write(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
//		out.write(" xmlns=\"http://www.topografix.com/GPX/1/1\"");
//		out.write(" xmlns:topografix=\"http://www.topografix.com/GPX/Private/TopoGrafix/0/1\"");
//		out.write(" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 ");
//		out.write("http://www.topografix.com/GPX/1/1/gpx.xsd ");
//		out.write("http://www.topografix.com/GPX/Private/TopoGrafix/0/1 ");
//		out.write("http://www.topografix.com/GPX/Private/TopoGrafix/0/1/topografix.xsd\"> \n");
		
		out.write("<gpx \n");
		out.write(" version=\"1.0\"\n");
		out.write(" creator=\"GPSTrac\" \n");
		out.write(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		out.write(" xmlns=\"http://www.topografix.com/GPX/1/0\"\n");
		out.write(" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">\n");
	}

	  public void writeFooter() throws IOException {
	    if (out != null) {
	    	out.write("</gpx>");
	    }
	  }
	
	public void close() {
		if (out != null) {
			try {
				writeFooter();
			} catch (IOException e) {
				Log.e(COMPONENT, e.getMessage(), e);
			}
			
			try {
				out.close();
			} catch (IOException e) {
				Log.e(COMPONENT, e.getMessage(), e);
				creationTime = null;
				out = null;
			}
		}
	}

	private String formatLocation(Location l) {
		return "lat=\"" + coordinateFormatter.format(l.getLatitude()) + 
		       "\" lon=\"" + coordinateFormatter.format(l.getLongitude()) + "\"";
	}

	public void writeLocation(Location l) {
		if(l == null) return;
		
		GregorianCalendar now = new GregorianCalendar();
		int nowDay = now.get(Calendar.DAY_OF_YEAR);
		if(nowDay != creationDay){
			close();
			createNewFile();
		}
		
		if (out != null) {
			try {
				out.write("<wpt  " + formatLocation(l) + ">\n");
				if(l.hasAltitude()) out.write("<ele>" + elevationFormatter.format(l.getAltitude()) + "</ele>\n");
				out.write("<time>" + timestampFormatter.format(new Date(l.getTime())) + "</time>\n");
				out.write("</wpt >\n");
			} catch (IOException e) {
				Log.e(COMPONENT, e.getMessage(), e);
			}
		}
	}

}
