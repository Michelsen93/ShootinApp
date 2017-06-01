package com.example.ole_martin.shootinapp.activity;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ole_martin.shootinapp.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Created by ole-martin on 01.06.2017.
 */

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    String[] items;
    Map<Integer, Map<String, Object>> scoreCardProperties;
    public Adapter(Context context, String[] items, Map<Integer, Map<String, Object>> scoreCardProperties){
        this.context = context;
        this.items = items;
        this.scoreCardProperties = scoreCardProperties;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.custom_row, parent, false);
        Item item = new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((Item)holder).textView.setText(items[position]);
        ((Item) holder).button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ArrayList<String> resultList = new ArrayList<String>();
                Map<String, Object> card = scoreCardProperties.get(position);
                ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) card.get("results");
                for(Map<String, Object> result : results){
                    String standplass = (String) result.get("standplass");
                    int hits = (int) result.get("hits");
                    int figures = (int) result.get("figures");
                    int bullseyes = (int) result.get("bullseyes");
                    String present = standplass + " : " + hits + " | " + figures + " | " + bullseyes;
                    resultList.add(present);
                }
                ListView listView = new ListView(context);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, resultList);
                listView.setAdapter(adapter);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setNegativeButton("Lukk", null);
                builder.setView(listView);
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class Item extends RecyclerView.ViewHolder {
        TextView textView;
        Button button;
        public Item(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.item);
            button = (Button) itemView.findViewById(R.id.recbutton);

        }
    }
}
