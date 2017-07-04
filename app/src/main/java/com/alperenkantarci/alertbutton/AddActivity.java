package com.alperenkantarci.alertbutton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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

import static android.R.attr.data;
import static android.R.attr.id;


public class AddActivity extends AppCompatActivity {


    static final int PICK_CONTACT = 1;
    String firstName, family, display;
    EditText name, surname, phoneNumber, email;
    Spinner country_codes;
    Button add_button;
    Button add_from_contacts_button;
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
        add_from_contacts_button = (Button) findViewById(R.id.add_from_contact_button);
        trustedPeople = new ArrayList<>();

        /**
         * This part retrieve all saved persons from shared preferences.
         */

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("com.alperenkantarci.alertbutton", MODE_MULTI_PROCESS);
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


                SharedPreferences preferences = getApplicationContext().getSharedPreferences("com.alperenkantarci.alertbutton", MODE_MULTI_PROCESS);
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

        add_from_contacts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
                Log.e("ADDD", "ASDFDSA");
            }

        });


    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri resultUri = data.getData();
                    Cursor cont = getContentResolver().query(resultUri, null, null, null, null);
                    if (!cont.moveToNext()) {
                        Toast.makeText(this, "Cursor contains no data", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int columnIndexForId = cont.getColumnIndex(ContactsContract.Contacts._ID);
                    String contactId = cont.getString(columnIndexForId);

                    // Fetch contact name with a specific ID
                    String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = " + contactId;
                    String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                    Cursor nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
                    while (nameCur.moveToNext()) {
                        firstName = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                        family = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                        display = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));

                        name.setText(firstName);
                        surname.setText(family);



                    // TODO(1): Get phone number from contact list and write into edittext
                    if (Integer.parseInt(nameCur.getString(nameCur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        Cursor phoneCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                        while (phoneCur.moveToNext()) {
                            String phoneNumberCursor = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneNumber.setText(phoneNumberCursor);
                        }
                        phoneCur.close();

                        cont.close();

                       }
                    }
                    nameCur.close();
                    break;
                }
            }
        }
    }
