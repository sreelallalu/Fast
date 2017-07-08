package appcmpt.example.com.fast4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import appcmpt.example.com.fragment.DetailsFrag;
import appcmpt.example.com.fragment.DriverDetails;
import appcmpt.example.com.fragment.MapFragment;
import appcmpt.example.com.fragment.ScanerFragment;


import static appcmpt.example.com.fast4.MainDataBAse.TABLE_CONTACTS;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends ActionBarActivity {

	private ListView mDrawerList = null;
	private DrawerLayout mDrawerLayout = null;
	private ActionBarDrawerToggle mDrawerToggle ;
	private String mTitle = "";
	private MainDataBAse db;
	private String mDrawerTitle = "";
	@SuppressWarnings("unused")
	private TextView menuappname;
	private GPSTracker gps;
	private Toast trigger_toast;
	private String[] data_list = null;
	public static FragmentManager fragmentManager;
	private android.support.v7.app.ActionBar actionBar;
	private Fragment fragment = null;
	private android.support.v4.app.FragmentTransaction fragmentTransaction;
	private Boolean exit = false;
	private String utype = "";
	private String qrcode, lattitude, longitude;
	private JSONObject jsonObject;
private int type;
	private String _userId;
	private Double dbl_latitude, dbl_longitude;
	private final static String USER_AGENT = "Mozilla/5.0";

	@Override
	public void onBackPressed() {
		if (exit)
			System.exit(0);
		else {
			Toast.makeText(this, "Press Back again to Exit.",
					Toast.LENGTH_SHORT).show();
			exit = true;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					exit = false;
				}
			}, 3 * 1000);

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		gps = new GPSTracker(getApplicationContext());

		mDrawerList = (ListView) findViewById(R.id.drawer_list);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		db = new MainDataBAse(getApplicationContext());
		db.open();
		profile profile = db.getContact(1);
       db.close();
		// accessin Location with GPS
		if (gps.canGetLocation()) {

			dbl_latitude = gps.getLatitude();
			dbl_longitude = gps.getLongitude();
			lattitude = String.valueOf(dbl_latitude);
			longitude = String.valueOf(dbl_longitude);

		} else {
			Toast.makeText(getApplicationContext(), "Please enable your GPS",
					Toast.LENGTH_LONG).show();
		}

		qrcode = profile.getQcode();
		utype = profile.getType();
		_userId = profile.getUser_id();
        Log.e("usertype",utype);
		type=Integer.parseInt(utype);
       Log.e("type",""+type);

		if(type==0)
		{
			init1_User();
			if (savedInstanceState == null) {
				ClickPos(1);
				Log.e("usertype",utype);
			}

		}
		else
		if(type==1)
		{
			init_Doctor();
			if (savedInstanceState == null) {
				ClickPos(2);
				Log.e("usertype",utype);
			}
		}




		// Toast.makeText(getApplicationContext(),,Toast.LENGTH_LONG).show();

	}

	private void init_Doctor() {
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		actionBar = getSupportActionBar();

		MenuComp menu_data[] = new MenuComp[] {
				new MenuComp(R.drawable.qrmycode, "qr code"),
				new MenuComp(R.drawable.ic_validpoint, "valid point"),
				new MenuComp(R.drawable.log_off, "sign out"),
				new MenuComp(R.drawable.qrcloseicon, "Exit") };

		MenuAdapter adapter = new MenuAdapter(this, R.layout.listview_item_row,
				menu_data);
		View header = (View) getLayoutInflater().inflate(
				R.layout.listview_header_row, null);
		mDrawerList.addHeaderView(header);

		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// Initializing title
		mTitle = getResources().getString(R.string.app_name);
		mDrawerTitle = getResources().getString(R.string.drawer_open);

		actionbarToggleHandler();
		fragment = new ScanerFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, fragment).commit();


	}

	private void init1_User() {
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		actionBar = getSupportActionBar();
		// Initialize Listview

		// Initialize drawer layout


		MenuComp menu_data[] = new MenuComp[] {
				new MenuComp(R.drawable.qrscanicon, "Scan"),
				new MenuComp(R.drawable.mapicon, "Map"),
				new MenuComp(R.drawable.distress, "Distress"),
				new MenuComp(R.drawable.log_off, "sign out"),
				new MenuComp(R.drawable.history, "History"),
				new MenuComp(R.drawable.qrcloseicon, "Exit") };

		MenuAdapter adapter = new MenuAdapter(this, R.layout.listview_item_row,
				menu_data);

		View header = (View) getLayoutInflater().inflate(
				R.layout.listview_header_row, null);
		mDrawerList.addHeaderView(header);

		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// Initializing title
		mTitle = getResources().getString(R.string.app_name);
		mDrawerTitle = getResources().getString(R.string.drawer_open);

		actionbarToggleHandler();
		fragment = new ScanerFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
	}

	@SuppressLint("NewApi")
	private void actionbarToggleHandler() {
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View drawerView) {
				actionBar.setTitle(mTitle);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				actionBar.setTitle(mDrawerTitle);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	class DrawerItemClickListener implements OnItemClickListener {
		LayoutInflater li = getLayoutInflater();
		// Getting the View object as defined in the customtoast.xml file
		View layout = li.inflate(R.layout.customtoast_layt,
				(ViewGroup) findViewById(R.id.custom_toast_layout));

		@Override
		public void onItemClick(AdapterView adapter, View view, int position,
								long id) {


			ClickPos(position);

		}

	}	private void ClickPos(int position) {


		if(type==1)
		{

			switch (position) {


				case 1:
					Intent ned=new Intent(MainActivity.this,Qrcode.class);
					startActivity(ned);

				/*fragment = new DetailsFrag();
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
   */

					break;

				case 3:
					AlertDialog.Builder mbuilder = new AlertDialog.Builder(
							MainActivity.this);
					mbuilder.setMessage("this action will reset your app");
					mbuilder.setPositiveButton("Yes", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub


							db.open();
							db.deleteEntry();
							db.close();
							SharedPreferences.Editor editor=getSharedPreferences("USER", Context.MODE_PRIVATE).edit();

							editor.putBoolean("register", false);
							editor.putBoolean("typerr", false);
							// db.addContact(new
							// profile(st_usertype,result.getString("code")));
							editor.commit();
							finish();
						}
					});
					mbuilder.setNegativeButton("NO", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub

						}
					});
					AlertDialog d = mbuilder.create();
					d.show();
					break;

				case 4:
					mDrawerLayout.closeDrawer(mDrawerList);
					AlertDialog.Builder mbBuilder = new AlertDialog.Builder(
							MainActivity.this);
					mbBuilder.setMessage("Do yo want to quit?");
					mbBuilder.setPositiveButton("Yes",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									finish();
								}
							});
					mbBuilder.setNegativeButton("No",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									// TODO Auto-generated method stub

								}
							});
					AlertDialog quuit_dilg = mbBuilder.create();
					quuit_dilg.show();

					break;
				case 2:
					fragment = new ValidPoint();


					break;
				default:
					return;
			}


		}
		else
		if(type==0)
		{
			switch (position) {
				case 1:
					fragment = new ScanerFragment();
					break;
				case 2:
					fragment = new MapFragment();
					break;
				case 3:
					AlertDialog.Builder dis_alt_builder = new AlertDialog.Builder(
							MainActivity.this);
					dis_alt_builder.setMessage("feeling unsafe?");
					dis_alt_builder.setPositiveButton("yes",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									Vibrator v = (Vibrator) MainActivity.this
											.getSystemService(Context.VIBRATOR_SERVICE);
									v.vibrate(500);

									SendUnSafe();

								}
							});
					dis_alt_builder.setNegativeButton("No",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub

								}
							});
					AlertDialog triger_dlg = dis_alt_builder.create();
					triger_dlg.show();
					break;



				/*case 4:
					AlertDialog.Builder mbuilder = new AlertDialog.Builder(
							MainActivity.this);
					mbuilder.setMessage("this action will reset your app");
					mbuilder.setPositiveButton("Yes", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							db.open();
							db.deleteEntry();
							db.close();

							SharedPreferences.Editor editor = getPreferences(
									MODE_PRIVATE).edit();
							editor.putBoolean("register", false);
							// db.addContact(new
							// profile(st_usertype,result.getString("code")));
							editor.commit();
							finish();
						}
					});
					mbuilder.setNegativeButton("NO", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub

						}
					});
					AlertDialog d = mbuilder.create();
					d.show();
					break;*/
				case 4:
					mDrawerLayout.closeDrawer(mDrawerList);
					AlertDialog.Builder mbBuilder = new AlertDialog.Builder(
							MainActivity.this);
					mbBuilder.setMessage("Do yo want to sign out?");
					mbBuilder.setPositiveButton("Yes",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									SharedPreferences.Editor editor=getSharedPreferences("USER", Context.MODE_PRIVATE).edit();

									editor.putBoolean("register", false);
									editor.putBoolean("typerr", false);
									editor.putString("buddyno", "");
									// db.addContact(new
									// profile(st_usertype,result.getString("code")));
									editor.commit();
									finish();
								}
							});
					mbBuilder.setNegativeButton("No",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									// TODO Auto-generated method stub

								}
							});
					AlertDialog quuit_dilg = mbBuilder.create();
					quuit_dilg.show();

					break;


				case 5:
					fragment = new DriverDetails();
					Bundle args=new Bundle();
					args.putInt("key",1);
					fragment.setArguments(args);
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
					break;
				case 6:
					mDrawerLayout.closeDrawer(mDrawerList);
					AlertDialog.Builder mbBuilder1 = new AlertDialog.Builder(
							MainActivity.this);
					mbBuilder1.setMessage("Do yo want to quit?");
					mbBuilder1.setPositiveButton("Yes",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									finish();
								}
							});
					mbBuilder1.setNegativeButton("No",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									// TODO Auto-generated method stub

								}
							});
					AlertDialog quuit_dilg1 = mbBuilder1.create();
					quuit_dilg1.show();

					break;
				default:
					return;
			}

		}




		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, fragment).commit();

		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void SendUnSafe() {
String driverid=DataDriverId.getInstance().getDistributor_id();
		if(driverid=="")
		{
			Toast.makeText(MainActivity.this, "start journey", Toast.LENGTH_SHORT).show();
		return;
		}else {

			SharedPreferences prefs = getSharedPreferences("USER",Context.MODE_PRIVATE);
			String buddyno = prefs.getString("buddyno","");
			Log.e("buddyno",buddyno.length()+"");
			if(buddyno!="")
			{
                try {
					String content = "Help me I feel Unsafe.!" + "\n" + lattitude + "," + longitude;

					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage(buddyno, null, content, pi, null);
				}catch (Exception e){
e.printStackTrace();
				}
			}




			HashMap<String, String> hashmap = new HashMap<>();
			hashmap.put("tag", "unsafe");
			hashmap.put("userid", _userId);
			hashmap.put("latitude", lattitude + "," + longitude);
			hashmap.put("driver_id",driverid);

			PostResponseAsyncTask task = new PostResponseAsyncTask(MainActivity.this, hashmap, new AsyncResponse() {
				@Override
				public void processFinish(String s) {
					Log.e("response", s);
					try {

						JSONObject df = new JSONObject(s);
						int sdd = df.getInt("success");
						if (sdd == 1) {
							Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}


				}
			});
			task.execute("http://taxi.venturesoftwares.org/index.php");
			task.setEachExceptionsHandler(new EachExceptionsHandler() {
				@Override
				public void handleIOException(IOException e) {

				}

				@Override
				public void handleMalformedURLException(MalformedURLException e) {

				}

				@Override
				public void handleProtocolException(ProtocolException e) {

				}

				@Override
				public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {

				}
			});

		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	mDrawerToggle.syncState();
	}

	/*class CheckAvialability extends AsyncTask<String, Void, JSONObject> {

		ProgressDialog p = new ProgressDialog(MainActivity.this);

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			super.onPreExecute();
			p.setCancelable(false);
			p.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub

			jsonObject = getJSONFromUrl("http://taxi.venturesoftwares.org");
			if (jsonObject != null) {
				try {
					Log.d("json values",
							"json" + jsonObject.getString("success"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			p.dismiss();
			if (result != null) {
				try {
					if (result.getInt("success") == 1) {
						trigger_toast.show();

						ColorDrawable colorDrawable = new ColorDrawable(
								Color.parseColor("#F54542"));
						actionBar.setBackgroundDrawable(colorDrawable);
					} else {

						Toast.makeText(MainActivity.this,
								result.getString("error_msg"),
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Error in registration", Toast.LENGTH_LONG).show();
			}

			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		public JSONObject getJSONFromUrl(String url) {

			InputStream is = null;
			JSONObject jObj = null;
			String json = "";
			URL obj;
			HttpURLConnection con = null;

			// Making HTTP request
			try {
				System.out.println("url" + url);

				obj = new URL(url);
				con = (HttpURLConnection) obj.openConnection();

				// optional default is GET
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				// add request header
				// con.setRequestProperty("User-Agent", USER_AGENT);
				// con.setRequestProperty("Accept", "application/json");
				// con.setRequestProperty("tag", "trigger");
				// con.setRequestProperty("code", qrcode);
				// con.setRequestProperty("lat", lattitude);
				// con.setRequestProperty("long", longitude);
				String charset = "UTF-8";
				String s = "tag=" + URLEncoder.encode("trigger", charset);
				s += "&code=" + URLEncoder.encode(qrcode, charset);
				s += "&lat=" + URLEncoder.encode(lattitude, charset);
				s += "&long=" + URLEncoder.encode(longitude, charset);

				con.setFixedLengthStreamingMode(s.getBytes().length);
				PrintWriter out = new PrintWriter(con.getOutputStream());
				out.print(s);
				out.close();
				int responseCode = con.getResponseCode();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result

				json = response.toString();
				Log.e("JSON", json);
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}

			// try parse the string to a JSON object
			try {
				jObj = new JSONObject(json);
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}

			// return JSON String
			return jObj;

		}
	}*/

}
