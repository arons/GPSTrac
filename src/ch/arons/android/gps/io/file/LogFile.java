package ch.arons.android.gps.io.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class LogFile {
	private static final String COMPONENT = "LogFile";
	
	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss ";
	private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
	
	private static final File gpxfile;
	static{
		// NOTE that works only with API 7, for api 8 or greather check
		File root = Environment.getExternalStorageDirectory();
		File dir = new File(root, "/GPSTrac/");

		if (!dir.exists()) {
			boolean result = dir.mkdirs();
			Log.e(COMPONENT, "Create directory result:" + result);
		}

		gpxfile = new File(dir, "GPSTracLog.txt");
	}
	
	
	
	public static void log(String message){
		
		

		RandomAccessFile raf = null;
        try {
        	
        	Date time = new Date(System.currentTimeMillis());
        	
	        raf = new RandomAccessFile(gpxfile, "rw");
	        raf.seek(raf.length());
	        
	        
	        raf.writeBytes(timestampFormatter.format(time));
	        raf.writeBytes(" > ");
	        raf.writeBytes(message);
	        raf.writeBytes("\n");
	        
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

}
