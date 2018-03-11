package com.ibm.hellopush;

/**
 * Copyright 2015, 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;

import com.worklight.jsonstore.api.JSONStoreAddOptions;
import com.worklight.jsonstore.api.JSONStoreInitOptions;
import com.worklight.jsonstore.api.JSONStoreCollection;
import com.worklight.jsonstore.api.WLJSONStore;
import com.worklight.wlclient.api.WLClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {
    //new
    public static final String PREFS="example";

    public static final String PREFS_MOVIE_MASTER ="com.movie.master";
    public static final String PREF_MOVIE_LIST= "movielist";

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String TESTDRIVES_COLLECTION_NAME = "testdrives";
    private JSONStoreCollection testDrives;
    //final EditText userId =(EditText) findViewById(R.id.user);

    private MFPPush push; // Push client
    private MFPPushNotificationListener notificationListener; // Notification listener to handle a push sent to the phone
    String displayInfo="a";
    String qrcode;
    SharedPreferences sp = null;
    List<JSONObject> results;
    JSONArray movieListArray;
    ImageView LoadImage;
    StringBuilder urlBuilder = new StringBuilder();
     String store="q";
     String title;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // initialize core SDK with IBM Bluemix application Region, TODO: Update region if not using Bluemix US SOUTH
        BMSClient.getInstance().initialize(this, BMSClient.REGION_US_SOUTH);

        // Grabs push client sdk instance
        push = MFPPush.getInstance();
        // Initialize Push client
        // You can find your App Guid and Client Secret by navigating to the Configure section of your Push dashboard, click Mobile Options (Upper Right Hand Corner)
        // TODO: Please replace <APP_GUID> and <CLIENT_SECRET> with a valid App GUID and Client Secret from the Push dashboard Mobile Options
        push.initialize(this, "<APP_GUID>", "<CLIENT_SECRET>");

        // Create notification listener and enable pop up notification when a message is received
        notificationListener = new MFPPushNotificationListener() {
            @Override
            public void onReceive(final MFPSimplePushNotification message) {

                final String storeUrl;
               if (message.getUrl() != " ") {
                    Log.i(TAG, "Received a Push Notification: " + message.toString());
                    SharedPreferences sp = getSharedPreferences(MovieManager.getInstance().getUserId()+PREFS_MOVIE_MASTER, Context.MODE_PRIVATE);
                    String sc = sp.getString(PREF_MOVIE_LIST, null);
                    try {
                        if (sc == null) {
                            movieListArray = new JSONArray();
                        } else {
                            movieListArray = new JSONArray(sc);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    store = message.getAlert();
                    //storeUrl=message.getUrl();
                    //   displayInfo = store.toString();
                    if (message.getUrl() != "ibm") {
                        qrcode = message.getUrl();


                        Log.i("qrcode", qrcode);
                        // JSONStoreAddOptions addoptions= new JSONStoreAddOptions();
                        //  addoptions.isMarkDirty();
                        try {
                            JSONObject qrcodejson = new JSONObject(qrcode);
                            movieListArray.put(qrcodejson);
                            MovieItems movieItem = new MovieItems();
                            movieItem.setName(qrcodejson.getString("Movie"));
                            movieItem.setNumberOfTickets(qrcodejson.getString("Ticket"));
                            movieItem.setDate(qrcodejson.getString("Date"));
                            urlBuilder.append("https://api.qrserver.com/v1/create-qr-code/?size=175x175&data=");
                            urlBuilder.append("Movie:").append(movieItem.getName()).append("\n");
                            urlBuilder.append("Tickets:").append(movieItem.getNumberOfTickets()).append("\n");
                            urlBuilder.append("Date:").append(movieItem.getDate()).append("\n");
                            movieItem.setUrl(urlBuilder.toString());

                            MovieManager.getInstance().addResults(movieItem);
                            Log.d("JSONARRAY", movieListArray.toString());
                            sp.edit().putString(PREF_MOVIE_LIST, movieListArray.toString()).commit();
                        } catch (Exception e) {
                            System.out.println("Exception is " + e.getMessage());
                        }
                    }
                    String url=message.getUrl().toString();
                    Log.i("url",url);
                        if((url).equals("a")) {
                            title = "Your OTP : ";
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    new android.app.AlertDialog.Builder(MainActivity.this)
                                            .setTitle(title)
                                            .setMessage(store)
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            })
                                            .show();
                                }

                            });
                        }
                        else{
                            title = "Movie Booked : ";
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    new android.app.AlertDialog.Builder(MainActivity.this)
                                            .setTitle(title)
                                            .setMessage(store)
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            })
                                            .show();
                                }

                            });

                        }
                    }else {
                   store = message.getPayload();
                   // code to open browser OR create a new activity
                   Log.i(TAG, "Received a Store Notification: " + store);
               }



            }

        };



    }


    private void loadFromCache(){

        SharedPreferences sp = getSharedPreferences(MovieManager.getInstance().getUserId()+PREFS_MOVIE_MASTER, Context.MODE_PRIVATE);
        String sc = sp.getString(PREF_MOVIE_LIST, null);
        try {
            if (sc == null) {
                movieListArray = new JSONArray();
            } else {
                movieListArray = new JSONArray(sc);

            }

            int length =movieListArray.length()-1;
            MovieManager.getInstance().getResults().clear();
            for(int i=length;i>=0;i--){
                JSONObject jsonObject = movieListArray.getJSONObject(i);
                MovieItems movieItem = new MovieItems();
                movieItem.setName(jsonObject.getString("Movie"));
                movieItem.setNumberOfTickets(jsonObject.getString("Ticket"));
                movieItem.setDate(jsonObject.getString("Date"));
                urlBuilder.append("https://api.qrserver.com/v1/create-qr-code/?size=175x175&data=");
                urlBuilder.append("Movie:").append(movieItem.getName()).append("\n");
                urlBuilder.append("Tickets:").append(movieItem.getNumberOfTickets()).append("\n");
                urlBuilder.append("Date:").append(movieItem.getDate()).append("\n");
                movieItem.setUrl(urlBuilder.toString());

                //   movieItem.setUrl(jsonObject.getString("Url"));
                MovieManager.getInstance().addResults(movieItem);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Called when the register device button is pressed.
     * Attempts to register the device with your push service on Bluemix.
     * If successful, the push client sdk begins listening to the notification listener.
     * Also includes the example option of UserID association with the registration for very targeted Push notifications.
     *
     * @param view the button pressed
     */
    public void registerDevice(View view) {

        // Checks for null in case registration has failed previously
        if(push==null){
            push = MFPPush.getInstance();
        }


        // Make register button unclickable during registration and show registering text
        TextView userId = findViewById(R.id.user);
        MovieManager.getInstance().setUserId(userId.getText().toString());
        sp = getSharedPreferences(MovieManager.getInstance().getUserId()+PREFS_MOVIE_MASTER, Context.MODE_PRIVATE);
        loadFromCache();
        TextView buttonText = (TextView) findViewById(R.id.button_text);
     //   buttonText.setClickable(false);

        TextView responseText = (TextView) findViewById(R.id.response_text);
        buttonText.setText(R.string.Registering);
        Log.i(TAG, "Registering for notifications");

        // Creates response listener to handle the response when a device is registered.
        MFPPushResponseListener registrationResponselistener = new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String response) {
                // Split response and convert to JSON object to display User ID confirmation from the backend
                String[] resp = response.split("Text: ");
                try {
                    JSONObject responseJSON = new JSONObject(resp[1]);
                    setStatus(" " + responseJSON.getString("userId"), true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "Successfully registered for push notifications, " + response);
                // Start listening to notification listener now that registration has succeeded
                push.listen(notificationListener);
                Intent i = new Intent(MainActivity.this, BookingHistoryActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                is.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            @Override
            public void onFailure(MFPPushException exception) {
                String errLog = "Error registering for push notifications: ";
                String errMessage = exception.getErrorMessage();
                int statusCode = exception.getStatusCode();

                // Set error log based on response code and error message
                if(statusCode == 401){
                    errLog += "Cannot authenticate successfully with Bluemix Push instance, ensure your CLIENT SECRET was set correctly.";
                } else if(statusCode == 404 && errMessage.contains("Push GCM Configuration")){
                    errLog += "Push GCM Configuration does not exist, ensure you have configured GCM Push credentials on your Bluemix Push dashboard correctly.";
                } else if(statusCode == 404 && errMessage.contains("PushApplication")){
                    errLog += "Cannot find Bluemix Push instance, ensure your APPLICATION ID was set correctly and your phone can successfully connect to the internet.";
                } else if(statusCode >= 500){
                    errLog += "Bluemix and/or your Push instance seem to be having problems, please try again later.";
                }

                setStatus(errLog, false);
                Log.e(TAG,errLog);
                // make push null since registration failed
                push = null;
            }
        };

        // Attempt to register device using response listener created above
        // Include unique sample user Id instead of Sample UserId in order to send targeted push notifications to specific users

        push.registerDeviceWithUserId(userId.getText().toString(),registrationResponselistener);
    }



    // Capture button clicks


    // If the device has been registered previously, hold push notifications when the app is paused
    @Override
    protected void onPause() {
        super.onPause();

        if (push != null) {
            push.hold();
        }
    }

    // If the device has been registered previously, ensure the client sdk is still using the notification listener from onCreate when app is resumed
    @Override
    protected void onResume() {
        super.onResume();
        if (push != null) {
            push.listen(notificationListener);
        }
    }

    /**
     * Manipulates text fields in the UI based on initialization and registration events
     * @param messageText String main text view
     * @param wasSuccessful Boolean dictates top 2 text view texts
     */
    private void setStatus(final String messageText, boolean wasSuccessful){
        final TextView responseText = (TextView) findViewById(R.id.response_text);
      //  final TextView topText = (TextView) findViewById(R.id.top_text);
        final TextView bottomText = (TextView) findViewById(R.id.bottom_text);
        final TextView buttonText = (TextView) findViewById(R.id.button_text);
        final TextView movieAssistant = (TextView) findViewById(R.id.top_text1);
        final TextView user_ID = (TextView) findViewById(R.id.user);
      //  final Button hist = (Button) findViewById(R.id.Click);
        final ImageView img = (ImageView) findViewById(R.id.imageView1);

        final String topStatus = wasSuccessful ? "Welcome " : "Failed!";
        final String bottomStatus = wasSuccessful ? " " : " ";


    }


}

