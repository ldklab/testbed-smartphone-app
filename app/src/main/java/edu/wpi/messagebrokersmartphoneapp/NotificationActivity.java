package edu.wpi.messagebrokersmartphoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONObject;

import org.json.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotificationActivity extends AppCompatActivity {

    private ListView lv;
    List<InteractionResponse> interactionResponses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //Retrieving data from the notification intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String description = intent.getStringExtra("DESCRIPTION");
        String interactionData = intent.getStringExtra("INTERACTION");

        // Setting Title and Content in the view
        TextView titleTextView = findViewById(R.id.title); // Title
        titleTextView.setText(title);
        TextView contentTextView = findViewById(R.id.content); // Content
        contentTextView.setText(description);

        //Let's (try) to retrieve all the data
        String specific; //ToDo: implement
        String[] insructions; //ToDo: implement
        List<InteractionInput> inputs = processInput(interactionData); // Input retrieved

        interactionResponses = new ArrayList<>();
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.layout_notification_activity);

        for(int i = 0; i < inputs.size(); i++) {
            final InteractionInput input = inputs.get(i);
            final InteractionResponse IR = new InteractionResponse(input.getName());
            interactionResponses.add(IR);

            if((input.getType().compareTo("text") == 0) || (input.getType().compareTo("textarea") == 0)) {

                final EditText editText = new EditText(this);
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
}