package com.example.cz2006trial.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cz2006trial.controller.GoogleMapController;
import com.example.cz2006trial.R;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * This fragment is used to list all the access points based on the text inputted by user in the search bar
 */
public class SearchFragment extends Fragment {

    private ListView listLoc;
    private SearchView searchLoc;
    private ArrayAdapter<String> adapter;

    private ArrayList<Marker> accessPoint = new ArrayList<>();

    private GoogleMapController controller = GoogleMapController.getController();

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_map_search, container, false);

        listLoc = root.findViewById(R.id.listLoc);
        searchLoc = root.findViewById(R.id.searchBar);
        searchLoc.setQueryHint("Search for access points...");

        //accessPoint = controller.getMarkers();
        ArrayList<String> accessStr = new ArrayList<>();
        for (Marker marker : accessPoint) {
            accessStr.add(marker.getTitle());
            Log.i("marker title", marker.getTitle());
        }


        adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1,
                accessStr);

        listLoc.setAdapter(adapter);

        searchLoc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return true;
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
                for (Marker marker : accessPoint) {
                    if (marker.getTitle().equals(loc)) {
                        //controller.setPointChosen(marker);
                        break;
                    }
                }
            }
        });

        return root;
    }
}
