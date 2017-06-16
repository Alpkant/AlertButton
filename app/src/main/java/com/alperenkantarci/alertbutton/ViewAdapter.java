package com.alperenkantarci.alertbutton;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


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
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView;
        rowView = mInflater.inflate(R.layout.list_view_row, null);
        TextView textView1 =
                (TextView) rowView.findViewById(R.id.textView1);
        TextView textView2 =
                (TextView) rowView.findViewById(R.id.textView2);

        TrustyPerson tmpPerson = mTrustedPeople.get(i);

        textView1.setText(tmpPerson.getName() + " " + tmpPerson.getSurname());
        String[] tmp = tmpPerson.getCountry_code().split(","); //Gets only phone part
        textView2.setText(tmp[0] + "" + tmpPerson.getTelephone_number());
        return rowView;
    }
}
