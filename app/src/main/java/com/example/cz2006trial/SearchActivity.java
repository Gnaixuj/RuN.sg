package com.example.cz2006trial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ListView listLoc;
    SearchView searchLoc;
    ArrayAdapter<String> adapter;

    ArrayList<String> accessStr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map_search);

        listLoc = (ListView) findViewById(R.id.listLoc);
        
        searchLoc = (SearchView) findViewById(R.id.searchBar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             accessStr = (ArrayList<String>) getIntent().getStringArrayListExtra("key");
        }

        if (accessStr!= null) {
            adapter = new ArrayAdapter<String>(
                    SearchActivity.this,
                    android.R.layout.simple_list_item_1,
                    accessStr
            );
        }

        listLoc.setAdapter(adapter);

        searchLoc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        listLoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String loc = ((TextView)view).getText().toString();
                Intent intent = new Intent();
                intent.putExtra("locationkey", loc);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
