package com.alperenkantarci.alertbutton;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static java.lang.reflect.Array.getInt;


/**
 * Created by Alperen Kantarci on 16.06.2017.
 */

public class ViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<TrustyPerson> mTrustedPeople;

    public ViewAdapter(Activity activity, List<TrustyPerson> trustedPeople) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTrustedPeople = trustedPeople;
    }


    @Override
    public int getCount() {
        return mTrustedPeople.size();
    }

    @Override
    public Object getItem(int i) {
        return mTrustedPeople.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final View rowView;
        rowView = mInflater.inflate(R.layout.list_view_row, null);
        final TextView textView1 =
                (TextView) rowView.findViewById(R.id.textView1);
        TextView textView2 =
                (TextView) rowView.findViewById(R.id.textView2);

        ImageView deleteImage = (ImageView) rowView.findViewById(R.id.delete_image);


        final TrustyPerson tmpPerson = mTrustedPeople.get(i);

        textView1.setText(tmpPerson.getName() + " " + tmpPerson.getSurname());
        String[] tmp = tmpPerson.getCountry_code().split(","); //Gets only phone part
        textView2.setText(tmp[0] + "" + tmpPerson.getTelephone_number());

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrustedPeople.remove(i);
                notifyDataSetChanged();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                int numberOfPeople = mTrustedPeople.size();
                editor.putInt("Number", numberOfPeople);
                for (int i = 0; i < numberOfPeople; i++) {
                    editor.putString(String.valueOf(i) + " name", mTrustedPeople.get(i).getName());
                    editor.putString(String.valueOf(i) + " surname", mTrustedPeople.get(i).getSurname());
                    editor.putString(String.valueOf(i) + " country", mTrustedPeople.get(i).getCountry_code());
                    editor.putString(String.valueOf(i) + " number", mTrustedPeople.get(i).getTelephone_number());
                    editor.putString(String.valueOf(i) + " email", mTrustedPeople.get(i).getEmail());
                }
                editor.apply();

            }
        });

        return rowView;
    }
}
