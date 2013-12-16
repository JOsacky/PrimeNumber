package com.osacky.factor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class ShowResults extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);

        Intent intent = getIntent();
        final long number = intent.getLongExtra(getString(R.string.parse_factors_key), -1);

        //query our database to find factors of number
        ParseQueryAdapter.QueryFactory<ParseObject> factory =
                new ParseQueryAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery create() {
                        ParseQuery query = new ParseQuery(getString(R.string.parse_factors));
                        query.whereEqualTo(getString(R.string.parse_factors_key), number);
                        query.orderByAscending(getString(R.string.parse_factors_value));
                        return query;
                    }
                };

        // Pass the factory into the ParseQueryAdapter's constructor.
        ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(this, factory);
        adapter.setTextKey(getString(R.string.parse_factors_value));
        ListView listView = (ListView) findViewById(R.id.listNumbers);
        //populate listview with factors from the adapter
        listView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_show_results, container, false);
            return rootView;
        }
    }

}
