package appcmpt.example.com.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import appcmpt.example.com.fast4.DataUserAge;
import appcmpt.example.com.fast4.R;


public class DriverDetails extends Fragment {
	TextView username, usertype, vno, rent;
	Button go;
	Fragment fragment = null;
	int key = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.driverdetls, container, false);
		Bundle bundle = getArguments();
		username = (TextView) rootView.findViewById(R.id.tvqrusername);
		usertype = (TextView) rootView.findViewById(R.id.tvqrusertype);
		vno = (TextView) rootView.findViewById(R.id.tvqrvno);
		rent = (TextView) rootView.findViewById(R.id.tvqrrate);
		go = (Button) rootView.findViewById(R.id.btstartj);


		go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try{
				fragment = new MapFragment();}catch (Exception e){

				}
				// TODO Auto-generated method stub
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.frame_container, fragment).commit();

			}
		});
         String s= DataUserAge.getInstance().getDistributor_id();
		Log.e("driver_details",s);

		if (s != null) {

			try {


				JSONObject hj=new JSONObject(s);

					DataUserAge.getInstance().setDistributor_id(s);
					JSONArray er = hj.getJSONArray("Scan_Details");
					for (int i = 0; i < er.length(); i++) {
						JSONObject ko = er.getJSONObject(i);

						String Driver = ko.getString("Driver Name");
						String phone = ko.getString("phone no");
						String vehicleno = ko.getString("Vehicle_no");
						String rate = ko.getString("Rate");
						String type = ko.getString("Vehicle_type");

						username.setText(Driver);
						usertype.setText(type);
						vno.setText(vehicleno);
						rent.setText(rate);

					}







			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			Toast.makeText(getActivity(), "no details", Toast.LENGTH_LONG)
					.show();
		}
		return rootView;

	}

}
