package com.alperenkantarci.alertbutton;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    EditText name, surname, phoneNumber, email;
    Spinner country_codes;
    Button add_button;

    private static final String[] m_Codes = {
            "376",
            "971",
            "93",
            "355",
            "374",
            "599",
            "244",
            "672",
            "54",
            "43",
            "61",
            "297",
            "994",
            "387",
            "880",
            "32",
            "226",
            "359",
            "973",
            "257",
            "229",
            "590",
            "673",
            "591",
            "55",
            "975",
            "267",
            "375",
            "501",
            "1",
            "61",
            "243",
            "236",
            "242",
            "41",
            "225",
            "682",
            "56",
            "237",
            "86",
            "57",
            "506",
            "53",
            "238",
            "61",
            "357",
            "420",
            "49",
            "253",
            "45",
            "213",
            "593",
            "372",
            "20",
            "291",
            "34",
            "251",
            "358",
            "679",
            "500",
            "691",
            "298",
            "33",
            "241",
            "44",
            "995",
            "233",
            "350",
            "299",
            "220",
            "224",
            "240",
            "30",
            "502",
            "245",
            "592",
            "852",
            "504",
            "385",
            "509",
            "36",
            "62",
            "353",
            "972",
            "44",
            "91",
            "964",
            "98",
            "39",
            "962",
            "81",
            "254",
            "996",
            "855",
            "686",
            "269",
            "850",
            "82",
            "965",
            "7",
            "856",
            "961",
            "423",
            "94",
            "231",
            "266",
            "370",
            "352",
            "371",
            "218",
            "212",
            "377",
            "373",
            "382",
            "261",
            "692",
            "389",
            "223",
            "95",
            "976",
            "853",
            "222",
            "356",
            "230",
            "960",
            "265",
            "52",
            "60",
            "258",
            "264",
            "687",
            "227",
            "234",
            "505",
            "31",
            "47",
            "977",
            "674",
            "683",
            "64",
            "968",
            "507",
            "51",
            "689",
            "675",
            "63",
            "92",
            "48",
            "508",
            "870",
            "1",
            "351",
            "680",
            "595",
            "974",
            "40",
            "381",
            "7",
            "250",
            "966",
            "677",
            "248",
            "249",
            "46",
            "65",
            "290",
            "386",
            "421",
            "232",
            "378",
            "221",
            "252",
            "597",
            "239",
            "503",
            "963",
            "268",
            "235",
            "228",
            "66",
            "992",
            "690",
            "670",
            "993",
            "216",
            "676",
            "90",
            "688",
            "886",
            "255",
            "380",
            "256",
            "1",
            "598",
            "998",
            "39",
            "58",
            "84",
            "678",
            "681",
            "685",
            "967",
            "262",
            "27",
            "260",
            "263"
    };

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
                Log.i("YENI ADAM",newPerson.getCountry_code());

            }
        });



    }

}
