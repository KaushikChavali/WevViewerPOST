package com.example.admin.webviewpost;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Bundle;
        import android.text.method.ScrollingMovementMethod;
        import android.util.Base64;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.AuthFailureError;
        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;

        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;


public class MyActivity extends Activity {

    TextView output;
    ProgressBar pb;

    String username;
    String pwd;

    List<Site> siteList;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Intent intent = getIntent();

        username = intent.getStringExtra(LoginActivity.EXTRA_NAME);
        pwd = intent.getStringExtra(LoginActivity.EXTRA_PASS);

        output = (TextView) findViewById(R.id.textView);
        output.setMovementMethod(new ScrollingMovementMethod());

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);


        requestQueue = Volley.newRequestQueue(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_do_task) {
            if (isOnline()) {
                requestData("http://api.nilsp.in/api/v1/url/");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }



    private void requestData(String uri) {

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                uri,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("HTTP Response", response);
                        siteList = SiteJSONParser.parseFeed(response);
                        updateDisplay();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Toast.makeText(MyActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return createBasicAuthHeader(username,pwd);
            }


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id", "2");
                params.put("user_id", "1");
                params.put("url", "http://www.theverge.com");
                params.put("description", "Google Search Engine");

                return params;
            }
        };

        requestQueue.add(postRequest);
    }

    private Map<String, String> createBasicAuthHeader(String username, String password) {
        Map<String, String> headerMap = new HashMap<String, String>();

        String credentials = username + ":" + password;
        String base64EncodedCredentials =
                Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headerMap.put("Authorization", "Basic " + base64EncodedCredentials);
        Log.d("HTTP Response",base64EncodedCredentials);

        return headerMap;
    }


    protected void updateDisplay(){
        if(siteList != null){
            for (Site site: siteList){
                output.append("ID: "+site.getId()+"\n");
                output.append("User ID: "+site.getUser_id()+"\n");
                output.append("URL: "+site.getUrl()+"\n");
                output.append("Description: "+site.getDescription()+"\n");
                output.append("Created At: "+site.getCreated_at()+"\n");
                output.append("Updated At: "+site.getUpdated_at()+"\n");

            }
        }
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo!=null && netInfo.isConnectedOrConnecting()){
            return true;
        } else{
            return false;
        }
    }
}
