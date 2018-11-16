package com.example.adam.androidjsonparser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    EditText getUser; // This will be a reference to our ID input.
    Button btnGetUsers;  // This is a reference to the "Get Data" button.
    TextView tvDataList;  // This will reference our data list text box.
    RequestQueue requestQueue;  // This is our requests queue to process our HTTP requests.

    String baseUrl = "http://192.168.1.16:8080/api/users/";  // This is the local PZH API URL for users - because it runs now on localhost, ip address has to be changes on different locations
    String url; // This will hold the full URL which will include the user ID entered in the getUser. (if entered)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // This is some magic for Android to load a previously saved state for when you are switching between actvities.
        setContentView(R.layout.activity_main);  // This links our code to our layout

        this.getUser = (EditText) findViewById(R.id.get_user);  // Link our ID input text box.
        this.btnGetUsers = (Button) findViewById(R.id.btn_get_users);  // Link our clicky button.
        this.tvDataList = (TextView) findViewById(R.id.tv_repo_list);  // Link our data list text output box.
        this.tvDataList.setMovementMethod(new ScrollingMovementMethod());  // This makes our text box scrollable

        requestQueue = Volley.newRequestQueue(this);  // This setups up a new request queue which we will need to make HTTP requests.
    }
    private void clearDataList() {
        // This will clear the data list (set it as a blank string).
        this.tvDataList.setText("");
    }

    private void addToUserList(String firstName, String lastName, String createdAt, String lastUpdated, String email, String phone, String userId, String role) {
        // This will add a new data to our list.
        // It combines the data strings together in a "formated" matter
        // And then adds them followed by a new line (\n\n make two new lines).
        String strRow = firstName + " " + lastName + "\nID: " + userId + "\ntel: " + phone + "\nemail: " + email + "\nrole: " + role + "\n" + "created: " + createdAt + "\nupdated: " + lastUpdated;
        String currentText = tvDataList.getText().toString();
        this.tvDataList.setText(currentText + "\n\n" + strRow);
    }

    private void setDataListText(String str) {
        // This is used for setting the text of our data list box to a specific string.
        // We will use this to write a "No data found" message if the request links to nothing.
        this.tvDataList.setText(str);
    }
    private void getDataList(String id) {
        // First, we insert the id into the API url.
        // The API url is defined in format address:port/api/users/id (without id lists all users)
        this.url = this.baseUrl + id;

        // Next, we create a new JsonArrayRequest. This will use Volley to make a HTTP request
        // that expects a JSON Array Response.
        // To fully understand this, I'd recommend readng the office docs: https://developer.android.com/training/volley/index.html
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check the length of our response (to see if there are any data)
                        if (response.length() > 0) {
                            // The address points to some data, so let's loop through them all.
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // For each data block, add a new line to our data list.
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String firstName = jsonObj.get("first_name").toString();
                                    String lastName = jsonObj.get("last_name").toString();
                                    String createdAt = jsonObj.get("createdAt").toString();
                                    String lastUpdated = jsonObj.get("updatedAt").toString();
                                    String email = jsonObj.get("email").toString();
                                    String phone = jsonObj.get("phone").toString();
                                    String userId = jsonObj.get("user_id").toString();
                                    String role = jsonObj.get("role").toString();
                                    addToUserList(firstName, lastName, createdAt, lastUpdated, email, phone, userId, role);
                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                            }
                        } else {
                            // The address didnt point to any data.
                            setDataListText("No data found.");
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our data list.
                        setDataListText("Error while calling REST API");
                        Log.e("Volley", error.toString());
                    }
                }
        );
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }
    public void getDataClicked(View v) {
        // Clear the data list (so we have a fresh screen to add to)
        clearDataList();
        // Call our getdataList() function that is defined above and pass in the
        // text which has been entered into the getUser text input field.
        getDataList(getUser.getText().toString());
    }
}
