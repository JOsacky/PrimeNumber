package com.osacky.factor;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ParseObject.registerSubclass(TheNumber.class);
        Parse.initialize(this, "7A5hnUGC6Zxg6OQhZjWnB2gHAYc7h2VOOxYcVlMg", "L6tf0XRvzq6xSqbmf8EkMzJxqIfMMUqP5J9pljMC");
        ParseAnalytics.trackAppOpened(getIntent());

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                ParseQuery<ParseObject> query = ParseQuery.getQuery("TheNumber");
                query.whereGreaterThan("threads", 0);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> primeList, ParseException e) {
                        if (e == null) {
                            Log.d("score", "Retrieved " + primeList.size() + " scores");
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                        //IsPrime isPrime = new IsPrime();
                        if(primeList.isEmpty())
                        {
                            int primeNumber = (int)((Math.random()+.2)*100000);
                            ParseObject blah = new ParseObject("TheNumber");
                            blah.put("number", primeNumber);
                            blah.put("threads", 3);
                            blah.saveInBackground();
                            IsPrime.isPrime(primeNumber, 2, (int)Math.sqrt(primeNumber/4));
                        }
                        else
                        {
                            int primeNumber = primeList.get(0).getInt("number");
                            int threads = 4-primeList.get(0).getInt("threads");
                            primeList.get(0).increment("threads", -1);
                            IsPrime.isPrime(primeNumber, (int)Math.sqrt(primeNumber)*threads/4, (int)Math.sqrt(primeNumber)*(threads+1)/4);
                            primeList.get(0).saveInBackground();
                            if(primeList.get(0).getInt("threads")==0)
                            {
                                primeList.get(0).deleteInBackground();
                            }
                        }
                    }
                });
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
