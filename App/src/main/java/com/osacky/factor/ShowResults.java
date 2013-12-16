package com.osacky.factor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

        //Query our database to find factors of the number
        ParseQueryAdapter.QueryFactory<ParseObject> factory =
                new ParseQueryAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery create() {
                        ParseQuery query = new ParseQuery(getString(R.string.parse_factors));
                        query.whereEqualTo(getString(R.string.parse_factors_key), number);
                        query.orderByAscending(getString(R.string.parse_factors_value));
                        return query;
                    }
                };

        //Pass the factory into the ParseQueryAdapter's constructor.
        ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(this, factory);
        adapter.setTextKey(getString(R.string.parse_factors_value));
        ListView listView = (ListView) findViewById(R.id.listNumbers);

        //Populate the ListView with factors from the adapter
        listView.setAdapter(adapter);

    }
}
