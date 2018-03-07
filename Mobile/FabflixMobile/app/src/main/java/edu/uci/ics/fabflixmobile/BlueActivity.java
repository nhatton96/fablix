package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class BlueActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);

        Bundle bundle = getIntent().getExtras();
        Toast.makeText(this, "Last activity was " + bundle.get("last_activity") + ".", Toast.LENGTH_LONG).show();

        String msg = bundle.getString("message");
        if(msg != null && !"".equals(msg)){
            ((TextView)findViewById(R.id.last_page_msg_container)).setText(msg);
        }
        //connectToTomcat();

    }
/*
    public void goToRed(View view){
        String msg = ((EditText)findViewById(R.id.blue_2_red_message)).getText().toString();

        Intent goToIntent = new Intent(this, RedActivity.class);

        goToIntent.putExtra("last_activity", "blue");
        goToIntent.putExtra("message", msg);

        startActivity(goToIntent);
    }
    public void goToGreen(View view){
        String msg = ((EditText)findViewById(R.id.blue_2_green_message)).getText().toString();

        Intent goToIntent = new Intent(this, GreenActivity.class);

        goToIntent.putExtra("last_activity", "blue");
        goToIntent.putExtra("message", msg);

        startActivity(goToIntent);
    }*/

    public void search(View view){
        final Map<String, String> params = new HashMap<String, String>();


        // no user is logged in, so we must connect to the server
        RequestQueue queue = Volley.newRequestQueue(this);

        final Context context = this;
        String searchToken = ((EditText)findViewById(R.id.searchInput)).getText().toString();
        String url = "http://10.0.2.2:8080/Project/api/movie?title="+searchToken+"&Page=1&ACTION=SEARCH&order=ta&PageSize=20";


        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response){

                        Log.d("response", response);
                        fillTable(response);


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                        //wrongEmailPassword();

                    }
                }
        );


        // Add the request to the RequestQueue.
        //postRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);


        return ;
    }

    public void fillTable(String resultData) {
        try{
            JSONArray jsonArray = new JSONArray(resultData);
            TableLayout ll = (TableLayout) findViewById(R.id.tableLayout1);

            for (int i = 0; i < resultData.length(); i++) {

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView movieId = new TextView(this);
                TextView title = new TextView(this);
                TextView year = new TextView(this);
                TextView director = new TextView(this);
                TextView rating = new TextView(this);
                TextView list_of_genres = new TextView(this);

                movieId.setText(jsonArray.getJSONObject(i).getString("movieId"));
                title.setText(jsonArray.getJSONObject(i).getString("title"));
                year.setText(jsonArray.getJSONObject(i).getString("year"));
                director.setText(jsonArray.getJSONObject(i).getString("director"));
                rating.setText(jsonArray.getJSONObject(i).getString("rating"));
                list_of_genres.setText(jsonArray.getJSONObject(i).getString("list_of_genres"));

                movieId.setTextColor(Color.WHITE);
                title.setTextColor(Color.WHITE);
                year.setTextColor(Color.WHITE);
                director.setTextColor(Color.WHITE);
                rating.setTextColor(Color.WHITE);
                list_of_genres.setTextColor(Color.WHITE);

                row.addView(movieId);
                row.addView(title);
                row.addView(year);
                row.addView(director);
                row.addView(rating);
                row.addView(list_of_genres);

                ll.addView(row, i);
            }
        }catch (JSONException e){
            Log.d("Fill Table Exception", e.toString());
        }
    }

    public void connectToTomcat(){

        //

        final Map<String, String> params = new HashMap<String, String>();


        // no user is logged in, so we must connect to the server
        RequestQueue queue = Volley.newRequestQueue(this);

        final Context context = this;
        String url = "http://10.0.2.2:8080/Project/api/movie?Page=1&ACTION=LIST&order=tr&PageSize=20";


        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response){

                        Log.d("response", response);
                        //((TextView)findViewById(R.id.http_response)).setText("HEEEEEYYYYY");
                        fillTable(response);


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                        //wrongEmailPassword();

                    }
                }
        );


        // Add the request to the RequestQueue.
        //postRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);


        return ;
    }

}
