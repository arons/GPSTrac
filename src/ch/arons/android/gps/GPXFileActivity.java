package ch.arons.android.gps;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GPXFileActivity extends Activity {
	private static final String COMPONENT = "GPXFileActivity";

	ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.file_list);
		list = (ListView) findViewById(R.id.list);

		list.setTextFilterEnabled(true);

		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// When clicked, show a toast with the TextView text
				CharSequence fileName = ((TextView) view).getText();
				Toast.makeText(getApplicationContext(), fileName.toString(), Toast.LENGTH_SHORT).show();

				GPXFileViewActivity.fileName = fileName.toString();
				Intent menuFileIntent = new Intent(getBaseContext(), GPXFileViewActivity.class);
				startActivityForResult(menuFileIntent, 0);

			}
		});

		registerForContextMenu(list);

		initList();
	}

	private void initList() {
		// retrieve all file
		File root = Environment.getExternalStorageDirectory();
		File dir = new File(root, "/GPSTrac/");

		File[] listOfFile = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String s) {
				return s.endsWith(".gpx");
			}
		});
		
		Arrays.sort(listOfFile);

		if (listOfFile.length > 0) {
			String[] filaNames = new String[listOfFile.length];
			for (int i = 0; i < listOfFile.length; i++) {
				filaNames[listOfFile.length - i -1] = listOfFile[i].getName();
			}
			
			list.setAdapter(new ArrayAdapter<String>(this, R.layout.listitem, filaNames));
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		if (v.getId() == R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(((TextView) info.targetView).getText());
			// String[] menuItems = getResources().getStringArray(R.array.menu);
			// for (int i = 0; i<menuItems.length; i++) {
			// menu.add(Menu.NONE, i, i, menuItems[i]);
			// }
			menu.add(Menu.NONE, 0, 0, "Delete");
			menu.add(Menu.NONE, 1, 1, "Send");
			menu.add(Menu.NONE, 2, 2, "Map");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();

		CharSequence fileName = ((TextView) info.targetView).getText();
		File root = Environment.getExternalStorageDirectory();
		File dir = new File(root, "/GPSTrac/");
		File file = new File(dir, fileName.toString());

		switch (menuItemIndex) {
		case 0:// delete
			Log.d(COMPONENT,"deleting file:"+file.getName());
			if (file.delete()) {
				Toast.makeText(getApplicationContext(), file.getName() + " deleted", Toast.LENGTH_SHORT).show();
				initList();
			}
			break;

		case 1:// send
			email(file);
			break;
			
		case 2:// Map
			Intent mapIntent = new Intent(getBaseContext(), GPXFileViewMapActivity.class);
			startActivityForResult(mapIntent, 0);
			break;

		default:
			break;
		}

		return true;
	}

	private void email(File file) {
		// need to "send multiple" to get more than one attachment
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("plain/text");
		// emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new
		// String[]{emailTo});
		// emailIntent.putExtra(android.content.Intent.EXTRA_CC, new
		// String[]{emailCC});
		// has to be an ArrayList
		ArrayList<Uri> uris = new ArrayList<Uri>();
		// convert from paths to Android friendly Parcelable Uri's
		Uri u = Uri.fromFile(file);
		uris.add(u);

		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		startActivityForResult(emailIntent, 0);
	}
}
