package ch.arons.android.gps;

import java.io.File;
import java.io.FilenameFilter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GPXFileActivity extends ListActivity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  //retrieve all file
	  File root = Environment.getExternalStorageDirectory();
	  File dir = new File(root, "/GPSTrac/");
	  
	  File[] listOfFile = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String s) {
				return s.endsWith(".gpx");
			}
		});
	  
	  if(listOfFile.length > 0){
		  String[] filaNames = new String[listOfFile.length];
		  for(int i=0; i<listOfFile.length; i++){
			  filaNames[i] = listOfFile[i].getName();
		  }
		  setListAdapter(new ArrayAdapter<String>(this, R.layout.file_list, filaNames));
	  }
	  
	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);

	  lv.setOnItemClickListener(new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	      // When clicked, show a toast with the TextView text
	      CharSequence fileName = ((TextView) view).getText();
	      Toast.makeText(getApplicationContext(), fileName.toString(),Toast.LENGTH_SHORT).show();
	      
	      GPXFileViewActivity.fileName = fileName.toString();
	      Intent menuFileIntent = new Intent(getBaseContext(), GPXFileViewActivity.class);
          startActivityForResult(menuFileIntent, 0);
          
	    }
	  });
	}

}
