package com.alperenkantarci.alertbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    List<TrustyPerson> trustedPeople ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_person);

        name = (EditText) findViewById(R.id.name_edit_text);
        surname = (EditText) findViewById(R.id.surname_edit_text);
        phoneNumber = (EditText) findViewById(R.id.phone_edit_text);
        email = (EditText) findViewById(R.id.email_edit_text);
        country_codes = (Spinner) findViewById(R.id.country_code);
        add_button = (Button) findViewById(R.id.add_button);
        trustedPeople = new ArrayList<>();

        String[] m_Codes= getResources().getStringArray(R.array.CountryCodes);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,m_Codes);
        country_codes.setAdapter(spinnerAdapter);


        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TrustyPerson newPerson = new TrustyPerson();
                newPerson.setName(name.getText().toString());
                newPerson.setSurname(surname.getText().toString());
                newPerson.setCountry_code(country_codes.getSelectedItem().toString());
                newPerson.setTelephone_number(phoneNumber.getText().toString());
                newPerson.setEmail(email.getText().toString());
                trustedPeople.add(newPerson);
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
