package com.nuaa.safedriving;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


public class Home extends Fragment {

    private TextView origin,destination,time;
    private ImageView exchange;

    public Home() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home,container,false);
        origin = (TextView)view.findViewById(R.id.origin);
        time = (TextView)view.findViewById(R.id.time);
        destination = (TextView)view.findViewById(R.id.destination);
        exchange = (ImageView)view.findViewById(R.id.exchange);

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin_content = origin.getText().toString().trim();
                String destination_content = destination.getText().toString().trim();
                String temp = destination_content;
                destination_content = origin_content;
                origin_content = temp;
                origin.setText(origin_content);
                destination.setText(destination_content);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

}
