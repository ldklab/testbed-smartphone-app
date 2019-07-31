package edu.wpi.messagebrokersmartphoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import org.json.*;
import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotificationActivity extends AppCompatActivity {

    private ListView lv;
    List<InteractionResponse> interactionResponses;
    String interactionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        // ---------- NETWORK CONFIGURATION ----------
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
        }
        // ---------- END NETWORK CONFIGURATION ----------


        //Retrieving data from the notification intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String description = intent.getStringExtra("DESCRIPTION");
        String interactionData = intent.getStringExtra("INTERACTION");
        interactionID = intent.getStringExtra("INTERACTION_ID");


        List<String> instructions = getInstructions(interactionData);
        String completeInstructions = "\n\n";
        if(instructions != null) {
            int ii = 1;
            for (String e : instructions) {
                completeInstructions += ii + ". " + e + '\n';
                ii++;
            }
        }


        // Setting Title and Content in the view
        TextView titleTextView = findViewById(R.id.title); // Title
        titleTextView.setText(title);
        TextView contentTextView = findViewById(R.id.content); // Content
        contentTextView.setText(description + completeInstructions);



                //Let's (try) to retrieve all the data
        String specific; //ToDo: implement
        String[] insructions; //ToDo: implement
        List<InteractionInput> inputs = processInput(interactionData); // Input retrieved

        interactionResponses = new ArrayList<>();
        LinearLayout contentLayout = findViewById(R.id.content_layout);

        for(int i = 0; i < inputs.size(); i++) {
            final InteractionInput input = inputs.get(i);
            final InteractionResponse IR = new InteractionResponse(input.getName());
            interactionResponses.add(IR);

            LinearLayout myLayout = new LinearLayout(this); // Creating horizontal layout to hold Text field and input box
            myLayout.setOrientation(LinearLayout.HORIZONTAL);
            myLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView textView = new TextView(this);
            textView.setText(input.getTitle());
            myLayout.addView(textView);
            contentLayout.addView(myLayout);

            if((input.getType().compareTo("text") == 0) || (input.getType().compareTo("textarea") == 0)) {

                final EditText editText = new EditText(this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                editText.setHint(input.getTitle()); // Setting placeholder

                editText.addTextChangedListener(new TextWatcher() { //Binding changes to view
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void afterTextChanged(Editable editable) { IR.setValue(editable.toString()); }
                });

                myLayout.addView(editText); //Adding field to layout

            //}else if(input.getType().compareTo("textarea") == 0) { //ToDo: implement textarea

            }else if(input.getType().compareTo("checkbox") == 0) {
                CheckBox CB = new CheckBox(this);
                CB.setText(input.getTitle());
                IR.setValue("false"); // Initial value

                CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Binding changes to view
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) { IR.setValue(Boolean.toString(b)); }
                });

                myLayout.addView(CB); //Adding field to layout

            }else if(input.getType().compareTo("select") == 0) {
                Spinner spinner = new Spinner(this);


                IR.setValue(input.getElements().get(0).getValue()); //Default value = first element

                List<String> strings = new ArrayList<String>(input.getElements().size());
                for (InteractionInput.Element element : input.getElements()) {
                    strings.add(element.getText());
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, strings);
                spinner.setAdapter(spinnerArrayAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { IR.setValue(input.getElements().get(i).getValue()); }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });

                myLayout.addView(spinner); //Adding field to layout

            }else if(input.getType().compareTo("button") == 0) {
                Button btn = new Button(this);
                btn.setText(inputs.get(i).getTitle());

                btn.setOnClickListener(new View.OnClickListener() { //Binding changes to view
                    @Override
                    public void onClick(View view) { IR.setValue("true"); }
                });
                myLayout.addView(btn); //Adding field to layout
            }
        } // End for "inputs"
    }


    public void sendButton(View view){
        System.out.println("Send clicked");
        System.out.println(interactionResponses);

        //Retrieve API_URL
        Context ctx = getApplicationContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String BASE_API_RUL = sharedPref.getString(getString(R.string.API_URL), "");
        String API_URL = BASE_API_RUL + "/interactions/" + interactionID;

        System.out.println("Sending data to API_URL: " + API_URL);
        jsonPUT(API_URL, interactionResponses);

        finish();
    }


    protected String jsonPUT(String API_URL, List<InteractionResponse> postDataList) {

        // ---------- Creating JSON ----------
        JSONArray jsonArray = new JSONArray();
        for(InteractionResponse IR : postDataList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", IR.getName());
                jsonObject.put("value", IR.getValue());
            }catch(Exception e){
                System.out.println("Error happened!");
            }

            jsonArray.put(jsonObject);
        }
        System.out.println("Real data: " + jsonArray.toString());
        JSONObject finalObject = new JSONObject();
        try{
            finalObject.put("data", jsonArray);
        }catch(Exception e){
            System.out.println("Error happened!");
        }
        // ---------- END Creating JSON ----------



        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap // Instantiate the cache
        Network network = new BasicNetwork(new HurlStack()); // Set up the network to use HttpURLConnection as the HTTP client.
        requestQueue = new RequestQueue(cache, network); // Instantiate the RequestQueue with the cache and network.
        requestQueue.start(); // Start the queue



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, API_URL, finalObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        requestQueue.add(jsonObjectRequest);

        return null;
    }


    private List<InteractionInput> processInput(String interactionData) {
        List<InteractionInput> inputs = new ArrayList<>();

        try {
            JSONObject interactionDataJson = new JSONObject(interactionData);
            JSONArray jsonInputs = interactionDataJson.getJSONArray("inputs");

            List<InteractionInput.Element> inputElementsList = new ArrayList<>();
            for(int i = 0; i < jsonInputs.length(); i++){
                JSONObject jsonInput = jsonInputs.getJSONObject(i);
                String inputTitle = jsonInput.has("title") ? jsonInput.getString("title") : null;
                String inputName = jsonInput.has("name") ? jsonInput.getString("name") : null;
                String inputType = jsonInput.has("type") ? jsonInput.getString("type") : null;
                Boolean required = jsonInput.has("required") ? jsonInput.getBoolean("required") : null;

                InteractionInput in = new InteractionInput(inputTitle, inputName, inputType, inputElementsList, required);


                JSONArray inputElementsJson = jsonInput.getJSONArray("elements");
                for(int j = 0; j < inputElementsJson.length(); j++){
                    JSONObject singleElement = inputElementsJson.getJSONObject(j);

                    InteractionInput.Element e = in.new Element(singleElement.getString("text"), singleElement.getString("value"));
                    inputElementsList.add(e);
                }


                inputs.add(in);
                //System.out.println("Input added: " + in.toString());
            }

            return inputs;
            //arrayAdapter.notifyDataSetChanged();
        }catch(Exception e) {
            Log.d("MyDebugError", e.toString());
            //System.out.println(e);
        }

        return null;
    }

    private List<String> getInstructions(String interactionData){
        List<String> instructions = new ArrayList<>();

        try {
            JSONObject interactionDataJson = new JSONObject(interactionData);
            JSONArray jsonSingleString = interactionDataJson.getJSONArray("instructions");

            if(jsonSingleString.length() == 0){
                return null;
            }

            for(int i = 0; i < jsonSingleString.length(); i++){
                instructions.add(jsonSingleString.optString(i));
            }

            return instructions;
            //arrayAdapter.notifyDataSetChanged();
        }catch(Exception e) {
            Log.d("MyDebugError", e.toString());
            //System.out.println(e);
        }

        return null;
    }
}
