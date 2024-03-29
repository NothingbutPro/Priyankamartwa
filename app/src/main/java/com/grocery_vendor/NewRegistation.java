package com.grocery_vendor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import com.grocery_vendor.Config.BaseURL;
import com.grocery_vendor.util.AppPreference;
import com.grocery_vendor.util.ConnectivityReceiver;
import com.grocery_vendor.util.Session_management;

public class NewRegistation extends AppCompatActivity {
    EditText otphint;
    TextView mobNum;
    Button btVerify;
    String Otphint;
    private String newString;
    int otps;
    String NewMob;
    Session_management sessionManagement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registation);

        sessionManagement = new Session_management(NewRegistation.this);

        otphint = (EditText)findViewById(R.id.otphint);
        btVerify = (Button)findViewById(R.id.btVerify);
        mobNum = (TextView)findViewById(R.id.mobNum);
       // mobNum.setText(sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE));
        mobNum.setText(getIntent().getStringExtra("mobile"));
      //  otphint.setText(AppPreference.getOtp(NewRegistation.this));

        //NewMob = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        NewMob = getIntent().getStringExtra("mobile");

        otphint.setText(getIntent().getStringExtra("OTP"));

        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Otphint = otphint.getText().toString();

                if (ConnectivityReceiver.isConnected()) {
                    new PostReg().execute();
                } else {
                    Toast.makeText(NewRegistation.this, "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //-----------------------------------------------------------------------

    public class PostReg extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(NewRegistation.this);
            dialog.show();

        }

        protected String doInBackground(String... strings) {

            try {

                URL url = new URL("https://axomiyagohona.com/priyanka_mart/grocery-store/api/matchotp");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("mobile", mobNum.getText().toString());
                postDataParams.put("otp", otphint.getText().toString());

                Log.e("postDataParams_otp", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds*/);
                conn.setConnectTimeout(15000  /*milliseconds*/);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        StringBuffer Ss = sb.append(line);
                        Log.e("Ss", Ss.toString());
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                dialog.dismiss();
              //  Toast.makeText(NewRegistation.this, ""+result, Toast.LENGTH_SHORT).show();

                JSONObject jsonObject = null;
                String s = result.toString();
                Log.e("Json_result",s);
                try {
                    jsonObject = new JSONObject(result);
                    Boolean responce = jsonObject.getBoolean("responce");
                    String data = jsonObject.getString("data");
                    String user_id = jsonObject.getString("user_id");
                    String mobile_no = jsonObject.getString("mobile_no");

                    if (responce) {

                        AppPreference.setMobile(NewRegistation.this,mobile_no);
                        AppPreference.setUser_Id(NewRegistation.this,user_id);
                       // Toast.makeText(NewRegistation.this, "userI "+ AppPreference.getUser_Id(NewRegistation.this), Toast.LENGTH_SHORT).show();
                        sessionManagement.createLoginSession(mobile_no);

                        Toast.makeText(NewRegistation.this, "Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewRegistation.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(NewRegistation.this, data, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {

                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }
    }
}

    //-------------------------------------------------------------------------------------

   /* private class CompleteRegistration extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(NewRegistation.this);
            dialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                URL url = new URL("http://axomiyagohona.com/grocery-store/api/matchotp");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("mobile", NewMob);
                postDataParams.put("otp", Otphint);

                Log.e("postDataParams", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 *//* milliseconds*//*);
                conn.setConnectTimeout(15000  *//*milliseconds*//*);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        result.append(line);
                    }
                    r.close();
                    return result.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }


        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {

                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }


        @Override
        protected void onPostExecute(String result) {
            //dialog.dismiss();
            if (result != null) {
                //  dialog.dismiss();


                Log.e("PostRegistration", result.toString());

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String responce = jsonObject.getString("responce");
                    String data = jsonObject.getString("data");
                    if (responce.equals("true")) {
                        Intent intent = new Intent(NewRegistation.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(NewRegistation.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(NewRegistation.this, "Could not register the user", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            super.onPostExecute(result);
        }

    }

}
*/
//---------------------------------------------------


