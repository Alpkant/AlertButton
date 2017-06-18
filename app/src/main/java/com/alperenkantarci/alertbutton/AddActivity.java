package com.alperenkantarci.alertbutton;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    EditText name, surname, phoneNumber, email;
    Spinner country_codes;
    Button add_button;
    List<TrustyPerson> trustedPeople;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_person);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        /**
         * This can produce NULLException
         */

        name = (EditText) findViewById(R.id.name_edit_text);
        surname = (EditText) findViewById(R.id.surname_edit_text);
        phoneNumber = (EditText) findViewById(R.id.phone_edit_text);
        email = (EditText) findViewById(R.id.email_edit_text);
        country_codes = (Spinner) findViewById(R.id.country_code);
        add_button = (Button) findViewById(R.id.add_button);
        trustedPeople = new ArrayList<>();

        /**
         * This part retrieve all saved persons from shared preferences.
         */

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        int numberOfPeople = preferences.getInt("Number", 0);
        for (int i = 0; i < numberOfPeople; i++) {
            String name = preferences.getString(String.valueOf(i) + " name", "");
            String surname = preferences.getString(String.valueOf(i) + " surname", "");
            String countryCode = preferences.getString(String.valueOf(i) + " country", "");
            String phoneNumber = preferences.getString(String.valueOf(i) + " number", "");
            String email = preferences.getString(String.valueOf(i) + " email", "");
            trustedPeople.add(new TrustyPerson(name, surname, countryCode, phoneNumber, email));
        }
        editor.apply();

        /**
         * Array adapter setting.
         */
        String[] m_Codes = getResources().getStringArray(R.array.CountryCodes);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, m_Codes);
        country_codes.setAdapter(spinnerAdapter);


        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (name.getText().toString().equals("")) {
                    Toast.makeText(AddActivity.this, "Name can't be empty.Please enter a name.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (surname.getText().toString().equals("")) {
                    Toast.makeText(AddActivity.this, "Surname can't be empty.Please enter a surname.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (phoneNumber.getText().toString().equals("") && email.getText().toString().equals("")) {
                    Toast.makeText(AddActivity.this, "Either email or phone number should be given in order to add a person.", Toast.LENGTH_SHORT).show();
                    return;
                }

                TrustyPerson newPerson = new TrustyPerson();
                newPerson.setName(name.getText().toString());
                newPerson.setSurname(surname.getText().toString());
                newPerson.setCountry_code(country_codes.getSelectedItem().toString());
                newPerson.setTelephone_number(phoneNumber.getText().toString());
                newPerson.setEmail(email.getText().toString());
                trustedPeople.add(newPerson);


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("Number", trustedPeople.size());
                int i = trustedPeople.size() - 1;
                editor.putString(String.valueOf(i) + " name", trustedPeople.get(i).getName());
                editor.putString(String.valueOf(i) + " surname", trustedPeople.get(i).getSurname());
                editor.putString(String.valueOf(i) + " country", trustedPeople.get(i).getCountry_code());
                editor.putString(String.valueOf(i) + " number", trustedPeople.get(i).getTelephone_number());
                editor.putString(String.valueOf(i) + " email", trustedPeople.get(i).getEmail());
                editor.apply();


                Toast.makeText(AddActivity.this, "Person successfully added to your list.\n" +
                        "If you want to add new person fill the field.", Toast.LENGTH_LONG).show();
                name.setText("");
                surname.setText("");
                phoneNumber.setText("");
                email.setText("");


            }
        });


    }


}
