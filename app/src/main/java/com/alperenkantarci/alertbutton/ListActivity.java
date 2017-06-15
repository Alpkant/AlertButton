package com.alperenkantarci.alertbutton;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    List<TrustyPerson> trustedPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_person);

        trustedPeople = new ArrayList<>();

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int numberOfPeople = preferences.getInt("Number",0);
        for ( int i=0 ; i < numberOfPeople ; i++){
            String name= preferences.getString(String.valueOf(i)+" name","");
            String surname= preferences.getString(String.valueOf(i)+" surname","");
            String countryCode= preferences.getString(String.valueOf(i)+" country","");
            String phoneNumber= preferences.getString(String.valueOf(i)+" number","");
            String email= preferences.getString(String.valueOf(i)+" email","");
            trustedPeople.add(new TrustyPerson(name,surname,countryCode,phoneNumber,email));
        }
        editor.apply();

    }

}
